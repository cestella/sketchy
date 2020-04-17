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
package com.caseystella.sketchy.sketches;

import com.caseystella.stellar.common.utils.SerDeUtils;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.datasketches.quantiles.DoublesSketchBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class DoubleDistributionSketchTest {

  public static void validateStatisticsProvider( DistributionSketch<Double> statsProvider
                                               , SummaryStatistics summaryStats
                                               , DescriptiveStatistics stats
                                               ) {
    //N
    assertEquals(statsProvider.getCount(), stats.getN());
    //sum
    assertEquals(statsProvider.getSum(), stats.getSum(), 1e-3);
    //sum of squares
    assertEquals(statsProvider.getSumSquares(), stats.getSumsq(), 1e-3);
    //sum of squares
    assertEquals(statsProvider.getSumLogs(), summaryStats.getSumOfLogs(), 1e-3);
    //Mean
    assertEquals(statsProvider.getMean(), stats.getMean(), 1e-3);
    //Quadratic Mean
    assertEquals(statsProvider.getQuadraticMean(), summaryStats.getQuadraticMean(), 1e-3);
    //SD
    assertEquals(statsProvider.getStandardDeviation(), stats.getStandardDeviation(), 1e-3);
    //Variance
    assertEquals(statsProvider.getVariance(), stats.getVariance(), 1e-3);
    if(stats.getN() > 0) {
      //Min
      assertEquals(statsProvider.getMin().get(), stats.getMin(), 1e-3);
      //Max
      assertEquals(statsProvider.getMax().get(), stats.getMax(), 1e-3);
    }
    else {
      assertFalse(statsProvider.getMin().isPresent());
      assertFalse(statsProvider.getMax().isPresent());
    }

    //Kurtosis
    assertEquals(stats.getKurtosis(), statsProvider.getKurtosis(), 1e-3);

    //Skewness
    assertEquals(stats.getSkewness(), statsProvider.getSkewness(), 1e-3);
    for(double d = 10.0;d < 100.0;d+=10) {
      // This is a sketch, so we're a bit more forgiving here in our choice of \epsilon.
      assertEquals(
          statsProvider.getPercentile(d),
          stats.getPercentile(d),
          1e-2,
          "Percentile mismatch for " + d + "th %ile");
    }
  }

  private void validateEquality(Iterable<Double> values) {
    DescriptiveStatistics stats = new DescriptiveStatistics();
    SummaryStatistics summaryStats = new SummaryStatistics();
    DoublesSketchBuilder builder = new DoublesSketchBuilder().setK(512);
    DoubleDistributionSketch statsProvider = new DoubleDistributionSketch(builder);
    //Test that the aggregated provider gives the same results as the provider that is shown all the data.
    List<DistributionSketch<Double>> providers = new ArrayList<>();
    for(int i = 0;i < 10;++i) {
      providers.add(new DoubleDistributionSketch(builder));
    }
    int i = 0;
    for(double d : values) {
      i++;
      stats.addValue(d);
      summaryStats.addValue(d);
      providers.get(i % providers.size()).addValue(d);
      statsProvider.addValue(d);
    }
    DistributionSketch<Double> aggregatedProvider = cloneSketch(providers.get(0));
    for(int j = 1;j < providers.size();++j) {
      aggregatedProvider = aggregatedProvider.merge(cloneSketch(providers.get(j)));
    }
    validateStatisticsProvider(statsProvider, summaryStats, stats);
    validateStatisticsProvider(aggregatedProvider, summaryStats, stats);
  }

  DistributionSketch<Double> cloneSketch(DistributionSketch<Double> sketch) {
    byte[] ser = SerDeUtils.toBytes(sketch);
    DoubleDistributionSketch ret = (DoubleDistributionSketch) SerDeUtils.fromBytes(ser, sketch.getClass());
    assertEquals(ret.getK(), ((DoubleDistributionSketch)sketch).getK());
    return ret;
  }

  @Test
  public void testNormallyDistributedRandomData() {
    List<Double> values = new ArrayList<>();
    GaussianRandomGenerator gaussian = new GaussianRandomGenerator(new MersenneTwister(0L));
    for(int i = 0;i < 1000000;++i) {
      double d = gaussian.nextNormalizedDouble();
      values.add(d);
    }
    validateEquality(values);
  }
  @Test
  public void testNormallyDistributedRandomDataShifted() {
    List<Double> values = new ArrayList<>();
    GaussianRandomGenerator gaussian = new GaussianRandomGenerator(new MersenneTwister(0L));
    for(int i = 0;i < 1000000;++i) {
      double d = gaussian.nextNormalizedDouble() + 10;
      values.add(d);
    }
    validateEquality(values);
  }

  @Test
  public void testNormallyDistributedRandomDataShiftedBackwards() {
    List<Double> values = new ArrayList<>();
    GaussianRandomGenerator gaussian = new GaussianRandomGenerator(new MersenneTwister(0L));
    for(int i = 0;i < 1000000;++i) {
      double d = gaussian.nextNormalizedDouble() - 10;
      values.add(d);
    }
    validateEquality(values);
  }
  @Test
  public void testNormallyDistributedRandomDataSkewed() {
    List<Double> values = new ArrayList<>();
    GaussianRandomGenerator gaussian = new GaussianRandomGenerator(new MersenneTwister(0L));
    for(int i = 0;i < 1000000;++i) {
      double d = (gaussian.nextNormalizedDouble()+ 10000) /1000;
      values.add(d);
    }
    validateEquality(values);
  }

  @Test
  public void testNormallyDistributedRandomDataAllNegative() {
    List<Double> values = new ArrayList<>();
    GaussianRandomGenerator gaussian = new GaussianRandomGenerator(new MersenneTwister(0L));
    for(int i = 0;i < 1000000;++i) {
      double d = -1*gaussian.nextNormalizedDouble();
      values.add(d);
    }
    validateEquality(values);
  }
  @Test
  public void testUniformlyDistributedRandomData() {
    List<Double> values = new ArrayList<>();
    for(int i = 0;i < 100000;++i) {
      double d = Math.random();
      values.add(d);
    }
    validateEquality(values);
  }

}
