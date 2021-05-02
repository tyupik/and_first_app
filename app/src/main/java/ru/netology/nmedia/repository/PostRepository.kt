package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.Post

interface PostRepository {
    //    val data: LiveData<List<Post>>
    fun getAll(): List<Post>
    fun likeById(post: Post, callback: CommonCallback)
    fun shareById(post: Post, callback: CommonCallback)
    fun removeById(id: Long, callback: CommonCallback)
    fun save(post: Post, callback: CommonCallback)

    fun getAllAsync(callback: GetAllCallback)

    interface GetAllCallback {
        fun onError(e: Exception) {}
        fun onSuccess(posts: List<Post>) {}
    }

    interface CommonCallback {
        fun onError(e: Exception) {}
        fun onSuccess(post: Post) {}
    }

}