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
package com.caseystella.stellar.common.shell.specials;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.caseystella.sketchy.utilities.ConversionUtils;
import com.caseystella.stellar.common.shell.DefaultStellarShellExecutor;
import com.caseystella.stellar.common.shell.StellarResult;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MagicListGlobalsTest {

  MagicListGlobals magic;
  DefaultStellarShellExecutor executor;

  @BeforeEach
  public void setup() throws Exception {

    // setup the %magic
    magic = new MagicListGlobals();

    // setup the executor
    Properties props = new Properties();
    executor = new DefaultStellarShellExecutor(props, Optional.empty());
    executor.init();
  }

  @Test
  public void testGetCommand() {
    assertEquals("%globals", magic.getCommand());
  }

  @Test
  public void testShouldMatch() {
    List<String> inputs =
        Arrays.asList("%globals", "   %globals   ", "%globals   FOO", "    %globals    FOO ");
    for (String in : inputs) {
      assertTrue(magic.getMatcher().apply(in), "failed: " + in);
    }
  }

  @Test
  public void testShouldNotMatch() {
    List<String> inputs = Arrays.asList("foo", "  globals ", "bar", "%define");
    for (String in : inputs) {
      assertFalse(magic.getMatcher().apply(in), "failed: " + in);
    }
  }

  @Test
  public void test() {
    // define some globals
    executor.getGlobalConfig().put("x", 2);

    // get all globals
    StellarResult result = executor.execute("%globals");

    // validate the result
    assertTrue(result.isSuccess());
    assertTrue(result.getValue().isPresent());

    String out = ConversionUtils.convert(result.getValue().get(), String.class);
    assertEquals("{x=2}", out);
  }

  @Test
  public void testWithNoGlobals() {
    // get all globals
    StellarResult result = executor.execute("%globals");

    // validate the result
    assertTrue(result.isSuccess());
    assertTrue(result.getValue().isPresent());

    String out = ConversionUtils.convert(result.getValue().get(), String.class);
    assertEquals("{}", out);
  }
}
