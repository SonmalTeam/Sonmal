package com.d202.sonmal.model.paging

data class PagingResult<T>(var page: Int, var totalPage: Int, var result: List<T>) {
}