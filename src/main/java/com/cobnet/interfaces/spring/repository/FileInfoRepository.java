package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.entity.FileInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileInfoRepository extends JPABaseRepository<FileInfo, Long> {

    @Cacheable("FileInfos")
    public Optional<FileInfo> findFileInfoByName(String name);

    @CacheEvict(cacheNames = "FileInfos", key = "#info.getName()")
    @Override
    void delete(FileInfo info);

    @CacheEvict(cacheNames = "FileInfos", key = "#info.getName()")
    @Override
    <S extends FileInfo> S save(S info);
}
