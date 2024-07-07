package com.dnsserver.executor;

import com.dnsserver.message.Answer;
import com.dnsserver.message.DnsMessage;
import com.dnsserver.message.Header;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ForwarderExecutor implements RequestExecutor {

  private final int BUFFER_SIZE = 512;

  private final DatagramSocket socket;
  private final SocketAddress remoteAddress;

  public ForwarderExecutor(DatagramSocket socket, String forwardAddress)
      throws UnknownHostException, SocketException {
    this.socket = Objects.requireNonNull(socket);
    remoteAddress = GetSocketAddress(forwardAddress);
  }

  @Override
  public void Send(byte[] data, SocketAddress address) throws IOException {

    var message = DnsMessage.From(data);

    List<Answer> answers = new ArrayList<>();

    // Remote server does not support multi question, so we need to send individual requests
    // and then aggregate decoded answer results

    for (var question : message.questions()) {
      var singleBuff = ByteBuffer.allocate(BUFFER_SIZE);
      message.header().Encode(singleBuff);
      question.Encode(singleBuff);

      // Send to remote server
      var rBytes = singleBuff.array();
      var reqPacket = new DatagramPacket(rBytes, rBytes.length, remoteAddress);
      socket.send(reqPacket);

      // Read from remote server
      singleBuff = ByteBuffer.allocate(BUFFER_SIZE);
      rBytes = singleBuff.array();

      var respPacket = new DatagramPacket(rBytes, rBytes.length, remoteAddress);

      socket.receive(respPacket);

      answers.add(DnsMessage.From(rBytes).answer());
    }

   // Send response local client
    var rBuff = ByteBuffer.allocate(BUFFER_SIZE);
    ResponseHeader(message).Encode(rBuff);
    message.questions().forEach(q -> q.Encode(rBuff));
    answers.forEach(a -> a.Encode(rBuff));

    var localPacket = new DatagramPacket(rBuff.array(), rBuff.array().length, address);
    socket.send(localPacket);
  }


  private SocketAddress GetSocketAddress(String address) {
    var parts = address.split(":");
    if (parts.length != 2) {
      throw new IllegalArgumentException("Invalid address");
    }

    return new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
  }

  private Header ResponseHeader(DnsMessage message) {
    var requestHeader = message.header();
    return new Header(
        requestHeader.packetIdentifier(),
        true,
        requestHeader.operationCode(),
        requestHeader.authoritativeAnswer(),
        requestHeader.truncation(),
        requestHeader.recursionDesired(),
        requestHeader.recursionAvailable(),
        requestHeader.reserved(),
        (byte) requestHeader.responseCode(),
        (short) message.questions().size(),
        (short)message.questions().size(),
        requestHeader.authorityRecordCount(),
        requestHeader.additionalRecordCount()
    );
  }
}
