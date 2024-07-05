package com.dnsserver;

import java.nio.ByteBuffer;

public class Header {

  private short id = 1234;
  private short flags = (short)0b10000000_00000000;
  private short qdCount = 1;
  private short anCount;
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

