package com.dnsserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Main {
  public static void main(String[] args){

    System.out.println("Starting DNS server");

    Header header = new Header();

    try(DatagramSocket serverSocket = new DatagramSocket(2053)) {
      while(true) {
        final byte[] buf = new byte[512];
        final DatagramPacket packet = new DatagramPacket(buf, buf.length);
        serverSocket.receive(packet);

        System.out.println(String.format("Received: %s", new String(buf, StandardCharsets.UTF_8)));

        var buffer = ByteBuffer.allocate(512);
        new Header().Bytes(buffer);
        Question.WriteQuestion(buffer, "codecrafters.io");

        byte[] responseBytes = buffer.array();

        final DatagramPacket packetResponse = new DatagramPacket(responseBytes, responseBytes.length, packet.getSocketAddress());
        serverSocket.send(packetResponse);
      }
    } catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
    }
  }
}
