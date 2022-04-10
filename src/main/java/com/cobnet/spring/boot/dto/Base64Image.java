package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.Transmission;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Base64;

public class Base64Image implements Transmission<String>, Serializable {

    private String image;

    public Base64Image(BufferedImage image, String format, Charset charset) throws IOException {

        this.image = encode(image, format, charset);
    }

    public Base64Image(BufferedImage image, String format) throws IOException {

        this(image, format, Charset.defaultCharset());
    }

    @JsonIgnore
    @Override
    public String getData() {
        return null;
    }

    public String getImage() {
        return image;
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
