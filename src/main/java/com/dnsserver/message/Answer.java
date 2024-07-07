package com.dnsserver.message;

import java.nio.ByteBuffer;

public record Answer(
    String domain,
    short QType,
    short QClass,
    int TTL,
    short RDLength,
    byte[] RData) {

  public void Encode(ByteBuffer buffer) {
    buffer.put(Domain.EncodeDomainName(domain));
    buffer.putShort(QType);
    buffer.putShort(QClass);
    buffer.putInt(TTL);
    buffer.putShort(RDLength);
    buffer.put(RData);
  }

  public static Answer Parse(ByteBuffer buffer) {
    var domain = Domain.DecodeDomainName(buffer);

    var qType = buffer.getShort();
    var qClass = buffer.getShort();
    var ttl = buffer.getInt();
    var rdLength = buffer.getShort();
    var rData = new byte[rdLength];
    buffer.get(rData);

    return new Answer(
        domain,
        qType,
        qClass,
        ttl,
        rdLength,
        rData
        );
  }
}