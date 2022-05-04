package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Base64;

public record Base64File(String encoded, String format, String charset) implements ApplicationJson {

    public Base64File(InputStream stream, String format, Charset charset) throws IOException {

        this(encode(stream, charset), format, charset.name());
    }

    public Base64File(File file, String format, Charset charset) throws IOException {

        this(Files.newInputStream(file.toPath()), format, charset);
    }

    public Base64File(FileInfo info, String format, Charset charset) throws IOException {

        this(ProjectBeanHolder.getFileSource().read(info), format, charset);
    }

    public Base64File(InputStream stream, String format) throws IOException {

        this(stream, format, Charset.defaultCharset());
    }

    public String encoded() {
        return encoded;
    }

    @Override
    public String charset() {
        return charset;
    }

    public static String encode(InputStream stream, Charset charset) throws IOException {

        return new String(Base64.getEncoder().encode(stream.readAllBytes()), charset).trim().replaceAll("\r|\n", "");
    }
}
