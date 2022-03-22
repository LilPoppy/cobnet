package com.cobnet.spring.boot.service;

import com.cobnet.EntryPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class RedisMessageListener implements MessageListener {

    private final static Logger LOG = LoggerFactory.getLogger(EntryPoint.class);

    @Override
    public void onMessage(Message message, byte[] pattern) {

        LOG.debug(String.format("Unhandled Redis message(%s): %s", new String(pattern), message));
    }
}
