package com.rxkotlin.kimyounghoon.Search

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.rxkotlin.kimyounghoon.DTO.Document
import com.rxkotlin.kimyounghoon.R
import com.rxkotlin.kimyounghoon.SearchAdapterData
import com.rxkotlin.kimyounghoon.databinding.ActivityMainBinding


class SearchActivity : AppCompatActivity(), SearchContract.View {

    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var presenter: SearchContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        activityMainBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = SearchAdapter(ArrayList())
        }
        presenter = SearchPresenter(SearchAdapterData(), this)
        presenter.onCreate()
        presenter.observeRecyclerViewScroll(RxRecyclerView.scrollEvents(activityMainBinding.recyclerView))
        presenter.observeEditText(RxTextView.textChangeEvents(activityMainBinding.searchEdit))
    }

    override fun scrolledToBottom(): Boolean {
        return !activityMainBinding.recyclerView.canScrollVertically(1)
    }

    override fun clearAdapter() {
        (activityMainBinding.recyclerView.adapter as SearchAdapter).apply {
            clear()
        }
    }

    override fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activityMainBinding.searchEdit.applicationWindowToken, 0)
    }

    override fun clearFocusEditText() {
        activityMainBinding.searchEdit.clearFocus()
    }

    override fun showProgress() {
        activityMainBinding.progress.visibility = VISIBLE
    }

    override fun hideProgress() {
        activityMainBinding.progress.visibility = GONE
    }

    override fun loadItems(documents: List<Document>) {
        (activityMainBinding.recyclerView.adapter as SearchAdapter).apply {
            addItem(documents as ArrayList<Document>)
        }
    }

    override fun showNoResults() {
        Toast.makeText(this, "noResult", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}