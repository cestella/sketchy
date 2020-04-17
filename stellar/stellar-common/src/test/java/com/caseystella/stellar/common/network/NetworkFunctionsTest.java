/**
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
package com.caseystella.stellar.common.network;

import com.caseystella.stellar.common.utils.StellarProcessorUtils;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import static com.caseystella.stellar.common.utils.StellarProcessorUtils.runWithArguments;

public class NetworkFunctionsTest {

  @Test
  public void inSubnetTest_positive() {
    StellarProcessorUtils.runWithArguments("IN_SUBNET",
        ImmutableList.of("192.168.0.1", "192.168.0.0/24"), true);
  }

  @Test
  public void inSubnetTest_negative() {
    StellarProcessorUtils.runWithArguments("IN_SUBNET",
        ImmutableList.of("192.168.1.1", "192.168.0.0/24"), false);
  }

  @Test
  public void inSubnetTest_multiple() {
    StellarProcessorUtils.runWithArguments("IN_SUBNET",
        ImmutableList.of("192.168.1.1", "192.168.0.0/24", "192.168.1.0/24"), true);
  }

  @Test
  public void removeSubdomainsTest() {
    StellarProcessorUtils.runWithArguments("DOMAIN_REMOVE_SUBDOMAINS", "www.google.co.uk",
        "google.co.uk");
    StellarProcessorUtils.runWithArguments("DOMAIN_REMOVE_SUBDOMAINS", "www.google.com",
        "google.com");
    StellarProcessorUtils.runWithArguments("DOMAIN_REMOVE_SUBDOMAINS", "com", "com");
  }

  @Test
  public void removeSubdomainsTest_tld_square() {
    StellarProcessorUtils.runWithArguments("DOMAIN_REMOVE_SUBDOMAINS", "com.com", "com.com");
    StellarProcessorUtils.runWithArguments("DOMAIN_REMOVE_SUBDOMAINS", "net.net", "net.net");
    StellarProcessorUtils.runWithArguments("DOMAIN_REMOVE_SUBDOMAINS", "co.uk.co.uk", "uk.co.uk");
    StellarProcessorUtils.runWithArguments("DOMAIN_REMOVE_SUBDOMAINS", "www.subdomain.com.com",
        "com.com");
  }

  @Test
  public void removeSubdomainsTest_unknowntld() {
    StellarProcessorUtils.runWithArguments("DOMAIN_REMOVE_SUBDOMAINS", "www.subdomain.google.gmail",
        "google.gmail");
  }

  @Test
  public void toTldTest() {
    StellarProcessorUtils.runWithArguments("DOMAIN_TO_TLD", "www.google.co.uk", "co.uk");
    StellarProcessorUtils.runWithArguments("DOMAIN_TO_TLD", "www.google.com", "com");
    StellarProcessorUtils.runWithArguments("DOMAIN_TO_TLD", "com", "com");
  }

  @Test
  public void toTldTest_tld_square() {
    StellarProcessorUtils.runWithArguments("DOMAIN_TO_TLD", "com.com", "com");
    StellarProcessorUtils.runWithArguments("DOMAIN_TO_TLD", "net.net", "net");
    StellarProcessorUtils.runWithArguments("DOMAIN_TO_TLD", "co.uk.co.uk", "co.uk");
    StellarProcessorUtils.runWithArguments("DOMAIN_TO_TLD", "www.subdomain.com.com", "com");
  }

  @Test
  public void toTldTest_unknowntld() {
    StellarProcessorUtils.runWithArguments("DOMAIN_TO_TLD", "www.subdomain.google.gmail", "gmail");
  }

  @Test
  public void removeTldTest() {
    StellarProcessorUtils.runWithArguments("DOMAIN_REMOVE_TLD", "google.com", "google");
    StellarProcessorUtils.runWithArguments("DOMAIN_REMOVE_TLD", "www.google.co.uk", "www.google");
    StellarProcessorUtils.runWithArguments("DOMAIN_REMOVE_TLD", "www.google.com", "www.google");
    StellarProcessorUtils.runWithArguments("DOMAIN_REMOVE_TLD", "com", "");
  }

  @Test
  public void removeTldTest_tld_square() {
    StellarProcessorUtils.runWithArguments("DOMAIN_REMOVE_TLD", "com.com", "com");
    StellarProcessorUtils.runWithArguments("DOMAIN_REMOVE_TLD", "net.net", "net");
    StellarProcessorUtils.runWithArguments("DOMAIN_REMOVE_TLD", "co.uk.co.uk", "co.uk");
    StellarProcessorUtils.runWithArguments("DOMAIN_REMOVE_TLD", "www.subdomain.com.com",
        "www.subdomain.com");
  }

  @Test
  public void removeTldTest_unknowntld() {
    StellarProcessorUtils.runWithArguments("DOMAIN_REMOVE_TLD", "www.subdomain.google.gmail",
        "www.subdomain.google");
  }

  @Test
  public void urlToPortTest() {
    StellarProcessorUtils.runWithArguments("URL_TO_PORT", "http://www.google.com/foo/bar", 80);
    StellarProcessorUtils.runWithArguments("URL_TO_PORT", "https://www.google.com/foo/bar", 443);
    StellarProcessorUtils.runWithArguments("URL_TO_PORT", "http://www.google.com:7979/foo/bar",
        7979);
  }


  @Test
  public void urlToPortTest_unknowntld() {
    StellarProcessorUtils.runWithArguments("URL_TO_PORT", "http://www.google.gmail/foo/bar", 80);
  }

  @Test
  public void urlToHostTest() {
    StellarProcessorUtils.runWithArguments("URL_TO_HOST", "http://www.google.com/foo/bar",
        "www.google.com");
    StellarProcessorUtils.runWithArguments("URL_TO_HOST", "https://www.google.com/foo/bar",
        "www.google.com");
    StellarProcessorUtils.runWithArguments("URL_TO_HOST", "http://www.google.com:7979/foo/bar",
        "www.google.com");
    StellarProcessorUtils.runWithArguments("URL_TO_HOST", "http://localhost:8080/a", "localhost");
  }


  @Test
  public void urlToHostTest_unknowntld() {
    StellarProcessorUtils.runWithArguments("URL_TO_HOST", "http://www.google.gmail/foo/bar",
        "www.google.gmail");
  }

  @Test
  public void urlToProtocolTest() {
    StellarProcessorUtils.runWithArguments("URL_TO_PROTOCOL", "http://www.google.com/foo/bar",
        "http");
    StellarProcessorUtils.runWithArguments("URL_TO_PROTOCOL", "https://www.google.com/foo/bar",
        "https");
  }


  @Test
  public void urlToProtocolTest_unknowntld() {
    StellarProcessorUtils.runWithArguments("URL_TO_PROTOCOL", "http://www.google.gmail/foo/bar",
        "http");
  }

  @Test
  public void urlToPathTest() {
    StellarProcessorUtils.runWithArguments("URL_TO_PATH", "http://www.google.com/foo/bar",
        "/foo/bar");
    StellarProcessorUtils.runWithArguments("URL_TO_PATH", "https://www.google.com/foo/bar",
        "/foo/bar");
  }


  @Test
  public void urlToPathTest_unknowntld() {
    StellarProcessorUtils.runWithArguments("URL_TO_PATH", "http://www.google.gmail/foo/bar",
        "/foo/bar");
  }


}
