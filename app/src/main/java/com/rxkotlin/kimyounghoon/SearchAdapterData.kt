package com.rxkotlin.kimyounghoon

import com.rxkotlin.kimyounghoon.DTO.SearchDTO
import com.rxkotlin.kimyounghoon.network.RetrofitClient
import com.rxkotlin.kimyounghoon.network.SearchApi
import io.reactivex.Flowable


class SearchAdapterData {
    private val searchService: SearchApi = RetrofitClient.getInstance().create(SearchApi::class.java)
    private var searchDTO: SearchDTO? = null
    var query = DEFAULT_QUERY
        private set
    private var pageCount = 0
    var isLoading = false
        private set
    val isLoadMore: Boolean
        get() = searchDTO != null && !searchDTO!!.meta.isEnd
    val nextPage: Int
        get() = pageCount + 1

    fun getItemsFromNetwork(pageCount: Int, query: String): Flowable<SearchDTO> {
        this.pageCount = pageCount
        this.query = query
        return searchService.getSearchImages(query, pageCount, DEFAULT_ITEM_SIZE)
    }

    fun setSearchData(searchDTO: SearchDTO) {
        this.searchDTO = searchDTO
    }

    fun clearData() {
        searchDTO = null
        query = DEFAULT_QUERY
        pageCount = 0
        isLoading = false
    }

    fun setLoadState(isLoading: Boolean) {
        this.isLoading = isLoading
    }

    companion object {
        private val DEFAULT_ITEM_SIZE = 20
        private val DEFAULT_QUERY = ""
    }

}