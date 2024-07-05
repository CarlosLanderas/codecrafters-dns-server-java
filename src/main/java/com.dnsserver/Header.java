package com.dnsserver;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;

public class Header {
  public static byte[] Bytes() {
    short ID = (short) 1234;

    final var bitset = new BitSet(8);
    bitset.flip(7); // Set QR to 1

    final var bufResponse = ByteBuffer.allocate(12)
        .order(ByteOrder.BIG_ENDIAN)
        .putShort(ID) // 2 bytes
        .put(bitset.toByteArray()) // 1 byte
        .put((byte) 0) // 1 byte
        .putShort((short) 0) // 2 bytes
        .putShort((short) 0) // 2 bytes
        .putShort((short) 0) // 2 bytes
        .putShort((short) 0) // 2 bytes
        .array(); // 12 bytes

    return bufResponse;
  }
}

