package com.d202.assemble.repo;

import com.d202.assemble.dto.UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCategoryRepo extends JpaRepository<UserCategory, Long> {

    Optional<UserCategory> findByUserSeq(Long userSeq);
    void deleteByUserSeq(Long userSeq);
}
