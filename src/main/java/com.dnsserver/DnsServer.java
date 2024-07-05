package com.dnsserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class DnsServer implements AutoCloseable {

  private int port = 0;
  private DatagramSocket serverSocket;

  public DnsServer(int port) {
    this.port = port;
  }

  public void Start() throws IOException {
    serverSocket = new DatagramSocket(port);

    while (true) {
      try {
        final byte[] buf = new byte[512];
        final DatagramPacket packet = new DatagramPacket(buf, buf.length);
        serverSocket.receive(packet);

        System.out.println(
            String.format("Received: %s", new String(buf, StandardCharsets.UTF_8)));

        var buffer = ByteBuffer.allocate(512);

        byte[] responseBytes = new DnsResponse()
            .Write(buffer)
            .array();

        final DatagramPacket packetResponse = new DatagramPacket(responseBytes,
            responseBytes.length, packet.getSocketAddress());

        serverSocket.send(packetResponse);

      } catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
      }
    }
  }

  @Override
  public void close() throws IOException {
    if (!serverSocket.isClosed()) {
      serverSocket.close();
    }
  }
}
