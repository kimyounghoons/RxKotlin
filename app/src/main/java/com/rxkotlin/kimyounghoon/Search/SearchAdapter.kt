package com.rxkotlin.kimyounghoon.Search

import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.rxkotlin.kimyounghoon.DTO.Document
import com.rxkotlin.kimyounghoon.GlideApp
import com.rxkotlin.kimyounghoon.R
import com.rxkotlin.kimyounghoon.databinding.ItemSearchBinding

class SearchAdapter(var items: ArrayList<Document>) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SearchAdapter.SearchViewHolder {
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(viewGroup.context))
        return SearchViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(searchViewHolder: SearchViewHolder, position: Int) = searchViewHolder.bind(items[position])

    class SearchViewHolder(private val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(document: Document) {
            binding.root.context.apply {
                val placeholderColor = this.getColor(R.color.white)
                GlideApp.with(this)
                        .load(Uri.parse(document.imageUrl))
                        .placeholder(ColorDrawable(placeholderColor))
                        .transition(withCrossFade())
                        .into(binding.thumbnailImage)
                binding.date.text = document.dateTime
                binding.title.text = document.imageUrl
            }
        }
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    fun addItem(items: ArrayList<Document>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }
}