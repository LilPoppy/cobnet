package com.cobnet.spring.boot.dto.support;

import com.cobnet.spring.boot.dto.MessageWrapper;
import com.cobnet.spring.boot.dto.ObjectWrapper;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.util.Iterator;

public final class DTOModule extends SimpleModule {

    public DTOModule() {

        super(PackageVersion.VERSION);

        this.addDeserializer(MessageWrapper.class, new MessageWrapperDeserializer());
        this.addSerializer(MessageWrapper.class, new MessageWrapperSerializer());

        this.addDeserializer(ObjectWrapper.class, new ObjectWrapperDeserializer());
        this.addSerializer(ObjectWrapper.class, new ObjectWrapperSerializer());
    }

}

