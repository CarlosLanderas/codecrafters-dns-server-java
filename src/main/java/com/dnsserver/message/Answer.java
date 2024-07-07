package com.dnsserver.message;

import java.nio.ByteBuffer;

public class Answer {

  public static void Encode(ByteBuffer buffer, Question question) {
    buffer.put(question.EncodeDomainName());
    buffer.putShort((short) 1); // QTYPE
    buffer.putShort((short) 1); // QCLASS
    buffer.putInt(60);         // TTL
    buffer.putShort((short) 4); // RDLENGTH
    buffer.put(new byte[]{8, 8, 8, 8}); // ip 8.8.8.8
  }
}