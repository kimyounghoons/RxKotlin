package com.rxkotlin.kimyounghoon.DTO

import com.google.gson.annotations.SerializedName

class Meta(
        @SerializedName("total_count")val totalCount: Long,
        @SerializedName("pageable_count")val pageableCount: Long,
        @SerializedName("is_end")val isEnd: Boolean
)