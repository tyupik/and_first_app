package ru.netology.nmedia.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostKeyDao
import ru.netology.nmedia.dao.PostWorkDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DbModule {


    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext
        context: Context
    ): AppDb = Room.databaseBuilder(context, AppDb::class.java, "app.db")
        .fallbackToDestructiveMigration()
        .build()


    @Provides
    fun providePostDao(db: AppDb): PostDao = db.postDao()


    @Provides
    fun providePostWorkDao(db: AppDb): PostWorkDao = db.postWorkDao()

    @Provides
    fun providePostKeyDao(db: AppDb): PostKeyDao = db.postRemoteKeyDao()

}