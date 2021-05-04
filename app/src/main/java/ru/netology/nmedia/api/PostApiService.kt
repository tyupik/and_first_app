package ru.netology.nmedia.api

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.netology.nmedia.Post
import java.util.concurrent.TimeUnit

private const val BASE_URL = "http://192.168.3.9:9999/api/slow/"

private val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(client)
    .build()

interface PostApiService {
    @GET("posts")
    fun getAll(): Call<List<Post>>

    @POST("posts")
    fun save(@Body post: Post): Call<Post>

    @POST("posts/{id}/likes")
    fun likeById(@Path("id") id: Long): Call<Post>

    @DELETE("posts/{id}/likes")
    fun dislikeById(@Path("id") id: Long): Call<Post>

    @DELETE("posts/{id}")
    fun removeById(@Path("id") id: Long): Call<Unit>


}

object PostApi {
    val retrofitService: PostApiService by lazy {
        retrofit.create(PostApiService::class.java)
    }
}