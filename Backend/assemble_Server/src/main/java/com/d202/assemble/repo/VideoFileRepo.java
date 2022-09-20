package com.d202.assemble.repo;

import com.d202.assemble.dto.VideoFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoFileRepo extends JpaRepository<VideoFile, Long> {
}
