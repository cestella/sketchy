/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.caseystella.sketchy.sketches.statistics.distribution.sketchimpl;

import com.caseystella.sketchy.sketches.statistics.distribution.DistributionSketch;
import com.caseystella.sketchy.sketches.statistics.distribution.types.number.NumberType;
import com.caseystella.sketchy.sketches.statistics.distribution.types.sketch.SketchType;
import com.caseystella.stellar.common.utils.SerDeUtils;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.math3.util.FastMath;

/**
 * A (near) constant memory implementation of a statistics provider.
 * For first order statistics, simple terms are stored and composed
 * to return the statistics results.  This is intended to provide a
 * mergeable implementation for a statistics provider.
 */
public abstract class DistributionSketchImpl<T extends Number, S> implements DistributionSketch<T>,
    Serializable {
  private static final long serialVersionUID = 1L;
  transient protected SketchType<S> sketchType;
  transient protected NumberType<T> numberType;
  protected S sketch;
  protected long n = 0;
  protected T sum;
  protected T sumOfSquares;
  protected double sumOfLogs = 0;
  protected T min;
  protected T max;

  //\mu_1, E[X]
  protected double M1 = 0;
  //\mu_2: E[(X - \mu)^2]
  protected double M2 = 0;
  //\mu_3: E[(X - \mu)^3]
  protected double M3 = 0;
  //\mu_4: E[(X - \mu)^4]
  protected double M4 = 0;
  //almost sensible default k
  int k = 128;

  public DistributionSketchImpl() {
    this(128);
  }

  public DistributionSketchImpl(int k) {
    this.k = k;
    this.numberType = createNumberType();
    this.sketchType = createSketchType(k);
    sketch = sketchType.createSketch();
    sum = numberType.zero();
    sumOfSquares = numberType.zero();
    min = numberType.maxValue();
    max = numberType.minValue();
  }

  abstract protected NumberType<T> createNumberType();
  abstract protected SketchType<S> createSketchType(int k);
  abstract protected DistributionSketchImpl<T, S> createNew();

  private void copyFrom(DistributionSketchImpl<T, S> s) {
    this.sketchType = s.sketchType;
    this.numberType = s.numberType;
    this.sketch = s.sketch;
    this.n = s.n;
    this.sum = s.sum;
    this.sumOfSquares = s.sumOfSquares;
    this.sumOfLogs = s.sumOfLogs;
    this.min = s.min;
    this.max = s.max;
    this.M1 = s.M1;
    this.M2 = s.M2;
    this.M3 = s.M3;
    this.M4 = s.M4;
    this.k = s.k;
  }

  public int getK() {
    return k;
  }
  /**
   * Add a value.
   * NOTE: This does not store the point, but only updates internal state.
   * NOTE: This is NOT threadsafe.
   * @param value
   */
  @Override
  public void addValue(T value) {
    long n1 = n;
    min = min == null?value:numberType.min(min, value);
    max = max == null?value:numberType.max(max, value);
    sum = numberType.add(sum, value);
    sumOfLogs += Math.log(value.doubleValue());
    sumOfSquares = numberType.add(sumOfSquares, numberType.multiply(value, value));
    sketchType.addValue(sketch, value);
    n++;
    double delta, delta_n, delta_n2, term1;
    //delta between the value and the mean
    delta = value.doubleValue() - M1;
    //(x - E[x])/n
    delta_n = delta / n;
    delta_n2 = delta_n * delta_n;
    term1 = delta * delta_n * n1;

    // Adjusting expected value: See Knuth TAOCP vol 2, 3rd edition, page 232
    M1 += delta_n;
    // Adjusting the \mu_i, see http://www.johndcook.com/blog/skewness_kurtosis/
    M4 += term1 * delta_n2 * (n*n - 3*n + 3) + 6 * delta_n2 * M2 - 4 * delta_n * M3;
    M3 += term1 * delta_n * (n - 2) - 3 * delta_n * M2;
    M2 += term1;
    //checkFlowError(sumOfSquares, sum, sumOfSquares, M1, M2, M3, M4);
  }

  private void checkFlowError(double sumOfSquares, double sum, double... vals) {
    //overflow
    for(double val : vals) {
      if(Double.isInfinite(val)) {
        throw new IllegalStateException("Double overflow!");
      }
    }
    //underflow.  It is sufficient to check sumOfSquares because sumOfSquares is going to converge to 0 faster than sum
    //in the situation where we're looking at an underflow.
    if(sumOfSquares == 0.0 && sum > 0) {
      throw new IllegalStateException("Double underflow!");
    }
  }

  @Override
  public long getCount() {
    return n;
  }

  @Override
  public Optional<T> getMin() {
    return Objects.equals(min, numberType.maxValue()) ?Optional.empty():Optional.ofNullable(min);
  }

  @Override
  public Optional<T> getMax() {
    return Objects.equals(max, numberType.minValue()) ?Optional.empty():Optional.ofNullable(max);
  }

  @Override
  public double getMean() {
    return getSum().doubleValue()/getCount();
  }

  @Override
  public T getSum() {
    return sum;
  }

  @Override
  public double getVariance() {
    return M2/(n - 1.0);
  }

  @Override
  public double getStandardDeviation() {
    return FastMath.sqrt(getVariance());
  }

  @Override
  public double getGeometricMean() {
    throw new UnsupportedOperationException("Unwilling to compute the geometric mean.");
  }

  @Override
  public double getPopulationVariance() {
    throw new UnsupportedOperationException("Unwilling to compute the geometric mean.");
  }

  @Override
  public double getQuadraticMean() {
    return FastMath.sqrt(sumOfSquares.doubleValue()/n);
  }

  @Override
  public double getSumLogs() {
    return sumOfLogs;
  }

  @Override
  public T getSumSquares() {
    return sumOfSquares;
  }

  /**
   * Unbiased kurtosis.
   * See http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math4/stat/descriptive/moment/Kurtosis.html
   * @return unbiased kurtosis
   */
  @Override
  public double getKurtosis() {
    //kurtosis = { [n(n+1) / (n -1)(n - 2)(n-3)] \mu_4 / std^4 } - [3(n-1)^2 / (n-2)(n-3)]
    if(n < 4) {
      return Double.NaN;
    }
    double std = getStandardDeviation();
    double t1 = (1.0*n)*(n+1)/((n-1)*(n-2)*(n-3));
    double t3 = 3.0*((n-1)*(n-1))/((n-2)*(n-3));
    return t1*(M4/FastMath.pow(std, 4))-t3;
  }

  /**
   * Unbiased skewness.
   * See  http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math4/stat/descriptive/moment/Skewness.html
   * @return unbiased skewness
   */
  @Override
  public double getSkewness() {
    //  skewness = [n / (n -1) (n - 2)] sum[(x_i - mean)^3] / std^3
    if(n < 3) {
      return Double.NaN;
    }
    double t1 = (1.0*n)/((n - 1)*(n-2));
    double std = getStandardDeviation();
    return t1*M3/FastMath.pow(std, 3);
  }

  @Override
  public double getPercentile(double p) {
    return sketchType.getPercentile(sketch, p/100.0);
  }


  @Override
  public DistributionSketch<T> merge(DistributionSketch<T> provider) {
    DistributionSketchImpl<T, S> combined = createNew();
    DistributionSketchImpl<T, S> a = this;
    DistributionSketchImpl<T, S> b = (DistributionSketchImpl<T, S>)provider;

    //Combining the simple terms that obviously form a semigroup
    combined.n = a.n + b.n;
    combined.sum = numberType.add(a.sum,b.sum);
    if(a.min != null && b.min != null) {
      combined.min = numberType.min(a.min, b.min);
      combined.max = numberType.max(a.max, b.max);
    }
    else {
      combined.min = a.min;
      combined.max = a.max;
    }
    combined.sumOfSquares = numberType.add(a.sumOfSquares, b.sumOfSquares);
    combined.sumOfLogs = a.sumOfLogs+ b.sumOfLogs;

    // Adjusting the standardized moments, see http://www.johndcook.com/blog/skewness_kurtosis/
    double delta = b.M1 - a.M1;
    double delta2 = delta*delta;
    double delta3 = delta*delta2;
    double delta4 = delta2*delta2;

    combined.M1 = (a.n*a.M1 + b.n*b.M1) / combined.n;

    combined.M2 = a.M2 + b.M2 +
            delta2 * a.n * b.n / combined.n;

    combined.M3 = a.M3 + b.M3 +
            delta3 * a.n * b.n * (a.n - b.n)/(combined.n*combined.n);
    combined.M3 += 3.0*delta * (a.n*b.M2 - b.n*a.M2) / combined.n;

    combined.M4 = a.M4 + b.M4 + delta4*a.n*b.n * (a.n*a.n - a.n*b.n + b.n*b.n) /
            (combined.n*combined.n*combined.n);
    combined.M4 += 6.0*delta2 * (a.n*a.n*b.M2 + b.n*b.n*a.M2)/(combined.n*combined.n) +
            4.0*delta*(a.n*b.M3 - b.n*a.M3) / combined.n;

    //Merging the distributional sketches
    combined.sketch = sketchType.merge(a.sketch, b.sketch);
    //checkFlowError(combined.sumOfSquares, sum, combined.sumOfSquares, combined.M1, combined.M2, combined.M3, combined.M4);
    return combined;
  }

  @Override
  public void write(Kryo kryo, Output output) {
    output.writeInt(k);
    sketchType.serialize(sketch, output);
    output.writeLong(n);
    numberType.serialize(sum, output);
    numberType.serialize(sumOfSquares, output);
    output.writeDouble(sumOfLogs);
    numberType.serialize(min, output);
    numberType.serialize(max, output);
    output.writeDouble(M1);
    output.writeDouble(M2);
    output.writeDouble(M3);
    output.writeDouble(M4);
  }

  private void writeObject(java.io.ObjectOutputStream output)
      throws IOException {
    byte[] ser = SerDeUtils.toBytes(this);
    output.writeInt(ser.length);
    output.write(ser);
  }

  private void readObject(java.io.ObjectInputStream input)
      throws IOException, ClassNotFoundException {
    int len = input.readInt();
    byte[] ser = new byte[len];
    input.readFully(ser);
    DistributionSketchImpl<T, S> s = SerDeUtils.fromBytes(ser, this.getClass());
    copyFrom(s);
  }

  @Override
  public void read(Kryo kryo, Input input) {
    k = input.readInt();
    sketchType = createSketchType(k);
    numberType = createNumberType();
    sketch = sketchType.materialize(input);
    n = input.readLong();
    sum = numberType.materialize(input);
    sumOfSquares = numberType.materialize(input);
    sumOfLogs = input.readDouble();
    min = numberType.materialize(input);
    max = numberType.materialize(input);
    M1 = input.readDouble();
    M2 = input.readDouble();
    M3 = input.readDouble();
    M4 = input.readDouble();
  }

  @Override
  public String toString() {
    return "DistributionSketchImpl{" +
        "sketch=" + sketch +
        ", n=" + n +
        ", sum=" + sum +
        ", sumOfSquares=" + sumOfSquares +
        ", sumOfLogs=" + sumOfLogs +
        ", min=" + min +
        ", max=" + max +
        ", M1=" + M1 +
        ", M2=" + M2 +
        ", M3=" + M3 +
        ", M4=" + M4 +
        ", k=" + k +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DistributionSketchImpl<?, ?> that = (DistributionSketchImpl<?, ?>) o;
    return n == that.n &&
        Double.compare(that.sumOfLogs, sumOfLogs) == 0 &&
        Double.compare(that.M1, M1) == 0 &&
        Double.compare(that.M2, M2) == 0 &&
        Double.compare(that.M3, M3) == 0 &&
        Double.compare(that.M4, M4) == 0 &&
        k == that.k &&
        Objects.equals(sum, that.sum) &&
        Objects.equals(sumOfSquares, that.sumOfSquares) &&
        Objects.equals(min, that.min) &&
        Objects.equals(max, that.max);
  }

  @Override
  public int hashCode() {
    return Objects.hash(n, sum, sumOfSquares, sumOfLogs, min, max, M1, M2, M3, M4, k);
  }
}
