package com.dnsserver.executor;

import com.dnsserver.message.DnsMessage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Objects;

public class LocalExecutor implements RequestExecutor {

  private final DatagramSocket socket;

  public LocalExecutor(DatagramSocket socket) {
    this.socket = Objects.requireNonNull(socket);
  }

  @Override
  public void Send(byte[] data, SocketAddress address) throws IOException {
    var message = DnsMessage.From(data);
    var bytes = message.Write();
    var packet = new DatagramPacket(bytes, bytes.length, address);

    socket.send(packet);
  }
}
