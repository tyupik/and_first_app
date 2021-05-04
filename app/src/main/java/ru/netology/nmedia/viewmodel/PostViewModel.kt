package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Post
import ru.netology.nmedia.SingleLiveEvent
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.BadConnectionException
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import java.io.IOException
import javax.security.auth.callback.Callback
import kotlin.concurrent.thread

private val defaultPost = Post(
    id = 0L,
    author = "",
    authorAvatar = "1",
    content = "",
    published = "",
    likedByMe = false,
    likeCount = 0,
    shareCount = 0
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    private val edited = MutableLiveData(defaultPost)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }


    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
            override fun onError(e: Exception) {
                if (e is BadConnectionException) {
                    _data.value = FeedModel(internetError = true)
                } else {
                    _data.value = FeedModel(error = true)
                }
            }

            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }
        })
    }


    fun likeById(post: Post) {
        repository.likeById(post, object : PostRepository.Callback<Post> {
            override fun onError(e: Exception) {
                if (e is BadConnectionException) {
                    _data.value = FeedModel(internetError = true)
                } else {
                    _data.value = FeedModel(error = true)
                }
            }

            override fun onSuccess(post: Post) {
                _data.postValue(
                    FeedModel(posts = _data.value?.posts
                        .orEmpty().map {
                            if (it.id == post.id) post else it
                        })
                )
            }
        })
    }

    fun shareById(post: Post) {
        repository.shareById(post, object : PostRepository.Callback<Post> {
            override fun onError(e: Exception) {
                super.onError(e)
            }

            override fun onSuccess(post: Post) {
                super.onSuccess(post)
            }
        })
    }

    fun removeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        try {
            repository.removeById(id, object : PostRepository.Callback<Unit> {
                override fun onError(e: Exception) {
                    if (e is BadConnectionException) {
                        _data.value = FeedModel(internetError = true)
                    } else {
                        _data.value = FeedModel(error = true)
                    }
                    _data.postValue(_data.value?.copy(posts = old))
                }

                override fun onSuccess(unit: Unit) {
                    _data.postValue(
                        _data.value?.copy(posts = _data.value?.posts.orEmpty()
                            .filter { it.id != id })
                    )
                }
            })
        } catch (e: IOException) {
            _data.postValue(_data.value?.copy(posts = old))
        }
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (text == edited.value?.content) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun save() {
        edited.value?.let { it ->
            repository.save(it, object : PostRepository.Callback<Post> {
                override fun onError(e: Exception) {
                    if (e is BadConnectionException) {
                        _data.value = FeedModel(internetError = true)
                    } else {
                        _data.value = FeedModel(error = true)
                    }
                    _postCreated.postValue(Unit)
                }

                override fun onSuccess(post: Post) {
                    if (edited.value?.id != defaultPost.id) {
                        _data.postValue(
                            FeedModel(posts = _data.value?.posts
                                .orEmpty().map { if (it.id == post.id) post else it })
                        )
                    }
                    _postCreated.value = Unit
                }
            })
        }
        edited.value = defaultPost
    }

    fun edit(post: Post) {
        edited.value = post
    }


}