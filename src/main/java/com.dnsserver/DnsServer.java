package com.dnsserver;

import com.dnsserver.executor.LocalExecutor;
import com.dnsserver.executor.ForwarderExecutor;
import com.dnsserver.executor.RequestExecutor;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class DnsServer implements AutoCloseable {

  private int port = 0;
  private String forwarderAddress = null;
  private DatagramSocket serverSocket;

  public DnsServer(int port, String forwarderAddress) {
    this.port = port;
    this.forwarderAddress = forwarderAddress;
  }

  public void Start() throws IOException {
    serverSocket = new DatagramSocket(port);

    RequestExecutor executor = GetExecutor(serverSocket, forwarderAddress);

    while (!serverSocket.isClosed()) {
      try {
        final byte[] buf = new byte[512];
        final DatagramPacket packet = new DatagramPacket(buf, buf.length);

        serverSocket.receive(packet);

        executor.Send(buf, packet.getSocketAddress());

      } catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
      }
    }
  }

  private RequestExecutor GetExecutor(DatagramSocket socket, String forwarder)
      throws SocketException, UnknownHostException {
    if (forwarder != null) {
      return new ForwarderExecutor(socket, forwarderAddress);
    }

    return new LocalExecutor(serverSocket);
  }

  @Override
  public void close() throws IOException {
    if (!serverSocket.isClosed()) {
      serverSocket.close();
    }
  }
}
