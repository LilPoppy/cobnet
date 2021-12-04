package com.storechain.configuration;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.storechain.Endian;

public class ServerConfiguration {

	public static boolean ENABLE_LOG_WEBSOCKET_CHANNEL_ACTIVE = true;
	
	public static boolean ENABLE_LOG_WEBSOCKET_CHANNEL_INACTIVE = true;
	
	public static boolean ENABLE_LOG_WEBSOCKET_CHANNEL_READ = true;
	
	public static Endian WEBSOCKET_DEFAULT_ENCODE_ENDIAN = Endian.BIG;
	
	public static Endian WEBSOCKET_DEFAULT_DECODE_ENDIAN = Endian.BIG;
	
	public static Charset WEBSOCKET_DEFAULT_CHARSET = StandardCharsets.UTF_8;
}
