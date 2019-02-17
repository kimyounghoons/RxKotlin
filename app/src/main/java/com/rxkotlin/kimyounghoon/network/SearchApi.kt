package com.rxkotlin.kimyounghoon.network

import com.rxkotlin.kimyounghoon.DTO.SearchDTO
import io.reactivex.Flowable
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    @GET("v2/search/image?")
    fun getSearchImages(
            @Query("query") query: String,
            @Query("page") page: Int,
            @Query("size") size: Int): Observable<SearchDTO>
}