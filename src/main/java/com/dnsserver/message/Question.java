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
    var joiner = new StringJoiner(".");
    while(buffer.hasRemaining()) {
      var length = buffer.get();
      if (length == 0) {
        break;
      }
      var label = new byte[length];
      buffer.get(label);
      joiner.add(new String(label));
    }

    return joiner.toString();
  }

  private int Length() {
    return EncodeDomainName().length + 4;
  }
}
