package com.d202.assemble.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagingResult<T> {
    private int page;
    private int totalPage;
    private List<T> result;
}
