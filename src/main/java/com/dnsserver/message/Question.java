package com.dnsserver.message;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.StringJoiner;

public record Question(String domain, short QType, short QClass) {

  public void Encode(ByteBuffer buffer) {
    buffer.put(EncodeDomainName());
    buffer.putShort(QType);
    buffer.putShort(QClass);
  }

  public static Question Parse(ByteBuffer buffer) {
    var encodedName = DecodeDomainName(buffer);
    return new Question(
        encodedName,
        buffer.getShort(),
        buffer.getShort()
    );
  }

  public byte[] EncodeDomainName() {
    var out = new ByteArrayOutputStream();

    for (String label : domain.split("\\.")) {
      out.write(label.length());
      out.writeBytes(label.getBytes());
    }

    out.write(0);

    return out.toByteArray();
  }

  private static String DecodeDomainName(ByteBuffer buffer) {

    // Compressed message format (two octet)
    // +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    // | 1  1|                OFFSET                   |
    // +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+

    var labels = new StringJoiner(".");
    byte b;
    while ((b = buffer.get()) != 0) {
      if ((b & 0b1100_0000) == 0b1100_0000){
        int pointer = (0b0011_1111 & b) << 8 | buffer.get();
        int currentPosition = buffer.position();
        buffer.position(pointer);
        labels.add(DecodeDomainName(buffer));
        buffer.position(currentPosition);
      } else {
        byte[] dst = new byte[b];
        buffer.get(dst);
        labels.add(new String(dst));
      }
    }

    return labels.toString();
  }
}
