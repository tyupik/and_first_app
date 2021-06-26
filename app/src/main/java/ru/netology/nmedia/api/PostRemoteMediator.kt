package ru.netology.nmedia.api

import androidx.paging.*
import androidx.room.withTransaction
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostRemoteKeyEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.repository.PostRepositoryImpl


@ExperimentalPagingApi
class PostRemoteMediator(
    private val api: ApiService,
    private val postDao: PostDao,
    private val db: AppDb,
    private val postKeyDao: PostKeyDao,
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {

//        val lastId = postKeyDao.max() ?: 0
        try {

            val pageSize = state.config.pageSize
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    val id = postKeyDao.max()
                    if (id != null) {
                        api.getNewer(id)
                    } else {
                        api.getLatest(pageSize)
                    }

                }
                LoadType.PREPEND -> {
                    //Отключение автоматического PREPEND
                    return MediatorResult.Success(true)
//                    val id = postKeyDao.max() ?: return MediatorResult.Success(false)
//                    api.getAfter(id, pageSize)
                }
                LoadType.APPEND -> {
                    val id = postKeyDao.min() ?: return MediatorResult.Success(false)
                    api.getBefore(id, pageSize)
                }

            }
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(
                response.code(), response.message()
            )

            if (body.isEmpty()) {
                return MediatorResult.Success(body.isEmpty())
            }
            db.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        postKeyDao.insert(
                            listOf(
                                PostRemoteKeyEntity(
                                    PostRemoteKeyEntity.Type.PREPEND,
                                    body.first().id
                                ),
                                PostRemoteKeyEntity(
                                    PostRemoteKeyEntity.Type.APPEND,
                                    body.last().id
                                )
                            )
                        )
//                        postDao.removeAll()
                    }
                    LoadType.PREPEND -> {
                        postKeyDao.insert(
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.Type.PREPEND,
                                body.first().id
                            )
                        )
                    }
                    LoadType.APPEND -> {
                        postKeyDao.insert(
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.Type.APPEND,
                                body.last().id
                            )
                        )
                    }
                }

                postDao.insert(body.map(PostEntity.Companion::fromDto))
            }
            val lastId = body.first().id

            return MediatorResult.Success(false)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}