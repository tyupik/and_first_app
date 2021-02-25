package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.Post
import ru.netology.nmedia.repository.PostRepositoryInMemory

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

class PostViewModel : ViewModel() {
    private val repository = PostRepositoryInMemory()
    val data: MutableLiveData<List<Post>>
        get() = repository.data
    val edited = MutableLiveData(defaultPost)
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

    fun edit (post: Post) {
        edited.value = post
    }

    fun cancelEditing() {
        edited.value?.let {
            repository.cancelEditing(it)
        }
        edited.value = defaultPost
    }
}