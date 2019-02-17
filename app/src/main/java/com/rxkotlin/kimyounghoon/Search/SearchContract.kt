package com.rxkotlin.kimyounghoon.Search

import com.jakewharton.rxbinding2.support.v7.widget.RecyclerViewScrollEvent
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent
import com.rxkotlin.kimyounghoon.DTO.Document
import com.rxkotlin.kimyounghoon.DTO.SearchDTO
import io.reactivex.Flowable
import io.reactivex.Observable

interface SearchContract {
    interface View {
        fun clearFocusEditText()
        fun hideKeyboard()
        fun showProgress()
        fun hideProgress()
        fun scrolledToBottom(): Boolean
        fun clearAdapter()
        fun add(documents: List<Document>)
        fun showNoResults()
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun observeRecyclerViewScroll(observable: Observable<RecyclerViewScrollEvent>)
        fun observeEditText(observable: Observable<TextViewTextChangeEvent>)
        fun getItemsFromNetwork(pageCount: Int, query: String): Flowable<SearchDTO>
    }
}