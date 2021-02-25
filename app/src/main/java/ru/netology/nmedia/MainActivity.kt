package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.Group
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import ru.netology.nmedia.AndroidUtils.hideKeyboard
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.adapter.PostAdapterClickListener
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()

        val adapter = PostAdapter(
            object: PostAdapterClickListener {
                override fun onEditClicked(post: Post) {
                    binding.root.edit_group.visibility = Group.VISIBLE
                    viewModel.edit(post)
                }

                override fun onRemoveClicked(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onLikeClicked(post: Post) {
                    viewModel.likeById(post.id)
                }

                override fun onShareClicked(post: Post) {
                    viewModel.shareById(post.id)
                }

            }
        )

        binding.list.adapter = adapter
        binding.list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        viewModel.data.observe(this, adapter::submitList)
        viewModel.edited.observe(this) {
            binding.content.setText(it.content)
            binding.editableText.setText(it.content)
            if (it.content.isNotBlank()) {
                binding.content.requestFocus()
            }
        }

        binding.save.setOnClickListener{
            val text = binding.content.text?.toString().orEmpty()
            if (text.isBlank()) {
                Toast.makeText(this, getString(R.string.empty_post_error), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.changeContent(text)
            viewModel.save()
            binding.content.clearFocus()
            it.hideKeyboard()
            binding.root.edit_group.visibility = Group.GONE
        }
        binding.cancel.setOnClickListener{
            viewModel.cancelEditing()
            binding.content.clearFocus()
            it.hideKeyboard()
            binding.root.edit_group.visibility = Group.GONE
        }
    }
}