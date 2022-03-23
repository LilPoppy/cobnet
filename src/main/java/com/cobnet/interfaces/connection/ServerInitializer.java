package com.cobnet.interfaces.connection;

import com.cobnet.connection.NettyServer;

public interface ServerInitializer<T extends NettyServer<?>> {

    public T getServer();
}
