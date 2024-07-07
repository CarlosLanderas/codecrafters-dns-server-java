package com.dnsserver.executor;

import java.io.IOException;
import java.net.SocketAddress;

public interface RequestExecutor {
  void Send(byte[] data, SocketAddress address) throws IOException;
}


