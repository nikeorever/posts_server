package cn.nikeo.server.models

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

data class Post(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("date") val date: DateTime,
    @SerializedName("tags") val tags: String?,
    @SerializedName("path") val path: String,
    @SerializedName("enable") val enable: Boolean,
    @SerializedName("category_id") val categoryId: Int
)