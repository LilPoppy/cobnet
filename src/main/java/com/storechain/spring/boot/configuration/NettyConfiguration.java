package com.storechain.spring.boot.configuration;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import com.storechain.Endian;
import com.storechain.connection.netty.NettyServer;

import io.netty.channel.ChannelOption;

@Configuration
@ConfigurationProperties("netty")
public class NettyConfiguration {
	
	private boolean enable;
	
	private List<ServerConfiguration> servers = new ArrayList<ServerConfiguration>();

	public List<ServerConfiguration> getServers() {
		return servers;
	}
	
	public ServerConfiguration getServerConfig(String name) {
		
		for(ServerConfiguration config : servers) {
			
			if(config.name.toUpperCase().equals(name.toUpperCase())) {
				
				return config;
			}
		}
		
		return null;
	}
	
	public ServerConfiguration getServerConfig(NettyServer server) { 
		return getServerConfig(server.getName());
	}

	public void setServers(List<ServerConfiguration> servers) {
		this.servers = servers;
	}
	
	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public static class ServerConfiguration {
		
		private String name = "unknown";
		
		private int port = 8090;
		
		private String provider = "com.storechain.spring.boot.connection.netty.NettyBaseServerProvider";
		
		private String clientConverter = "com.storechain.connection.netty.NettyBaseClientConverter";
		
		private String handler = "io.netty.handler.logging.LoggingHandler";
		
		private String subHandler;
		
		private List<Map<ChannelOption, Object>> options;
		
		private List<Map<ChannelOption, Object>> childOptions;
		
		private Endian encodeEndian = Endian.LITTLE;
		
		private Endian decodeEndian = Endian.LITTLE;
		
		private String charset = "UTF-8";
		
		private boolean logUserActive;
		
		private boolean logUserInactive;
		
		private boolean logChannelRead;
		
		private boolean logChannelWrite;
		
		private boolean kickUnknownPacketUser;
		
		private boolean logBannedIpActive;
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public Endian getEncodeEndian() {
			return encodeEndian;
		}

		public void setEncodeEndian(Endian encodeEndian) {
			this.encodeEndian = encodeEndian;
		}

		public Endian getDecodeEndian() {
			return decodeEndian;
		}

		public void setDecodeEndian(Endian decodeEndian) {
			this.decodeEndian = decodeEndian;
		}

		public Charset getCharset() {
			return Charset.availableCharsets().get(charset);
		}

		public void setCharset(String charset) {
			this.charset = charset;
		}

		public boolean isLogUserActive() {
			return logUserActive;
		}

		public void setLogUserActive(boolean logUserActive) {
			this.logUserActive = logUserActive;
		}

		public boolean isLogUserInactive() {
			return logUserInactive;
		}

		public void setLogUserInactive(boolean logUserInactive) {
			this.logUserInactive = logUserInactive;
		}

		public boolean isLogChannelRead() {
			return logChannelRead;
		}

		public void setLogChannelRead(boolean logChannelRead) {
			this.logChannelRead = logChannelRead;
		}

		public boolean isLogChannelWrite() {
			return logChannelWrite;
		}

		public void setLogChannelWrite(boolean logChannelWrite) {
			this.logChannelWrite = logChannelWrite;
		}

		public boolean isKickUnknownPacketUser() {
			return kickUnknownPacketUser;
		}

		public void setKickUnknownPacketUser(boolean kickUnknownPacketUser) {
			this.kickUnknownPacketUser = kickUnknownPacketUser;
		}

		public boolean isLogBannedIpActive() {
			return logBannedIpActive;
		}

		public void setLogBannedIpActive(boolean logBannedIpActive) {
			this.logBannedIpActive = logBannedIpActive;
		}

		public Class<?> getSubHandler() throws ClassNotFoundException {
			
			return subHandler == null || subHandler.length() == 0 ? null : Class.forName(subHandler);
		}

		public void setSubHandler(String subHandler) {
			this.subHandler = subHandler;
		}
		
		public Entry<ChannelOption, Object>[] getOptions() {
			Map<ChannelOption, Object> map = this.options != null && this.options.size() > 0 ? this.options.get(0) : new HashMap<ChannelOption, Object>();
			
			Entry<ChannelOption, Object>[] array = new Entry[map.size()];

			return map.entrySet().toArray(array);
		}

		public void setOptions(List<Map<ChannelOption, Object>> options) {
			this.options = options;
		}

		public Entry<ChannelOption, Object>[] getChildOptions() {
			Map<ChannelOption, Object> map = this.childOptions != null && this.childOptions.size() > 0 ? this.childOptions.get(0) : new HashMap<ChannelOption, Object>();;
			
			Entry<ChannelOption, Object>[] array = new Entry[map.size()];

			return map.entrySet().toArray(array);
		}

		public void setChildOptions(List<Map<ChannelOption, Object>> childOptions) {
			this.childOptions = childOptions;
		}

		public Class<?> getProvider() throws ClassNotFoundException {
			return Class.forName(provider);
		}

		public void setProvider(String provider) {
			this.provider = provider;
		}

		public Class<?> getClientConverter() throws ClassNotFoundException {
			return Class.forName(clientConverter);
		}

		public void setClientConverter(String clientConverter) {
			this.clientConverter = clientConverter;
		}

		public Class<?> getHandler() throws ClassNotFoundException {
			return Class.forName(handler);
		}

		public void setHandler(String handler) {
			this.handler = handler;
		}

	}
	


}