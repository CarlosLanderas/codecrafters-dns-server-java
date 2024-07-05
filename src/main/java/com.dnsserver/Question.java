package com.dnsserver;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class Question {

  public static ByteBuffer WriteQuestion(ByteBuffer buffer, String domainName) {
    buffer.put(getEncodedDomain(domainName));
    buffer.putShort((short) 1); // QTYPE
    buffer.putShort((short) 1); // QCLASS

    return buffer;

  }

  private static byte[] getEncodedDomain(String domain) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    for (String label: domain.split("\\.")) {
      out.write(label.length());
      out.writeBytes(label.getBytes());
    }

    out.write(0);

    return out.toByteArray();
  }
}
