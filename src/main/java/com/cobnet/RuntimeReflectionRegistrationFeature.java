package com.cobnet;

import com.cobnet.interfaces.connection.annotation.EventHandler;
import com.cobnet.interfaces.security.annotation.AccessSecured;
import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.hosted.FeatureImpl;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

// All the reflection references must defined here before pack native.
@AutomaticFeature
public class RuntimeReflectionRegistrationFeature implements Feature {
	
	public void beforeAnalysis(BeforeAnalysisAccess access) {
		
		FeatureImpl.BeforeAnalysisAccessImpl config = (FeatureImpl.BeforeAnalysisAccessImpl) access;
		
		var metaAccess = config.getMetaAccess();
		
	    try {
			RuntimeReflection.register(AccessSecured.class);
			RuntimeReflection.register(EventHandler.class);


        }  catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
    }


	
}