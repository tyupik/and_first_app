package ru.netology.nmedia

import android.widget.ImageView

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: ImageView? = null,
    val content: String,
    val published: String,
    var likeByMe: Boolean = false,
    var likeCount: Int = 0,
    var shareCount: Int = 0
)
