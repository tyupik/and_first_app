package ru.netology.nmedia
<<<<<<< HEAD

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import ru.netology.nmedia.NewPostFragment.Companion.textArg

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }
            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }
            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment).navigate(
                R.id.action_feedFragment_to_newPostFragment,
                Bundle().apply {
                    textArg = text
                }
            )
=======
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.item_post.*
import ru.netology.nmedia.AndroidUtils.hideKeyboard
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.adapter.PostAdapterClickListener
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    private val newPostRequestCode = 1
    val viewModel: PostViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter = PostAdapter(
            object : PostAdapterClickListener {
                override fun onEditClicked(post: Post) {
                    val intent = Intent(this@MainActivity, NewPostActivity::class.java).apply {
                        action = "post.content"
                        putExtra("post.text", post.content)
                    }
                    startActivityForResult(intent, newPostRequestCode)

                    viewModel.edit(post)
                }

                override fun onRemoveClicked(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onLikeClicked(post: Post) {
                    viewModel.likeById(post.id)
                }

                override fun onShareClicked(post: Post) {

                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }

                    val shareIntent =
                        Intent.createChooser(intent, getString(R.string.chooser_share_post))
                    startActivity(shareIntent)
                    viewModel.shareById(post.id)
                }

                override fun onVideoClicked(post: Post) {
                    if (viewModel.getUri(post)){
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(post.video)))
                    }
                }
            }
        )

        binding.list.adapter = adapter
        binding.list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        viewModel.data.observe(this, adapter::submitList)

//        binding.save.setOnClickListener {
//            val text = binding.content.text?.toString().orEmpty().trim()
//            if (text.isBlank()) {
//                Toast.makeText(this, getString(R.string.empty_post_error), Toast.LENGTH_SHORT)
//                    .show()
//                return@setOnClickListener
//            }
//            viewModel.changeContent(text)
//            viewModel.save()
//            binding.content.clearFocus()
//            it.hideKeyboard()
//            binding.root.edit_group.visibility = Group.GONE
//        }
//        binding.cancel.setOnClickListener {
//            viewModel.cancelEditing()
//            binding.content.clearFocus()
//            it.hideKeyboard()
//            binding.root.edit_group.visibility = Group.GONE
//        }

        binding.fab.setOnClickListener {
            val intent = Intent(this, NewPostActivity::class.java)
            startActivityForResult(intent, newPostRequestCode)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            newPostRequestCode -> {
                if (resultCode != Activity.RESULT_OK) {
                    return
                }

                data?.getStringExtra(Intent.EXTRA_TEXT)?.let {
                    viewModel.changeContent(it)
                    viewModel.save()
                }
            }
>>>>>>> 0c5698f812532553fd8ab7de70690d24ce284a98
        }
    }
}