package ru.netology.nmedia.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.RecipientInfo
import javax.inject.Inject
import kotlin.random.Random


@AndroidEntryPoint
class FCMService() : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()


    @Inject
    lateinit var auth: AppAuth


    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
//        message.data[action]?.let {
//            try {
//                when (Action.valueOf(it)) {
//                    Action.LIKE -> handleLike( gson.fromJson( message.data[content], Like::class.java))
//                    Action.NEWPOST -> handleNewPost(gson.fromJson(message.data[content], NewPost::class.java))
//                }
//            } catch (e: IllegalArgumentException) {
//                updateApp()
//            }
//        }
//        println(Gson().toJson(message))
        val msg = gson.fromJson(message.data["content"], RecipientInfo::class.java)

        val myid = auth.authStateFlow.value.id

        if (msg.recipientId == null) {
            //show notify
            notification(this, msg.content)
        }

        if (msg.recipientId == myid.toString()) {
            //show notify
            notification(this, msg.content)
        }

        if (msg.recipientId == "0" && msg.recipientId != myid.toString()) {
            //send again
            auth.sendPushToken()
        }

        if (msg.recipientId != "0" && msg.recipientId != myid.toString()) {
            //send again
            auth.sendPushToken()
        }

    }

    override fun onNewToken(token: String) {
        auth.sendPushToken(token)
        println(token)
    }


    private fun updateApp() {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.update_app))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    private fun handleLike(content: Like) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    content.userName,
                    content.postAuthor,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    private fun handleNewPost(content: NewPost) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_new_post,
                    content.userName
                )
            )
            .setContentText(content.content)
            .setStyle(NotificationCompat.BigTextStyle())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    private fun notification(context: Context, msg: String) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.avatar)
            .setContentTitle(getString(R.string.new_message))
            .setContentText(msg)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }
}

enum class Action {
    LIKE,
    NEWPOST
}

data class Like(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
)

data class NewPost(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
    val content: String
)