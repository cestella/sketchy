/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.caseystella.stellar.dsl.functions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.caseystella.stellar.common.shell.VariableResult;
import com.caseystella.stellar.common.utils.StellarProcessorUtils;
import com.caseystella.stellar.dsl.Context;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import org.adrianwalker.multilinestring.Multiline;
import org.junit.jupiter.api.Test;

public class ShellFunctionsTest {

  Map<String, VariableResult> variables =
      ImmutableMap.of("var1", VariableResult.withExpression("CASEY", "TO_UPPER('casey')"), "var2",
          VariableResult.withValue("foo"), "var3", VariableResult.withValue(null), "var4",
          VariableResult.withExpression(null, "blah"));

  Context context =
      new Context.Builder().with(Context.Capabilities.SHELL_VARIABLES, () -> variables).build();
// @formatter:off
/**
╔══════════╤═══════╤════════════╗
║ VARIABLE │ VALUE │ EXPRESSION ║
╠══════════╪═══════╪════════════╣
║ foo      │ 2.0   │ 1 + 1      ║
╚══════════╧═══════╧════════════╝
 **/
// @formatter:on
  @Multiline
  static String expectedListWithFoo;

  @Test
  public void testListVarsWithVars() {
    Map<String, VariableResult> variables =
        ImmutableMap.of("foo", VariableResult.withExpression(2.0, "1 + 1"));

    Context context =
        new Context.Builder().with(Context.Capabilities.SHELL_VARIABLES, () -> variables).build();
    Object out = StellarProcessorUtils.run("SHELL_LIST_VARS()", new HashMap<>(), context);
    assertEquals(expectedListWithFoo, out);
  }

  // @formatter:off
/**
╔══════════╤═══════╤════════════╗
║ VARIABLE │ VALUE │ EXPRESSION ║
╠══════════╧═══════╧════════════╣
║ (empty)                       ║
╚═══════════════════════════════╝
 **/
// @formatter:on
  @Multiline
  static String expectedEmptyList;

  @Test
  public void testListVarsWithoutVars() {
    Context context = new Context.Builder()
        .with(Context.Capabilities.SHELL_VARIABLES, () -> new HashMap<>()).build();
    Object out = StellarProcessorUtils.run("SHELL_LIST_VARS()", new HashMap<>(), context);
    assertEquals(expectedEmptyList, out);
  }

  // @formatter:off
/**
╔════════╤═══════╗
║ KEY    │ VALUE ║
╠════════╪═══════╣
║ field1 │ val1  ║
╟────────┼───────╢
║ field2 │ val2  ║
╚════════╧═══════╝
 **/
// @formatter:on
  @Multiline
  static String expectedMap2Table;

  @Test
  public void testMap2Table() {
    Map<String, Object> variables =
        ImmutableMap.of("map_field", ImmutableMap.of("field1", "val1", "field2", "val2"));
    Context context = Context.EMPTY_CONTEXT();
    Object out = StellarProcessorUtils.run("SHELL_MAP2TABLE(map_field)", variables, context);
    assertEquals(expectedMap2Table, out);
  }

  // @formatter:off
 /**
╔═════╤═══════╗
║ KEY │ VALUE ║
╠═════╧═══════╣
║ (empty)     ║
╚═════════════╝
 **/
 // @formatter:on
  @Multiline
  static String expectedMap2TableNullInput;

  @Test
  public void testMap2TableNullInput() {
    Map<String, Object> variables = new HashMap<String, Object>() {
      {
        put("map_field", null);
      }
    };
    Context context = Context.EMPTY_CONTEXT();
    Object out = StellarProcessorUtils.run("SHELL_MAP2TABLE(map_field)", variables, context);
    assertEquals(expectedMap2TableNullInput, out);
  }

  @Test
  public void testMap2TableInsufficientArgs() {
    Map<String, Object> variables = new HashMap<>();
    Context context = Context.EMPTY_CONTEXT();
    Object out = StellarProcessorUtils.run("SHELL_MAP2TABLE()", variables, context);
    assertNull(out);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testVars2Map() {
    Object out =
        StellarProcessorUtils.run("SHELL_VARS2MAP('var1', 'var2')", new HashMap<>(), context);
    assertTrue(out instanceof Map);
    Map<String, String> mapOut = (Map<String, String>) out;
    // second one is null, so we don't want it there.
    assertEquals(1, mapOut.size());
    assertEquals("TO_UPPER('casey')", mapOut.get("var1"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testVars2MapEmpty() {
    Object out = StellarProcessorUtils.run("SHELL_VARS2MAP()", new HashMap<>(), context);
    Map<String, String> mapOut = (Map<String, String>) out;
    assertEquals(0, mapOut.size());
  }

  @Test
  public void testGetExpression() {
    Object out =
        StellarProcessorUtils.run("SHELL_GET_EXPRESSION('var1')", new HashMap<>(), context);
    assertTrue(out instanceof String);
    String expression = (String) out;
    // second one is null, so we don't want it there.
    assertEquals("TO_UPPER('casey')", expression);
  }

  @Test
  public void testGetExpressionEmpty() {
    Object out = StellarProcessorUtils.run("SHELL_GET_EXPRESSION()", new HashMap<>(), context);
    assertNull(out);
  }

  @Test
  public void testEdit() {
    System.getProperties().put("EDITOR", "/bin/cat");
    Object out = StellarProcessorUtils.run("TO_UPPER(SHELL_EDIT(foo))",
        ImmutableMap.of("foo", "foo"), context);
    assertEquals("FOO", out);
  }

}
