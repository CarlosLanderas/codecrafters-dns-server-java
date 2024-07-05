package com.dnsserver;

import java.io.IOException;

public class Main {

  public static void main(String[] args) {

    try (final DnsServer server = new DnsServer(2053)) {
      System.out.println("Starting DNS server on port 2053");
      server.Start();

    } catch (IOException e) {
      System.out.println("Failed to start DNS server: " + e.getMessage());
    }
  }
}
