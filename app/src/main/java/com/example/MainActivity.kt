package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.SocialRepository
import com.example.ui.screens.SocialAppLayout
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.SocialViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Room DB, Repository and ViewModel
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = SocialRepository(
            profileDao = database.profileDao(),
            postDao = database.postDao(),
            commentDao = database.commentDao(),
            storyDao = database.storyDao()
        )
        val viewModel = ViewModelProvider(
            this,
            SocialViewModel.provideFactory(repository)
        )[SocialViewModel::class.java]

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            MyApplicationTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SocialAppLayout(viewModel = viewModel)
                }
            }
        }
    }
}
