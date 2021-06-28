package ru.netology.nmedia.adapter

import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.AdModel

//typealias OnLikeListener = (post: Post) -> Unit
//typealias OnShareListener = (post: Post) -> Unit
//typealias OnRemoveListener = (post: Post) -> Unit

interface PostAdapterClickListener {
    fun onEditClicked(post: Post)
    fun onRemoveClicked(post: Post)
    fun onLikeClicked(post: Post)
    fun onShareClicked(post: Post)
    fun onAttachmentClicked(post: Post)
    fun onAdClicked(adModel: AdModel)
//    fun onVideoClicked(post: Post)
}