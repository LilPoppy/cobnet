package com.cobnet.spring.boot.core;

import com.cobnet.interfaces.FileSource;
import com.cobnet.interfaces.spring.repository.FileInfoRepository;
import com.cobnet.spring.boot.configuration.FileSourceConfiguration;
import com.cobnet.spring.boot.entity.FileInfo;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalFileSource implements FileSource {

    private final FileSourceConfiguration configuration;

    LocalFileSource(FileSourceConfiguration configuration) {

        this.configuration = configuration;
    }

    @Override
    public void write(InputStream stream, FileInfo info) throws IOException {

        Path path = Paths.get(configuration.getUrl().getPath());

        Files.createDirectories(path);

        path = path.resolve(info.getHash());

        File file = path.toFile();

        if(file.exists()) {

            file.delete();
        }

        Files.copy(stream, path);
    }

    @Override
    public @Nullable InputStream read(FileInfo info) throws IOException {

        Path path = Paths.get(configuration.getUrl().getPath()).resolve(info.getHash());

        File file = path.toFile();

        if(file.exists() && !file.isDirectory()) {

            return Files.newInputStream(path);
        }

        return null;
    }
}
