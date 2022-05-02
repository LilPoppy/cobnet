package com.cobnet.spring.boot.entity;

import com.cobnet.common.cipher.HashDigest;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.File;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;

@Entity
public class FileInfo extends EntityBase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String memeType;

    private long size;

    private String hash;

    public FileInfo() {}

    public FileInfo(String name, String memeType, long size) {

        this.name = name;
        this.memeType = memeType;
        this.size = size;
        this.hash = generateHash();
    }

    public FileInfo(MultipartFile file) {

        this(file.getOriginalFilename(), file.getContentType(), file.getSize());
    }

    public FileInfo(File file) {

        this(file.getName(), FilenameUtils.getExtension(file.getName()), file.length());
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMemeType() {
        return memeType;
    }

    public long getSize() {
        return size;
    }

    public String getHash() {
        return hash;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setMemeType(String memeType) {
        this.memeType = memeType;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String generateHash() {

        try {

            return new String(HashDigest.encrypt("MD5", new StringBuilder().append(this.name).append(this.memeType).append(this.hash).toString()));

        } catch (NoSuchAlgorithmException ex) {

            ex.printStackTrace();

            return null;
        }
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", memeType='" + memeType + '\'' +
                ", size=" + size +
                ", hash='" + hash + '\'' +
                '}';
    }
}
