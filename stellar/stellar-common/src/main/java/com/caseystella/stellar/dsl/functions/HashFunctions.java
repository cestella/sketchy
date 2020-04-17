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
package com.caseystella.stellar.dsl.functions;

import org.apache.commons.codec.EncoderException;
import com.caseystella.stellar.common.utils.hashing.HashStrategy;
import com.caseystella.stellar.dsl.BaseStellarFunction;
import com.caseystella.stellar.dsl.Stellar;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HashFunctions {

  @Stellar(name = "GET_HASHES_AVAILABLE",
      description = "Will return all available hashing algorithms available to 'HASH'.",
      returns = "A list containing all supported hashing algorithms.")
  public static class ListSupportedHashTypes extends BaseStellarFunction {

    @Override
    public List<String> apply(final List<Object> args) {
      if (args == null || args.size() != 0) {
        throw new IllegalArgumentException(
            "Invalid call. This function does not expect any arguments.");
      }

      List<String> ret = new ArrayList<>();
      ret.addAll(HashStrategy.ALL_SUPPORTED_HASHES);
      return ret;
    }
  }


  @Stellar(name = "HASH",
      description = "Hashes a given value using the given hashing algorithm and returns a hex encoded string.",
      params = {"toHash - value to hash.",
          "hashType - A valid string representation of a hashing algorithm. See 'GET_HASHES_AVAILABLE'.",
          "config? - Configuration for the hash function in the form of a String to object map.\n"
              + "          For all other hashes:\n"
              + "          - charset : The character set to use (UTF8 is default). \n"},
      returns = "A hex encoded string of a hashed value using the given algorithm. If 'hashType' is null "
          + "then '00', padded to the necessary length, will be returned. If 'toHash' is not able to be hashed or "
          + "'hashType' is null then null is returned.")
  public static class Hash extends BaseStellarFunction {

    @Override
    @SuppressWarnings("unchecked")
    public Object apply(final List<Object> args) {
      if (args == null || args.size() < 2) {
        throw new IllegalArgumentException(
            "Invalid number of arguments: " + (args == null ? 0 : args.size()));
      }

      final Object toHash = args.get(0);
      final Object hashType = args.get(1);
      if (hashType == null) {
        return null;
      }

      Map<String, Object> config = null;
      if (args.size() > 2) {
        Object configObj = args.get(2);
        if (configObj instanceof Map && configObj != null) {
          config = (Map<String, Object>) configObj;
        }
      }
      try {
        return HashStrategy.getHasher(hashType.toString(), Optional.ofNullable(config))
            .getHash(toHash);
      } catch (final EncoderException e) {
        return null;
      } catch (final NoSuchAlgorithmException e) {
        throw new IllegalArgumentException("Invalid hash type: " + hashType.toString());
      }
    }
  }
}
