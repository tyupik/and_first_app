package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: Flow<List<Post>>

    suspend fun likeById(post: Post)
    suspend fun shareById(post: Post,)
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
    suspend fun getAllAsync()
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun upload(upload: MediaUpload): Media
}
