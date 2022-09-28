package com.d202.assemble.repo;

import com.d202.assemble.dto.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepo extends JpaRepository<Category, Long> {
    Category findBySeq(Long seq);

    List<Category> findAll();
}
