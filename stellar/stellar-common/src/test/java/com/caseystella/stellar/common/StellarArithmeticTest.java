/*
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
 */

package com.caseystella.stellar.common;

import com.caseystella.stellar.common.utils.StellarProcessorUtils;
import com.caseystella.stellar.dsl.ParseException;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Pair;
import com.caseystella.stellar.dsl.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.caseystella.stellar.common.utils.StellarProcessorUtils.run;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class StellarArithmeticTest {
  @Test
  public void addingLongsShouldYieldLong() {
    final long timestamp = 1452013350000L;
    String query = "TO_EPOCH_TIMESTAMP('2016-01-05 17:02:30', 'yyyy-MM-dd HH:mm:ss', 'UTC') + 2";
    Assertions.assertEquals(timestamp + 2, StellarProcessorUtils.run(query, new HashMap<>()));
  }

  @Test
  public void addingIntegersShouldYieldAnInteger() {
    String query = "1 + 2";
    Assertions.assertEquals(3, StellarProcessorUtils.run(query, new HashMap<>()));
  }

  @Test
  public void addingDoublesShouldYieldADouble() {
    String query = "1.0 + 2.0";
    Assertions.assertEquals(3.0, StellarProcessorUtils.run(query, new HashMap<>()));
  }

  @Test
  public void addingDoubleAndIntegerWhereSubjectIsDoubleShouldYieldADouble() {
    String query = "2.1 + 1";
    Assertions.assertEquals(3.1, StellarProcessorUtils.run(query, new HashMap<>()));
  }

  @Test
  public void addingDoubleAndIntegerWhereSubjectIsIntegerShouldYieldADouble() {
    String query = "1 + 2.1";
    Assertions.assertEquals(3.1, StellarProcessorUtils.run(query, new HashMap<>()));
  }

  @Test
  public void testArithmetic() {
    Assertions.assertEquals(3, StellarProcessorUtils.run("1 + 2", new HashMap<>()));
    Assertions.assertEquals(3.2, StellarProcessorUtils.run("1.2 + 2", new HashMap<>()));
    Assertions.assertEquals(1.2e-3 + 2, StellarProcessorUtils.run("1.2e-3 + 2", new HashMap<>()));
    Assertions.assertEquals(1.2f + 3.7, StellarProcessorUtils.run("1.2f + 3.7", new HashMap<>()));
    Assertions.assertEquals(12L * (1.2f + 7),
        StellarProcessorUtils.run("12L*(1.2f + 7)", new HashMap<>()));
    Assertions.assertEquals(12.2f * (1.2f + 7L),
        StellarProcessorUtils.run("TO_FLOAT(12.2) * (1.2f + 7L)", new HashMap<>()));
  }

  @Test
  public void testNumericOperations() {
    {
      String query = "TO_INTEGER(1 + 2*2 + 3 - 4 - 0.5)";
      assertEquals(3, (Integer) StellarProcessorUtils.run(query, new HashMap<>()), 1e-6);
    }
    {
      String query = "1 + 2*2 + 3 - 4 - 0.5";
      assertEquals(3.5, (Double) StellarProcessorUtils.run(query, new HashMap<>()), 1e-6);
    }
    {
      String query = "2*one*(1 + 2*2 + 3 - 4)";
      Assertions.assertEquals(8, StellarProcessorUtils.run(query, ImmutableMap.of("one", 1)));
    }
    {
      String query = "2*(1 + 2 + 3 - 4)";
      assertEquals(4, (Integer) StellarProcessorUtils.run(query,
          ImmutableMap.of("one", 1, "very_nearly_one", 1.000001)), 1e-6);
    }
    {
      String query = "1 + 2 + 3 - 4 - 2";
      assertEquals(0, (Integer) StellarProcessorUtils.run(query,
          ImmutableMap.of("one", 1, "very_nearly_one", 1.000001)), 1e-6);
    }
    {
      String query = "1 + 2 + 3 + 4";
      assertEquals(10, (Integer) StellarProcessorUtils.run(query,
          ImmutableMap.of("one", 1, "very_nearly_one", 1.000001)), 1e-6);
    }
    {
      String query = "(one + 2)*3";
      assertEquals(9, (Integer) StellarProcessorUtils.run(query,
          ImmutableMap.of("one", 1, "very_nearly_one", 1.000001)), 1e-6);
    }
    {
      String query = "TO_INTEGER((one + 2)*3.5)";
      assertEquals(10, (Integer) StellarProcessorUtils.run(query,
          ImmutableMap.of("one", 1, "very_nearly_one", 1.000001)), 1e-6);
    }
    {
      String query = "1 + 2*3";
      assertEquals(7, (Integer) StellarProcessorUtils.run(query,
          ImmutableMap.of("one", 1, "very_nearly_one", 1.000001)), 1e-6);
    }
    {
      String query = "TO_LONG(foo)";
      assertNull(StellarProcessorUtils.run(query, ImmutableMap.of("foo", "not a number")));
    }
    {
      String query = "TO_LONG(foo)";
      Assertions.assertEquals(232321L,
          StellarProcessorUtils.run(query, ImmutableMap.of("foo", "00232321")));
    }
    {
      String query = "TO_LONG(foo)";
      Assertions.assertEquals(Long.MAX_VALUE,
          StellarProcessorUtils.run(query, ImmutableMap.of("foo", Long.toString(Long.MAX_VALUE))));
    }
  }

  @Test
  public void verifyExpectedReturnTypes() {
    Token<Integer> integer = mock(Token.class);
    when(integer.getValue()).thenReturn(1);

    Token<Long> lng = mock(Token.class);
    when(lng.getValue()).thenReturn(1L);

    Token<Double> dbl = mock(Token.class);
    when(dbl.getValue()).thenReturn(1.0D);

    Token<Float> flt = mock(Token.class);
    when(flt.getValue()).thenReturn(1.0F);

    Map<Pair<String, String>, Class<? extends Number>> expectedReturnTypeMappings =
        new HashMap<Pair<String, String>, Class<? extends Number>>() {
          {
            put(Pair.of("TO_FLOAT(3.0)", "TO_LONG(1)"), Float.class);
            put(Pair.of("TO_FLOAT(3)", "3.0"), Double.class);
            put(Pair.of("TO_FLOAT(3)", "TO_FLOAT(3)"), Float.class);
            put(Pair.of("TO_FLOAT(3)", "3"), Float.class);

            put(Pair.of("TO_LONG(1)", "TO_LONG(1)"), Long.class);
            put(Pair.of("TO_LONG(1)", "3.0"), Double.class);
            put(Pair.of("TO_LONG(1)", "TO_FLOAT(3)"), Float.class);
            put(Pair.of("TO_LONG(1)", "3"), Long.class);

            put(Pair.of("3.0", "TO_LONG(1)"), Double.class);
            put(Pair.of("3.0", "3.0"), Double.class);
            put(Pair.of("3.0", "TO_FLOAT(3)"), Double.class);
            put(Pair.of("3.0", "3"), Double.class);

            put(Pair.of("3", "TO_LONG(1)"), Long.class);
            put(Pair.of("3", "3.0"), Double.class);
            put(Pair.of("3", "TO_FLOAT(3)"), Float.class);
            put(Pair.of("3", "3"), Integer.class);
          }
        };

    expectedReturnTypeMappings.forEach((pair, expectedClass) -> {
      assertTrue(
          StellarProcessorUtils.run(pair.getLeft() + " * " + pair.getRight(), ImmutableMap.of())
              .getClass() == expectedClass);
      assertTrue(
          StellarProcessorUtils.run(pair.getLeft() + " + " + pair.getRight(), ImmutableMap.of())
              .getClass() == expectedClass);
      assertTrue(
          StellarProcessorUtils.run(pair.getLeft() + " - " + pair.getRight(), ImmutableMap.of())
              .getClass() == expectedClass);
      assertTrue(
          StellarProcessorUtils.run(pair.getLeft() + " / " + pair.getRight(), ImmutableMap.of())
              .getClass() == expectedClass);
    });
  }

  @Test
  public void happyPathFloatArithmetic() {
    Object run = StellarProcessorUtils.run(".0f * 1", ImmutableMap.of());
    assertEquals(.0f * 1, run);
    assertEquals(Float.class, run.getClass());

    Object run1 = StellarProcessorUtils.run("0.f / 1F", ImmutableMap.of());
    assertEquals(0.f / 1F, run1);
    assertEquals(Float.class, run1.getClass());

    Object run2 = StellarProcessorUtils.run(".0F + 1.0f", ImmutableMap.of());
    assertEquals(.0F + 1.0f, run2);
    assertEquals(Float.class, run2.getClass());

    Object run3 = StellarProcessorUtils.run("0.0f - 0.1f", ImmutableMap.of());
    assertEquals(0.0f - 0.1f, run3);
    assertEquals(Float.class, run2.getClass());
  }

  @SuppressWarnings("PointlessArithmeticExpression")
  @Test
  public void happyPathLongArithmetic() {
    Assertions.assertEquals(0L * 1L, StellarProcessorUtils.run("0L * 1L", ImmutableMap.of()));
    Assertions.assertEquals(0l / 1L, StellarProcessorUtils.run("0l / 1L", ImmutableMap.of()));
    Assertions.assertEquals(1L - 1l, StellarProcessorUtils.run("1L - 1l", ImmutableMap.of()));
    Assertions.assertEquals(2147483648L + 1L,
        StellarProcessorUtils.run("2147483648L + 1L", ImmutableMap.of()));
  }

  @SuppressWarnings("NumericOverflow")
  @Test
  public void checkInterestingCases() {
    Assertions.assertEquals((((((1L) + .5d)))) * 6.f,
        StellarProcessorUtils.run("(((((1L) + .5d)))) * 6.f", ImmutableMap.of()));
    Assertions.assertEquals((((((1L) + .5d)))) * 6.f / 0.f,
        StellarProcessorUtils.run("(((((1L) + .5d)))) * 6.f / 0.f", ImmutableMap.of()));
    Assertions.assertEquals(Double.class,
        StellarProcessorUtils.run("(((((1L) + .5d)))) * 6.f / 0.f", ImmutableMap.of()).getClass());
  }

  @Test
  public void makeSureStellarProperlyEvaluatesLiteralsToExpectedTypes() {
    {
      Assertions.assertEquals(Float.class,
          StellarProcessorUtils.run("6.f", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Float.class,
          StellarProcessorUtils.run(".0f", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Float.class,
          StellarProcessorUtils.run("6.0F", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Float.class,
          StellarProcessorUtils.run("6f", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Float.class,
          StellarProcessorUtils.run("6e-6f", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Float.class,
          StellarProcessorUtils.run("6e+6f", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Float.class,
          StellarProcessorUtils.run("6e6f", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Float.class,
          StellarProcessorUtils.run("TO_FLOAT(1231)", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Float.class,
          StellarProcessorUtils.run("TO_FLOAT(12.31)", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Float.class,
          StellarProcessorUtils.run("TO_FLOAT(12.31f)", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Float.class,
          StellarProcessorUtils.run("TO_FLOAT(12L)", ImmutableMap.of()).getClass());
    }
    {
      Assertions.assertEquals(Double.class,
          StellarProcessorUtils.run("6.d", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Double.class,
          StellarProcessorUtils.run("6.D", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Double.class,
          StellarProcessorUtils.run("6.0d", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Double.class,
          StellarProcessorUtils.run("6D", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Double.class,
          StellarProcessorUtils.run("6e5D", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Double.class,
          StellarProcessorUtils.run("6e-5D", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Double.class,
          StellarProcessorUtils.run("6e+5D", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Double.class,
          StellarProcessorUtils.run("TO_DOUBLE(1231)", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Double.class,
          StellarProcessorUtils.run("TO_DOUBLE(12.31)", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Double.class,
          StellarProcessorUtils.run("TO_DOUBLE(12.31f)", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Double.class,
          StellarProcessorUtils.run("TO_DOUBLE(12L)", ImmutableMap.of()).getClass());
    }
    {
      Assertions.assertEquals(Integer.class,
          StellarProcessorUtils.run("6", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Integer.class,
          StellarProcessorUtils.run("60000000", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Integer.class,
          StellarProcessorUtils.run("-0", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Integer.class,
          StellarProcessorUtils.run("-60000000", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Integer.class,
          StellarProcessorUtils.run("TO_INTEGER(1231)", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Integer.class,
          StellarProcessorUtils.run("TO_INTEGER(12.31)", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Integer.class,
          StellarProcessorUtils.run("TO_INTEGER(12.31f)", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Integer.class,
          StellarProcessorUtils.run("TO_INTEGER(12L)", ImmutableMap.of()).getClass());
    }
    {
      Assertions.assertEquals(Long.class,
          StellarProcessorUtils.run("12345678910l", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Long.class,
          StellarProcessorUtils.run("0l", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Long.class,
          StellarProcessorUtils.run("-0l", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Long.class,
          StellarProcessorUtils.run("-60000000L", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Long.class,
          StellarProcessorUtils.run("-60000000L", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Long.class,
          StellarProcessorUtils.run("TO_LONG(1231)", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Long.class,
          StellarProcessorUtils.run("TO_LONG(12.31)", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Long.class,
          StellarProcessorUtils.run("TO_LONG(12.31f)", ImmutableMap.of()).getClass());
      Assertions.assertEquals(Long.class,
          StellarProcessorUtils.run("TO_LONG(12L)", ImmutableMap.of()).getClass());
    }
  }

  @Test
  public void parseExceptionMultipleLeadingZerosOnInteger() {
    assertThrows(ParseException.class,
        () -> StellarProcessorUtils.run("000000", ImmutableMap.of()));
  }

  @Test
  public void parseExceptionMultipleLeadingZerosOnLong() {
    assertThrows(ParseException.class,
        () -> StellarProcessorUtils.run("000000l", ImmutableMap.of()));
  }

  @Test
  public void parseExceptionMultipleLeadingZerosOnDouble() {
    assertThrows(ParseException.class,
        () -> StellarProcessorUtils.run("000000d", ImmutableMap.of()));
  }

  @Test
  public void parseExceptionMultipleLeadingZerosOnFloat() {
    assertThrows(ParseException.class,
        () -> StellarProcessorUtils.run("000000f", ImmutableMap.of()));
  }

  @Test
  public void parseExceptionMultipleLeadingNegativeSignsFloat() {
    assertThrows(ParseException.class,
        () -> StellarProcessorUtils.run("--000000f", ImmutableMap.of()));
  }

  @Test
  public void parseExceptionMultipleLeadingNegativeSignsDouble() {
    assertThrows(ParseException.class,
        () -> StellarProcessorUtils.run("--000000D", ImmutableMap.of()));
  }

  @Test
  public void parseExceptionMultipleLeadingNegativeSignsLong() {
    assertThrows(ParseException.class,
        () -> StellarProcessorUtils.run("--000000L", ImmutableMap.of()));
  }

  @Test
  public void unableToDivideByZeroWithIntegers() {
    assertThrows(ParseException.class, () -> StellarProcessorUtils.run("0/0", ImmutableMap.of()));
  }

  @Test
  public void unableToDivideByZeroWithLongs() {
    assertThrows(ParseException.class, () -> StellarProcessorUtils.run("0L/0L", ImmutableMap.of()));
  }

  @Test
  public void ableToDivideByZero() {
    Assertions.assertEquals(0F / 0F, StellarProcessorUtils.run("0F/0F", ImmutableMap.of()));
    Assertions.assertEquals(0D / 0D, StellarProcessorUtils.run("0D/0D", ImmutableMap.of()));
    Assertions.assertEquals(0D / 0F, StellarProcessorUtils.run("0D/0F", ImmutableMap.of()));
    Assertions.assertEquals(0F / 0D, StellarProcessorUtils.run("0F/0D", ImmutableMap.of()));
    Assertions.assertEquals(0F / 0, StellarProcessorUtils.run("0F/0", ImmutableMap.of()));
    Assertions.assertEquals(0D / 0, StellarProcessorUtils.run("0D/0", ImmutableMap.of()));
    Assertions.assertEquals(0 / 0D, StellarProcessorUtils.run("0/0D", ImmutableMap.of()));
    Assertions.assertEquals(0 / 0F, StellarProcessorUtils.run("0/0F", ImmutableMap.of()));
  }
}
