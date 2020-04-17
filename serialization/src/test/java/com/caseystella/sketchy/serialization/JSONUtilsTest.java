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
package com.caseystella.sketchy.serialization;

import com.caseystella.sketchy.test.UnitTestHelper;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.adrianwalker.multilinestring.Multiline;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class JSONUtilsTest {
  private static File tmpDir;

  // @formatter:off
  /**
   {
   "a" : "hello",
   "b" : "world"
   }
   */
  // @formatter:on
  @Multiline
  private static String config;
  private static File configFile;

  @BeforeAll
  public static void setUp() throws Exception {
    tmpDir = UnitTestHelper.createTempDir(new File("target/jsonutilstest"));
    configFile = UnitTestHelper.write(new File(tmpDir, "config.json"), config);
  }

  @Test
  public void loads_file_with_typeref() throws Exception {
    Map<String, Object> expected = new HashMap<String, Object>() {
      {
        put("a", "hello");
        put("b", "world");
      }
    };
    Map<String, Object> actual = JSONUtils.INSTANCE.load(configFile, JSONUtils.MAP_SUPPLIER);
    MatcherAssert.assertThat("config not equal", actual, CoreMatchers.equalTo(expected));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void loads_file_with_map_class() throws Exception {
    Map<String, Object> expected = new HashMap<String, Object>() {
      {
        put("a", "hello");
        put("b", "world");
      }
    };
    Map<String, Object> actual = JSONUtils.INSTANCE.load(configFile, Map.class);
    MatcherAssert.assertThat("config not equal", actual, CoreMatchers.equalTo(expected));
  }

  @Test
  public void loads_file_with_custom_class() throws Exception {
    TestConfig expected = new TestConfig().setA("hello").setB("world");
    TestConfig actual = JSONUtils.INSTANCE.load(configFile, TestConfig.class);
    MatcherAssert.assertThat("a not equal", actual.getA(), CoreMatchers.equalTo(expected.getA()));
    MatcherAssert.assertThat("b not equal", actual.getB(), CoreMatchers.equalTo(expected.getB()));
  }

  public static class TestConfig {
    private String a;
    private String b;

    public String getA() {
      return a;
    }

    public TestConfig setA(String a) {
      this.a = a;
      return this;
    }

    public String getB() {
      return b;
    }

    public TestConfig setB(String b) {
      this.b = b;
      return this;
    }
  }
}
