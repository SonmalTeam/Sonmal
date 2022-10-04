package com.d202.assemble.repo;

import com.d202.assemble.dto.SignMacro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SignMacroRepo extends JpaRepository<SignMacro, Long> {
    Optional<SignMacro> findBySeq(long seq);

    Page<SignMacro> findAllByUserSeq(long userSeq, Pageable pageable);
    Page<SignMacro> findAllByUserSeqAndCategorySeq(long userSeq, long categorySeq, Pageable pageable);
    Page<SignMacro> findAllByUserSeqOrderByCountDesc(long userSeq, Pageable pageable);
    Page<SignMacro> findAllByUserSeqAndCategorySeqOrderByCountDesc(long userSeq, long categorySeq, Pageable pageable);
    List<SignMacro> findAllByUserSeq(long userSeq);
    Page<SignMacro> findByUserSeqAndTitleStartsWith(long userSeq, String keyword, Pageable pageable);
}
