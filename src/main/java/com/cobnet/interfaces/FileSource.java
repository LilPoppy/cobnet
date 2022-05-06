package com.cobnet.interfaces;

import com.cobnet.spring.boot.entity.FileInfo;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.*;

public interface FileSource {

    public void write(@NotNull InputStream stream, @NotNull FileInfo info) throws IOException;

    public default void write(@NotNull InputStream stream, @NotNull File file) throws IOException {

        this.write(stream, new FileInfo(file));
    }

    public default void write(@NotNull InputStream stream, @NotNull MultipartFile... files) throws IOException {

        for(MultipartFile file : files) {

            this.write(stream, new FileInfo(file));
        }
    }

    public default void write(@NotNull ByteArrayOutputStream stream, @NotNull FileInfo info) throws IOException {

        this.write(new ByteArrayInputStream(stream.toByteArray()), info);
    }

    public @Nullable InputStream read(@NotNull FileInfo info) throws IOException;
}
