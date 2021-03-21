package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Post
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl

private val defaultPost = Post(
    id = 0L,
    author = "",
    authorAvatar = null,
    content = "",
    published = "",
    likedByMe = false,
    likeCount = 0,
    shareCount = 0
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
//    private val repository: PostRepository = PostRepositorySQLiteImpl(AppDb.getInstance(application).postDao)   //SQLite
//    private val repository: PostRepository = PostRepositoryFileImpl(application)    //FileRepository
    private val repository: PostRepository = PostRepositoryImpl(AppDb.getInstance(context = application).postDao())
    val data = repository.getAll()
    private val edited = MutableLiveData(defaultPost)

    fun likeById(id: Long) {
        repository.likeById(id)
    }

    fun shareById(id: Long) {
        repository.shareById(id)
    }

    fun removeById(id: Long) {
        repository.removeById(id)
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (text == edited.value?.content) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = defaultPost
    }

    fun edit(post: Post) {
        edited.value = post
    }

//    fun cancelEditing() {
//        edited.value?.let {
//            repository.cancelEditing(it)
//        }
//        edited.value = defaultPost
//    }

//    fun getUri(post: Post): Boolean {
//        return repository.isVideo(post)
//    }

}