package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlin.math.floor
import ru.netology.nmedia.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likeCount = 999,
            likeByMe = false
        )

        with(binding){
            authorTv.text = post.author
            publishedTv.text = post.published
            textTv.text = post.content

            like.setOnClickListener {
                post.likeByMe = !post.likeByMe
                like.setImageResource(
                    if (post.likeByMe) R.drawable.like_red else R.drawable.empty_like
                )
                if (post.likeByMe) post.likeCount++ else post.likeCount--
                likeCount.text = setRoundCount(post.likeCount)
            }
            share.setOnClickListener{
                post.shareCount++
                shareCount.text = setRoundCount(post.shareCount)
            }
        }
    }

    private fun setRoundCount(value: Int): String{
        return when(value){
            0 -> ""
            in 1..999 -> value.toString()
            in 1000..1099 -> "1K"
            in 1100..9999 -> (floor(value.toDouble()/1000 * 10f) / 10f).toString() + "K"
            in 10000..999_999 -> floor(value.toDouble() / 1000).toInt().toString() + "K"
            else -> (floor(value.toDouble()/1_000_000 * 10f) / 10f).toString() + "M"
        }
    }
}