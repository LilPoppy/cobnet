package com.cobnet;

import com.cobnet.interfaces.security.annotation.AccessSecured;
import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.hosted.FeatureImpl;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

// All the reflection references must defined here before pack native.
@AutomaticFeature
public class RuntimeReflectionRegistrationFeature implements Feature {
	
	public void beforeAnalysis(BeforeAnalysisAccess access) {
		
		FeatureImpl.BeforeAnalysisAccessImpl config = (FeatureImpl.BeforeAnalysisAccessImpl) access;
		
		var metaAccess = config.getMetaAccess();
		
	    try {
			RuntimeReflection.register(AccessSecured.class);
//			RuntimeReflection.register(AbstractHandlerMethodMapping.class);
//			RuntimeReflection.register(AbstractHandlerMethodMapping.class.getConstructors());
//			RuntimeReflection.register(AbstractHandlerMethodMapping.class.getDeclaredClasses());
//			RuntimeReflection.register(RequestMappingHandlerMapping.class);


        }  catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
    }
	
}