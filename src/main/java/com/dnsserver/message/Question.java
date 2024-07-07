package com.dnsserver.message;

import java.nio.ByteBuffer;

public record Question(String domain, short QType, short QClass) {

  public void Encode(ByteBuffer buffer) {
    buffer.put(Domain.EncodeDomainName(domain));
    buffer.putShort(QType);
    buffer.putShort(QClass);
  }

  public static Question Parse(ByteBuffer buffer) {
    var encodedName = Domain.DecodeDomainName(buffer);
    return new Question(
        encodedName,
        buffer.getShort(),
        buffer.getShort()
    );
  }
}
