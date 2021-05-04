package ru.netology.nmedia

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_feed.*
import ru.netology.nmedia.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.adapter.PostAdapterClickListener
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {
    val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )
        val adapter = PostAdapter(
            object : PostAdapterClickListener {
                override fun onEditClicked(post: Post) {
                    findNavController().navigate(
                        R.id.action_feedFragment_to_newPostFragment,
                        Bundle().apply {
                            textArg = post.content
                        }
                    )
                    viewModel.edit(post)
                }

                override fun onRemoveClicked(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onLikeClicked(post: Post) {
                    viewModel.likeById(post)
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
                    viewModel.shareById(post)
                }

            },
            "${PostRepositoryImpl.BASE_URL}/avatars/"

        )


        binding.list.adapter = adapter
        binding.swipeRefresh.setOnRefreshListener{
            viewModel.loadPosts()
            swipe_refresh.isRefreshing = false
        }

        viewModel.data.observe(viewLifecycleOwner, { state ->
            adapter.submitList(state.posts)

            if (state.internetError) {
                Toast.makeText(requireContext(),"Connection error. Try again", Toast.LENGTH_SHORT).show()
                binding.errorGroup.isVisible = true
            }
            binding.progress.isVisible = state.loading
            binding.errorGroup.isVisible = state.error
            binding.emptyText.isVisible = state.empty
        })

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }



        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
        return binding.root
    }


}