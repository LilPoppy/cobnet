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
import java.util.Arrays;

public class LocalFileSource implements FileSource {

    private final FileInfoRepository repository;
    private final FileSourceConfiguration configuration;

    LocalFileSource(FileInfoRepository repository, FileSourceConfiguration configuration) {

        this.repository = repository;
        this.configuration = configuration;
    }

    @Override
    public void write(InputStream stream, FileInfo info) throws IOException {

        if(info.getHash() == null) {

            info.setHash(info.generateHash());
        }

        String[] nodes = info.getName().split("/");

        Path path = Paths.get(configuration.getUrl().getPath());

        if(nodes.length > 1) {

            path = path.resolve(String.join("/", Arrays.copyOfRange(nodes, 0, nodes.length - 1)));
        }

        Files.createDirectories(path);

        File file = path.resolve(info.getHash()).toFile();

        if(file.exists()) {

            file.delete();
        }

        info.setHash(info.generateHash());

        path = path.resolve(info.getHash());

        file = path.toFile();

        if(file.exists()) {

            file.delete();
        }

        Files.copy(stream, path);

        repository.save(info);
    }

    @Override
    public @Nullable InputStream read(FileInfo info) throws IOException {

        if(info.getHash() == null) {

            info.setHash(info.generateHash());

            repository.save(info);
        }

        String[] nodes = info.getName().split("/");

        Path path = Paths.get(configuration.getUrl().getPath());

        if(nodes.length > 1) {

            path = path.resolve(String.join("/", Arrays.copyOfRange(nodes,0,nodes.length - 1)));
        }

        path = path.resolve(info.getHash());

        File file = path.toFile();

        if(file.exists() && !file.isDirectory()) {

            return Files.newInputStream(path);
        }

        return null;
    }
}
