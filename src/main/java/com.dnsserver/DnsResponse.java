package com.dnsserver;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DnsResponse {

  public ByteBuffer Write(Header header, ByteBuffer buffer) {
    String domain = "codecrafters.io";
    header.Write(buffer);
    Question.Write(buffer, domain);
    Answer.Write(buffer, domain);

    return buffer;
  }

  public class Header {

    private short id;
    private short flags;
    private short qdcount = 1;
    private short ancount = 1;
    private short nscount;
    private short arcount;
    private int qr;
    private int opcode;
    private int rd;


    public ByteBuffer Write(ByteBuffer buffer) {
      buffer.putShort(id);
      buffer.putShort(responseFlags(this));
      buffer.putShort(qdcount);
      buffer.putShort(ancount);
      buffer.putShort(nscount);
      buffer.putShort(arcount);
      return buffer;
    }

    public Header Parse(byte[] data) {
      ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);

      id = buffer.getShort();

      flags = buffer.getShort();
      qdcount = buffer.getShort();
      ancount = buffer.getShort();
      nscount = buffer.getShort();
      arcount = buffer.getShort();

      qr = (flags >> 15) & 0b0001;
      opcode = (flags >> 11) & 0b1111;
      rd = (flags >> 8) & 0b0001;
      ancount = 1;

      return this;
    }

    private short responseFlags(Header dnsHeader) {
      short initialValue = (short)0b1000_0000_0000_0000; // qr = 1 => Response
      short response = (short)(initialValue | (dnsHeader.opcode << 11) |
          (dnsHeader.rd << 8));
      if (dnsHeader.opcode != 0) {
        response |= 4;
      }
      return response;
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
