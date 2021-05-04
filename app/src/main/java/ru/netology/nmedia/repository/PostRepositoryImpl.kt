package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.Post
import ru.netology.nmedia.api.PostApi
import java.util.concurrent.TimeUnit


class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        const val BASE_URL = "http://192.168.3.9:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken.type)
            }
    }


    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {

        PostApi.retrofitService.getAll()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    callback.onError(BadConnectionException(t.message ?: "BadConnectionException"))
                }
            })
    }


    override fun likeById(post: Post, callback: PostRepository.Callback<Post>) {
        val id = post.id

        if (!post.likedByMe) {
            PostApi.retrofitService.likeById(id)
                .enqueue(object : Callback<Post> {
                    override fun onResponse(call: Call<Post>, response: Response<Post>) {
                        if (!response.isSuccessful) {
                            callback.onError(RuntimeException(response.message()))
                            return

                        } else {
                            callback.onSuccess(
                                response.body() ?: throw RuntimeException("body is null")
                            )
                        }
                    }

                    override fun onFailure(call: Call<Post>, t: Throwable) {
                        callback.onError(BadConnectionException(t.message ?: "BadConnectionException"))
                    }
                })
        } else {
            PostApi.retrofitService.dislikeById(id)
                .enqueue(object : Callback<Post> {
                    override fun onResponse(call: Call<Post>, response: Response<Post>) {
                        if (!response.isSuccessful) {
                            callback.onError(RuntimeException(response.message()))
                            return
                        } else {
                            callback.onSuccess(
                                response.body() ?: throw RuntimeException("body is null")
                            )
                        }
                    }

                    override fun onFailure(call: Call<Post>, t: Throwable) {
                        callback.onError(BadConnectionException(t.message ?: "BadConnectionException"))
                    }
                })
        }
    }

    override fun shareById(post: Post, callback: PostRepository.Callback<Post>) {
        TODO("Not yet implemented")
    }

    override fun removeById(id: Long, callback: PostRepository.Callback<Unit>) {

        PostApi.retrofitService.removeById(id)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return

                    } else {
                        callback.onSuccess(
                            response.body() ?: throw RuntimeException("body is null")
                        )
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callback.onError(BadConnectionException(t.message ?: "BadConnectionException"))
                }
            })
    }

    override fun save(post: Post, callback: PostRepository.Callback<Post>) {

        PostApi.retrofitService.save(post)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(BadConnectionException(t.message ?: "BadConnectionException"))
                }
            })
    }
}