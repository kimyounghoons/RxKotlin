package com.rxkotlin.kimyounghoon.Search

import android.text.TextUtils
import com.jakewharton.rxbinding2.support.v7.widget.RecyclerViewScrollEvent
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent
import com.rxkotlin.kimyounghoon.DTO.SearchDTO
import com.rxkotlin.kimyounghoon.SearchAdapterData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SearchPresenter(var searchAdapterData: SearchAdapterData, var view: SearchContract.View) : SearchContract.Presenter {

    private var compositeDisposable: CompositeDisposable? = null

    override fun onCreate() {
        if (compositeDisposable == null)
            compositeDisposable = CompositeDisposable()
    }

    override fun observeRecyclerViewScroll(observable: Observable<RecyclerViewScrollEvent>) {
        compositeDisposable?.add(observable
                .observeOn(AndroidSchedulers.mainThread())
                .filter { s ->
                    // check loading state, list end
                    view.scrolledToBottom() && !searchAdapterData.isLoading && searchAdapterData.isLoadMore
                }
                .subscribe { event ->
                    trySearch(searchAdapterData.query)
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
                    trySearch(it.text().toString())
                })
    }

    private fun trySearch(query: String) {
        if (!TextUtils.isEmpty(searchAdapterData.query) && query != searchAdapterData.query) {
            view.clearAdapter()
            searchAdapterData.clearData()
        }
        compositeDisposable?.add(searchAdapterData.getItemsFromNetwork(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    view.showProgress()
                    searchAdapterData.setLoadState(true)
                }.doOnTerminate {
                    view.hideProgress()
                    searchAdapterData.setLoadState(false)
                }
                .subscribe({ searchDTO ->
                    searchAdapterData.setSearchData(searchDTO)
                    loadItems(searchDTO)
                }, { throwable ->
                    throwable.printStackTrace()
                })
        )
    }

    private fun loadItems(searchDTO: SearchDTO?) {
        searchDTO?.documents?.apply {
            if (size > 0) {
                view.loadItems(searchDTO.documents)
                return
            }
        }
        view.showNoResults()
    }

    override fun getItemsFromNetwork(query: String): Observable<SearchDTO> {
        return searchAdapterData.getItemsFromNetwork(query)
    }

    override fun onDestroy() {
        compositeDisposable?.dispose()
    }
}