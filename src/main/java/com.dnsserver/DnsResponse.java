package com.dnsserver;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class DnsResponse {

  public ByteBuffer Write(ByteBuffer buffer) {
    String domain = "codecrafters.io";
    new Header().Bytes(buffer);
    Question.Write(buffer, domain);
    Answer.Write(buffer, domain);

    return buffer;
  }

  class Header {

    private short id = 1234;
    private short flags = (short)0b10000000_00000000;
    private short qdCount = 1;
    private short anCount = 1;
    private short nsCount;
    private short arCount;

    public ByteBuffer Bytes(ByteBuffer buffer) {
      buffer.putShort(id);
      buffer.putShort(flags);
      buffer.putShort(qdCount);
      buffer.putShort(anCount);
      buffer.putShort(nsCount);
      buffer.putShort(arCount);
      return buffer;
    }
  }

  class Question {
    public static ByteBuffer Write(ByteBuffer buffer, String domainName) {
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

  class Answer {
    public static ByteBuffer Write(ByteBuffer buffer, String domain) {
      buffer.put(Question.getEncodedDomain(domain));
      buffer.putShort((short) 1); // QTYPE
      buffer.putShort((short) 1); // QCLASS
      buffer.putInt(60); // TTL
      buffer.putShort((short) 4); // RDLENGTH

      buffer.put(new byte[] {8, 8, 8, 8}); // ip 8.8.8.8

      return buffer;
    }
  }
}
