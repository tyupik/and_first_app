package ru.netology.nmedia.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import javax.inject.Inject

class RemovePostWorker(
    private val repository: PostRepository,
    applicationContext: Context,
    params: WorkerParameters
) : CoroutineWorker(applicationContext, params) {
    companion object {
        const val postKey = "post"
    }

    @Inject
    lateinit var appDbPWD: AppDb

    override suspend fun doWork(): Result {
        val id = inputData.getLong(postKey, 0L)
        if (id == 0L) {
            return Result.failure()
        }

//        val appDbPWD = AppDb.getInstance(context = applicationContext).postWorkDao()
//        val repository: PostRepository =
//            PostRepositoryImpl(
//                AppDb.getInstance(context = applicationContext).postDao(),
//                appDbPWD
//            )
        return try {
            repository.removeById(id)
            appDbPWD.postWorkDao().removeById(id)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        } catch (e: UnknownError) {
            Result.failure()
        }
    }
}