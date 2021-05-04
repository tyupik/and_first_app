package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.Post

interface PostRepository {
    //    val data: LiveData<List<Post>>
    fun getAll(): List<Post>
    fun likeById(post: Post, callback: Callback<Post>)
    fun shareById(post: Post, callback: Callback<Post>)
    fun removeById(id: Long, callback: Callback<Unit>)
    fun save(post: Post, callback: Callback<Post>)

    fun getAllAsync(callback: Callback<List<Post>>)

    interface Callback<T> {
        fun onError(e: Exception) {}
        fun onSuccess(posts: T) {}
    }
}

class BadConnectionException(message:String): Exception(message)