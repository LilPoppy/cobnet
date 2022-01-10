package com.storechain;

import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.hosted.FeatureImpl;
import com.storechain.connection.handler.UnknownPacketHandler;
import com.storechain.connection.netty.websocket.WebSocketClientConverter;
import com.storechain.connection.netty.websocket.handler.WebSocketServerInitializeHandler;
import com.storechain.spring.boot.connection.netty.NettyBaseServerProvider;
import com.storechain.spring.boot.connection.netty.WebSocketServerProvider;

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;

// All the reflection references must defined here before pack native.
@AutomaticFeature
public class RuntimeReflectionRegistrationFeature implements Feature {
	
	public void beforeAnalysis(BeforeAnalysisAccess access) {
		
		FeatureImpl.BeforeAnalysisAccessImpl config = (FeatureImpl.BeforeAnalysisAccessImpl) access;
		
		var metaAccess = config.getMetaAccess();
		
	    try {
	        RuntimeReflection.register(NettyBaseServerProvider.class);
	        RuntimeReflection.register(NettyBaseServerProvider.class.getDeclaredConstructor());
	        RuntimeReflection.register(WebSocketServerProvider.class);
	        RuntimeReflection.register(WebSocketServerProvider.class.getDeclaredConstructor());
	        RuntimeReflection.register(WebSocketServerInitializeHandler.class);
	        RuntimeReflection.register(WebSocketServerInitializeHandler.class.getDeclaredConstructor());
	        RuntimeReflection.register(WebSocketServerInitializeHandler.class.getMethods());
	        RuntimeReflection.register(WebSocketClientConverter.class);
	        RuntimeReflection.register(WebSocketClientConverter.class.getDeclaredConstructor());
	        RuntimeReflection.register(WebSocketClientConverter.class.getMethods());
	        RuntimeReflection.register(UnknownPacketHandler.class);
	        RuntimeReflection.register(UnknownPacketHandler.class.getMethods());
	   
	   
        } catch (NoSuchMethodException e) { 
			// TODO Auto-generated catch block
			e.printStackTrace();
        } catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
}