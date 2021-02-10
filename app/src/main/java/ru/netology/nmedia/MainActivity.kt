package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import kotlin.math.floor
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()

        with(binding) {

            like.setOnClickListener {
                viewModel.like()
            }
            share.setOnClickListener {
                viewModel.share()
            }
            viewModel.data.observe(this@MainActivity) { post ->
                authorTv.text = post.author
                publishedTv.text = post.published
                textTv.text = post.content
                like.setImageResource(
                    if (post.likeByMe) R.drawable.like_red else R.drawable.empty_like
                )
                likeCount.text = setRoundCount(post.likeCount)
                shareCount.text = setRoundCount(post.shareCount)
            }

        }
    }

    private fun setRoundCount(value: Int): String {
        return when (value) {
            0 -> ""
            in 1..999 -> value.toString()
            in 1000..1099 -> "1K"
            in 1100..9999 -> (floor(value.toDouble() / 1000 * 10f) / 10f).toString() + "K"
            in 10000..999_999 -> floor(value.toDouble() / 1000).toInt().toString() + "K"
            else -> (floor(value.toDouble() / 1_000_000 * 10f) / 10f).toString() + "M"
        }
    }
}