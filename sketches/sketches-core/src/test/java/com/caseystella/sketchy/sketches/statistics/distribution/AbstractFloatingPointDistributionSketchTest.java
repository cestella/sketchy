/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.caseystella.sketchy.sketches.statistics.distribution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.caseystella.sketchy.utilities.SerDeUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.jupiter.api.Test;

public abstract class AbstractFloatingPointDistributionSketchTest<T extends Number> {

  abstract DistributionSketch<T> createSketch();

  abstract T convert(Double d);

  protected double getPercentileDelta() {
    return 1e-2;
  }

  protected double getDelta() {
    return 1e-3;
  }

  public static <T extends Number> void validateStatisticsProvider(
      DistributionSketch<T> statsProvider, SummaryStatistics summaryStats,
      DescriptiveStatistics stats, double delta, double percentileDelta) {
    // N
    assertEquals(statsProvider.getCount(), stats.getN());
    // sum
    assertEquals(statsProvider.getSum().doubleValue(), stats.getSum(), delta);
    // sum of squares
    assertEquals(statsProvider.getSumSquares().doubleValue(), stats.getSumsq(), delta);
    // sum of squares
    assertEquals(statsProvider.getSumLogs(), summaryStats.getSumOfLogs(), delta);
    // Mean
    assertEquals(statsProvider.getMean(), stats.getMean(), delta);
    // Quadratic Mean
    assertEquals(statsProvider.getQuadraticMean(), summaryStats.getQuadraticMean(), delta);
    // SD
    assertEquals(statsProvider.getStandardDeviation(), stats.getStandardDeviation(), delta);
    // Variance
    assertEquals(statsProvider.getVariance(), stats.getVariance(), delta);
    if (stats.getN() > 0) {
      // Min
      assertEquals(statsProvider.getMin().get().doubleValue(), stats.getMin(), delta);
      // Max
      assertEquals(statsProvider.getMax().get().doubleValue(), stats.getMax(), delta);
    } else {
      assertFalse(statsProvider.getMin().isPresent());
      assertFalse(statsProvider.getMax().isPresent());
    }

    // Kurtosis
    assertEquals(stats.getKurtosis(), statsProvider.getKurtosis(), delta);

    // Skewness
    assertEquals(stats.getSkewness(), statsProvider.getSkewness(), delta);
    for (double d = 10.0; d < 100.0; d += 10) {
      // This is a sketch, so we're a bit more forgiving here in our choice of \epsilon.
      assertEquals(statsProvider.getPercentile(d), stats.getPercentile(d), percentileDelta,
          "Percentile mismatch for " + d + "th %ile");
    }
  }

  private void validateEquality(Iterable<Double> values)
      throws IOException, ClassNotFoundException {
    DescriptiveStatistics stats = new DescriptiveStatistics();
    SummaryStatistics summaryStats = new SummaryStatistics();
    DistributionSketch<T> statsProvider = createSketch();
    // Test that the aggregated provider gives the same results as the provider that is shown all
    // the data.
    List<DistributionSketch<T>> providers = new ArrayList<>();
    for (int i = 0; i < 10; ++i) {
      providers.add(createSketch());
    }
    int i = 0;
    for (double d : values) {
      i++;
      stats.addValue(d);
      summaryStats.addValue(d);
      providers.get(i % providers.size()).addValue(convert(d));
      statsProvider.addValue(convert(d));
    }
    DistributionSketch<T> aggregatedProvider = cloneSketch(providers.get(0), 0);
    for (int j = 1; j < providers.size(); ++j) {
      aggregatedProvider = aggregatedProvider.merge(cloneSketch(providers.get(j), j));
    }
    validateStatisticsProvider(statsProvider, summaryStats, stats, getDelta(),
        getPercentileDelta());
    validateStatisticsProvider(aggregatedProvider, summaryStats, stats, getDelta(),
        getPercentileDelta());
  }

  DistributionSketch<T> cloneSketch(DistributionSketch<T> sketch, int index)
      throws IOException, ClassNotFoundException {
    if (index % 2 == 0) {
      byte[] ser = SerDeUtils.toBytes(sketch);
      DistributionSketch<T> ret =
          (DistributionSketch<T>) SerDeUtils.fromBytes(ser, sketch.getClass());
      assertEquals(ret, sketch);
      return ret;
    } else {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(bos);
      out.writeObject(sketch);
      // De-serialization of object
      ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
      ObjectInputStream in = new ObjectInputStream(bis);
      DistributionSketch<T> ret = (DistributionSketch<T>) in.readObject();
      assertEquals(ret, sketch);
      return ret;
    }
  }

  @Test
  public void testNormallyDistributedRandomData() throws IOException, ClassNotFoundException {
    List<Double> values = new ArrayList<>();
    GaussianRandomGenerator gaussian = new GaussianRandomGenerator(new MersenneTwister(0L));
    for (int i = 0; i < 1000000; ++i) {
      double d = gaussian.nextNormalizedDouble();
      values.add(d);
    }
    validateEquality(values);
  }

  @Test
  public void testNormallyDistributedRandomDataShifted()
      throws IOException, ClassNotFoundException {
    List<Double> values = new ArrayList<>();
    GaussianRandomGenerator gaussian = new GaussianRandomGenerator(new MersenneTwister(0L));
    for (int i = 0; i < 1000000; ++i) {
      double d = gaussian.nextNormalizedDouble() + 10;
      values.add(d);
    }
    validateEquality(values);
  }

  @Test
  public void testNormallyDistributedRandomDataShiftedBackwards()
      throws IOException, ClassNotFoundException {
    List<Double> values = new ArrayList<>();
    GaussianRandomGenerator gaussian = new GaussianRandomGenerator(new MersenneTwister(0L));
    for (int i = 0; i < 1000000; ++i) {
      double d = gaussian.nextNormalizedDouble() - 10;
      values.add(d);
    }
    validateEquality(values);
  }

  @Test
  public void testNormallyDistributedRandomDataSkewed() throws IOException, ClassNotFoundException {
    List<Double> values = new ArrayList<>();
    GaussianRandomGenerator gaussian = new GaussianRandomGenerator(new MersenneTwister(0L));
    for (int i = 0; i < 1000000; ++i) {
      double d = (gaussian.nextNormalizedDouble() + 10000) / 1000;
      values.add(d);
    }
    validateEquality(values);
  }

  @Test
  public void testNormallyDistributedRandomDataAllNegative()
      throws IOException, ClassNotFoundException {
    List<Double> values = new ArrayList<>();
    GaussianRandomGenerator gaussian = new GaussianRandomGenerator(new MersenneTwister(0L));
    for (int i = 0; i < 1000000; ++i) {
      double d = -1 * gaussian.nextNormalizedDouble();
      values.add(d);
    }
    validateEquality(values);
  }

  @Test
  public void testUniformlyDistributedRandomData() throws IOException, ClassNotFoundException {
    List<Double> values = new ArrayList<>();
    for (int i = 0; i < 100000; ++i) {
      double d = Math.random();
      values.add(d);
    }
    validateEquality(values);
  }

}
