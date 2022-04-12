package com.cobnet.spring.boot.controller.handler;

import com.cobnet.connection.support.InboundPacket;
import com.cobnet.connection.support.NettyChannel;
import com.cobnet.connection.websocket.WebSocketServer;
import com.cobnet.interfaces.connection.AuthenticatableChannel;
import com.cobnet.interfaces.connection.EventListener;
import com.cobnet.interfaces.connection.annotation.EventHandler;
import com.cobnet.interfaces.security.Account;
import com.cobnet.spring.boot.configuration.SecurityConfiguration;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.ConnectionToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class ChannelAuthenticationHandler implements EventListener {

	private static Logger LOG = LoggerFactory.getLogger(ChannelAuthenticationHandler.class);

	@EventHandler(value = InboundOperation.AUTHENTICATION, allowed = WebSocketServer.class)
	@Override
	public boolean onEvent(NettyChannel channel, InboundPacket packet) {

		String username = packet.decodeText();

		String token = packet.decodeText();

		if(channel instanceof AuthenticatableChannel authentication) {

			for (Object principal : ProjectBeanHolder.getSessionRegistry().getAllPrincipals()) {

				if (principal instanceof Account account) {
					//TODO change token store place into cache
					Map<String, ? extends Session> sessions = ProjectBeanHolder.getRedisIndexedSessionRepository().findByPrincipalName(username);

					for (String key : sessions.keySet()) {

						if (((ConnectionToken)sessions.get(key).getAttribute(SecurityConfiguration.CONNECTION_TOKEN)).token().equals(token)) {

							authentication.setAccount(account);
							LOG.info(String.format("%s has logged in as '%s' successfully.", channel.getRemoteAddress(), account.getUsername()));
							//TODO reply success packet and new token
							return true;
						}
					}
				}
			}
		}

		LOG.warn(String.format("%s attempt to authenticate with unmatched token %s.", username, token));
		channel.close();

		return true;
	}


}
