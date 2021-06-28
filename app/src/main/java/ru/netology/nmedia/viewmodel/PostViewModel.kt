package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import androidx.work.*
import com.google.firebase.installations.FirebaseInstallations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.SingleLiveEvent
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.*
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.work.RemovePostWorker
import ru.netology.nmedia.work.SavePostWorker
import java.io.File
import javax.inject.Inject
import kotlin.random.Random

private val defaultPost = Post(
    id = 0L,
    authorId = 0L,
    author = "",
    authorAvatar = "1",
    content = "",
    published = "",
    likedByMe = false,
    likeCount = 0,
    shareCount = 0,
)

private val noPhoto = PhotoModel()

@HiltViewModel
@ExperimentalCoroutinesApi
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val workManager: WorkManager,
    auth: AppAuth
) : ViewModel() {

    private val cached = repository.data.cachedIn(viewModelScope)

    val data: Flow<PagingData<FeedModel>> = auth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { pagingData ->
                pagingData.map { post->
                    PostModel(post.copy(ownedByMe = post.authorId == myId))
                }.insertSeparators { post, _ ->
                    if (post?.id?.rem(5) == 0L) {
                        AdModel(Random.nextLong(), "figma.jpg")
                    } else {
                        null
                    }
                }
            }
        }

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(defaultPost)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    private var newPostsCollection: List<Post>? = null

    init {
        loadPosts()
        FirebaseInstallations.getInstance().getToken(true)
    }

//    val newerCount: LiveData<Int> = data.switchMap {
//        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0)
//            .catch { e ->
//                e.printStackTrace()
//            }
//            .asLiveData()
//    }


    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAllAsync()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAllAsync()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun likeById(post: Post) = viewModelScope.launch {
        try {
            repository.likeById(post)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun shareById(post: Post) = viewModelScope.launch {
        try {
            repository.shareById(post)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        viewModelScope.launch {
            val data = workDataOf(RemovePostWorker.postKey to id)
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = OneTimeWorkRequestBuilder<RemovePostWorker>()
                .setInputData(data)
                .setConstraints(constraints)
                .build()
            workManager.enqueue(request)


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
        edited.value?.let { post ->
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    val id = repository.saveWork(
                        post, _photo.value?.uri?.let { MediaUpload(it.toFile()) }
                    )
                    val data = workDataOf(SavePostWorker.postKey to id)
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val request = OneTimeWorkRequestBuilder<SavePostWorker>()
                        .setInputData(data)
                        .setConstraints(constraints)
                        .build()
                    workManager.enqueue(request)

                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    e.printStackTrace()
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = defaultPost
        _photo.value = noPhoto
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun updateNewPostsList(posts: List<Post>) {
        newPostsCollection = posts
    }

    fun addNewPosts(): List<Post>? {
        return newPostsCollection
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

}