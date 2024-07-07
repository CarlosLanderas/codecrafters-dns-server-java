package com.dnsserver;

import java.io.IOException;
import java.util.Arrays;

public class Main {

  public static void main(String[] args) {

    String forwardServer = null;

    if (Arrays.asList(args).contains("--resolver")) {
      forwardServer = args[1];
      System.out.println("Forwarding DNS requests to: " + forwardServer);
    }

    try (final DnsServer server = new DnsServer(2053, forwardServer)) {
      System.out.println("Starting DNS server on port 2053");
      server.Start();

    } catch (IOException e) {
      System.out.println("Failed to start DNS server: " + e.getMessage());
    }
  }
}
