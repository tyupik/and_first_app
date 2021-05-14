package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.Post

interface PostRepository {
    val data: LiveData<List<Post>>
//    suspend fun getAll(): List<Post>
//    suspend fun likeById(post: Post, callback: Callback<Post>)
//    suspend fun shareById(post: Post, callback: Callback<Post>)
//    suspend fun removeById(id: Long, callback: Callback<Unit>)
//    suspend fun save(post: Post, callback: Callback<Post>)
//
//    suspend fun getAllAsync(callback: Callback<List<Post>>)
//
//    interface Callback<T> {
//        fun onError(e: Exception) {}
//        fun onSuccess(posts: T) {}
//    }

    suspend fun likeById(post: Post)
    suspend fun shareById(post: Post,)
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
    suspend fun getAllAsync()

}

class BadConnectionException(message:String): Exception(message)