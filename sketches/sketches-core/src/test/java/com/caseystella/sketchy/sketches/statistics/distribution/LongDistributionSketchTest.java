package com.caseystella.sketchy.sketches.statistics.distribution;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import com.caseystella.stellar.common.utils.SerDeUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class LongDistributionSketchTest {
  public static void validateStatisticsProvider(DistributionSketch<Long> statsProvider,
      SummaryStatistics summaryStats, DescriptiveStatistics stats) {
    // N
    assertEquals(statsProvider.getCount(), stats.getN());
    // sum
    assertEquals(statsProvider.getSum(), stats.getSum(), 1e-3);
    // sum of squares
    assertEquals(statsProvider.getSumSquares(), stats.getSumsq(), 1e-3);
    // sum of squares
    assertEquals(statsProvider.getSumLogs(), summaryStats.getSumOfLogs(), 1e-3);
    // Mean
    assertEquals(statsProvider.getMean(), stats.getMean(), 1e-3);
    // Quadratic Mean
    assertEquals(statsProvider.getQuadraticMean(), summaryStats.getQuadraticMean(), 1e-3);
    // SD
    assertEquals(statsProvider.getStandardDeviation(), stats.getStandardDeviation(), 1e-3);
    // Variance
    assertEquals(statsProvider.getVariance(), stats.getVariance(), 1e-3);
    if (stats.getN() > 0) {
      // Min
      assertEquals(statsProvider.getMin().get(), stats.getMin(), 1e-3);
      // Max
      assertEquals(statsProvider.getMax().get(), stats.getMax(), 1e-3);
    } else {
      assertFalse(statsProvider.getMin().isPresent());
      assertFalse(statsProvider.getMax().isPresent());
    }

    // Kurtosis
    assertEquals(stats.getKurtosis(), statsProvider.getKurtosis(), 1e-3);

    // Skewness
    assertEquals(stats.getSkewness(), statsProvider.getSkewness(), 1e-3);
  }

  private void validateEquality(Iterable<Long> values) {
    DescriptiveStatistics stats = new DescriptiveStatistics();
    SummaryStatistics summaryStats = new SummaryStatistics();
    int k = 512;
    DistributionSketch<Long> statsProvider = DistributionSketches.LONG.create(k, Long.class);
    // Test that the aggregated provider gives the same results as the provider that is shown all
    // the data.
    List<DistributionSketch<Long>> providers = new ArrayList<>();
    for (int i = 0; i < 10; ++i) {
      providers.add(DistributionSketches.LONG.create(k, Long.class));
    }
    int i = 0;
    for (long d : values) {
      i++;
      stats.addValue(d);
      summaryStats.addValue(d);
      providers.get(i % providers.size()).addValue(d);
      statsProvider.addValue(d);
    }
    DistributionSketch<Long> aggregatedProvider = cloneSketch(providers.get(0));
    for (int j = 1; j < providers.size(); ++j) {
      aggregatedProvider = aggregatedProvider.merge(cloneSketch(providers.get(j)));
    }
    validateStatisticsProvider(statsProvider, summaryStats, stats);
    validateStatisticsProvider(aggregatedProvider, summaryStats, stats);
  }

  DistributionSketch<Long> cloneSketch(DistributionSketch<Long> sketch) {
    byte[] ser = SerDeUtils.toBytes(sketch);
    DistributionSketch<Long> ret =
        (DistributionSketch<Long>) SerDeUtils.fromBytes(ser, sketch.getClass());
    assertEquals(ret.getK(), sketch.getK());
    return ret;
  }

  @Test
  public void testUniformlyDistributedRandomData() {
    List<Long> values = new ArrayList<>();
    for (int i = 0; i < 100000; ++i) {
      long d = (long) (1000L * Math.random());
      values.add(d);
    }
    validateEquality(values);
  }
}
