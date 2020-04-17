/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caseystella.stellar.common;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.caseystella.stellar.dsl.ParseException;
import org.junit.jupiter.api.Test;

public class StellarPredicateProcessorTest {
    @Test
    public void testValidation() {
        StellarPredicateProcessor processor = new StellarPredicateProcessor();
        assertThrows(ParseException.class, () -> processor.validate("enrichedField1 == 'enrichedValue1"),
                "Invalid rule found to be valid - unclosed single quotes.");
    }
}
