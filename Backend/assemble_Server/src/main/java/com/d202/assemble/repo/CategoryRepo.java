package com.d202.assemble.repo;

import com.d202.assemble.dto.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Category, Long> {
    Category findBySeq(Long seq);
}
