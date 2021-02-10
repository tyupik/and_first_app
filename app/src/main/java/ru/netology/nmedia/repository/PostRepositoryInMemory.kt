package ru.netology.nmedia.repository

import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Post

class PostRepositoryInMemory: PostRepository {

    private val post = Post(
        id = 1,
        author = "Нетология. Университет интернет-профессий будущего",
        content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
        published = "21 мая в 18:36",
        likeCount = 999,
        likeByMe = false
    )
    override val data = MutableLiveData<Post>(post)

    override fun like() {
        val currentPost = requireNotNull(data.value)
        data.value = currentPost.copy(
            likeByMe = !currentPost.likeByMe,
            likeCount = if (currentPost.likeByMe) currentPost.likeCount.dec() else currentPost.likeCount.inc()
        )
    }

    override fun share() {
        val currentPost = requireNotNull(data.value)
        data.value = currentPost.copy(shareCount = currentPost.shareCount.inc())
    }
}