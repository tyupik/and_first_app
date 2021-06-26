package ru.netology.nmedia

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.NewPostFragment.Companion.textArg
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity
//@Inject constructor
    (
//    private val auth: AppAuth,
//    private val firebaseInstallations: FirebaseInstallations,
//    private val firebaseMessaging: FirebaseMessaging,
) : AppCompatActivity(R.layout.activity_main) {
    private val viewModel: AuthViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()

    @Inject
    lateinit var auth: AppAuth
    @Inject
    lateinit var firebaseInstallations: FirebaseInstallations
    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging



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
        }
        viewModel.data.observe(this) {
            invalidateOptionsMenu()
        }

        firebaseInstallations.id.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("some stuff happened: ${task.exception}")
                return@addOnCompleteListener
            }

            val token = task.result
            println(token)
        }

        firebaseMessaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("some stuff happened: ${task.exception}")
                return@addOnCompleteListener
            }

            val token = task.result
            println(token)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        menu?.let {
            it.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
            it.setGroupVisible(R.id.authenticated, viewModel.authenticated)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.signin -> {
                auth.setAuth(5, "x-token")
                Toast.makeText(this, "АВТОРИЗАЦИЯ", Toast.LENGTH_SHORT).show()
//                findNavController(R.id.nav_host_fragment).navigate(R.id.action_feedFragment_to_fragment_login)
                true
            }
            R.id.signup -> {
                auth.setAuth(5, "x-token")
                Toast.makeText(this, "Потом", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.signout -> {
                auth.removeAuth()
                postViewModel.refreshPosts()
                Toast.makeText(this, "Выход", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}