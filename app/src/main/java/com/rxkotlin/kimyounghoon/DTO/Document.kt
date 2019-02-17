package com.rxkotlin.kimyounghoon.DTO

import com.google.gson.annotations.SerializedName

class Document(@SerializedName("image_url")val imageUrl: String,
               @SerializedName("datetime")val dateTime: String)