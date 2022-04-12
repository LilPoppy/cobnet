package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;

public record Base64Image(String image, String format, String charset) implements ApplicationJson {

    public Base64Image(BufferedImage image, String format, Charset charset) throws IOException {

        this(encode(image, format, charset), format, charset.name());
    }

    public Base64Image(BufferedImage image, String format) throws IOException {

        this(image, format, Charset.defaultCharset());
    }

    @Override
    public String image() {
        return image;
    }

    @Override
    public String format() {
        return format;
    }

    @Override
    public String charset() {
        return charset;
    }

    public static String encode(BufferedImage image, String format, Charset charset) throws IOException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        ImageIO.write(image, format, stream);

        return new String(Base64.getEncoder().encode(stream.toByteArray()), charset).trim().replaceAll("\r|\n", "");
    }

    public static String encode(BufferedImage image, String format) throws IOException {

        return encode(image, format, Charset.defaultCharset());
    }
}
