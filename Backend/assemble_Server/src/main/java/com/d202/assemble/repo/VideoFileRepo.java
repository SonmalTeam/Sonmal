package com.d202.assemble.repo;

import com.d202.assemble.dto.VideoFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoFileRepo extends JpaRepository<VideoFile, Long> {
    void deleteById(Long id);
    Optional<VideoFile> findById(Long id);
}
