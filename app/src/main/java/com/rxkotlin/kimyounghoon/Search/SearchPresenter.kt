package com.rxkotlin.kimyounghoon.Search

import android.text.TextUtils
import com.jakewharton.rxbinding2.support.v7.widget.RecyclerViewScrollEvent
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent
import com.rxkotlin.kimyounghoon.DTO.SearchDTO
import com.rxkotlin.kimyounghoon.SearchAdapterData
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SearchPresenter(var searchAdapterData: SearchAdapterData, var view: SearchContract.View) : SearchContract.Presenter {

    private var compositeDisposable: CompositeDisposable? = null
    private var paginator = PublishProcessor.create<Int>()
    private var query: String = ""

    override fun onCreate() {
        if (compositeDisposable == null)
            compositeDisposable = CompositeDisposable()
        subscribeForSearch()
    }

    override fun observeRecyclerViewScroll(observable: Observable<RecyclerViewScrollEvent>) {
        compositeDisposable?.add(observable
                .observeOn(AndroidSchedulers.mainThread())
                .filter { s ->
                    // check loading state, list end
                    view.scrolledToBottom() && !searchAdapterData.isLoading && searchAdapterData.isLoadMore
                }
                .subscribe { event ->
                    paginator.onNext(searchAdapterData.nextPage)
                })
    }

    override fun observeEditText(observable: Observable<TextViewTextChangeEvent>) {
        compositeDisposable?.add(observable.debounce(1, TimeUnit.SECONDS)
                .filter {
                    return@filter !TextUtils.isEmpty(it.text())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe {
                    view.clearFocusEditText()
                    view.hideKeyboard()
                    query = it.text().toString()
                    trySearch()
                })
    }

    private fun trySearch() {
        if (!TextUtils.isEmpty(searchAdapterData.query) && query != searchAdapterData.query) {
            view.clearAdapter()
            searchAdapterData.clearData()
        }
        paginator.onNext(searchAdapterData.nextPage)
    }

    private fun subscribeForSearch() {
        compositeDisposable?.add(paginator
                .onBackpressureDrop()
                .concatMap {
                    view.showProgress()
                    searchAdapterData.setLoadState(true)
                    getItemsFromNetwork(it, query)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                }.subscribe({ searchDTO ->
                    searchDTO?.apply {
                        searchAdapterData.setSearchData(searchDTO)
                        loadItems(searchDTO)
                        view.hideProgress()
                        searchAdapterData.setLoadState(false)
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }

    private fun loadItems(searchDTO: SearchDTO) {
        searchDTO.documents.apply {
            if (size > 0) {
                view.add(searchDTO.documents)
                return
            }
        }
        view.showNoResults()
    }

    override fun getItemsFromNetwork(pageCount: Int, query: String): Flowable<SearchDTO> {
        return searchAdapterData.getItemsFromNetwork(pageCount, query)
    }

    override fun onDestroy() {
        compositeDisposable?.dispose()
    }
}