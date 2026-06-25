package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Comment
import com.example.data.model.Post
import com.example.data.model.Profile
import com.example.data.model.Story
import com.example.data.repository.SocialRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface Screen {
    object Home : Screen
    object CreatePost : Screen
    object Profile : Screen
}

@OptIn(ExperimentalCoroutinesApi::class)
class SocialViewModel(private val repository: SocialRepository) : ViewModel() {

    // Current Active Tab
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Dark/Light Theme state (Persisted in ViewModel)
    val isDarkTheme = MutableStateFlow(true)

    // Search and Categorization filters
    val currentCategory = MutableStateFlow("الكل")
    val searchQuery = MutableStateFlow("")

    // Profile State
    val profileState: StateFlow<Profile?> = repository.profileFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Stories State
    val storiesState: StateFlow<List<Story>> = repository.allStoriesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Raw Posts State
    val postsState: StateFlow<List<Post>> = repository.allPostsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filtered Posts State based on Search Query and Selected Category
    val filteredPostsState: StateFlow<List<Post>> = combine(
        repository.allPostsFlow,
        currentCategory,
        searchQuery
    ) { posts, category, query ->
        posts.filter { post ->
            val matchesCategory = if (category == "الكل") {
                true
            } else if (category == "محفوظة") {
                post.isBookmarked
            } else {
                post.category == category
            }
            val matchesQuery = query.isEmpty() ||
                    post.content.contains(query, ignoreCase = true) ||
                    post.authorName.contains(query, ignoreCase = true) ||
                    post.authorHandle.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Currently selected post for viewing comments
    private val _selectedPostId = MutableStateFlow<Int?>(null)
    val selectedPostId: StateFlow<Int?> = _selectedPostId.asStateFlow()

    // Active Comments list for selected post
    val activeComments: StateFlow<List<Comment>> = _selectedPostId
        .flatMapLatest { id ->
            if (id != null) {
                repository.getCommentsForPostFlow(id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            // Seed initial data if the local DB is fresh on first install
            repository.seedInitialDataIfEmpty()
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun toggleLike(postId: Int) {
        viewModelScope.launch {
            repository.toggleLike(postId)
        }
    }

    fun toggleBookmark(postId: Int) {
        viewModelScope.launch {
            repository.toggleBookmark(postId)
        }
    }

    fun createStory(mediaUrl: String, textOverlay: String?) {
        viewModelScope.launch {
            repository.createStory(mediaUrl, textOverlay)
        }
    }

    fun watchStory(storyId: Int) {
        viewModelScope.launch {
            repository.markStoryAsWatched(storyId)
        }
    }

    fun toggleTheme() {
        isDarkTheme.value = !isDarkTheme.value
    }

    fun setCategory(category: String) {
        currentCategory.value = category
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun createPost(content: String, imageKey: String?, category: String = "عام") {
        viewModelScope.launch {
            repository.createPost(content, imageKey, category)
            _currentScreen.value = Screen.Home // Return to feed after posting
        }
    }

    fun selectPostForComments(postId: Int?) {
        _selectedPostId.value = postId
    }

    fun getCommentsFlow(postId: Int): Flow<List<Comment>> {
        return repository.getCommentsForPostFlow(postId)
    }

    fun addCommentDirect(postId: Int, content: String) {
        viewModelScope.launch {
            repository.addComment(postId, content)
        }
    }

    fun submitComment(content: String) {
        val postId = _selectedPostId.value ?: return
        viewModelScope.launch {
            repository.addComment(postId, content)
        }
    }

    fun updateProfile(name: String, handle: String, bio: String, avatarUrl: String? = null) {
        viewModelScope.launch {
            val current = profileState.value ?: return@launch
            val updated = current.copy(
                username = name,
                handle = if (handle.startsWith("@")) handle else "@$handle",
                bio = bio,
                avatarUrl = avatarUrl ?: current.avatarUrl
            )
            repository.updateProfile(updated)
        }
    }

    fun deletePost(post: Post) {
        viewModelScope.launch {
            repository.deletePost(post)
        }
    }

    companion object {
        fun provideFactory(repository: SocialRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SocialViewModel(repository) as T
                }
            }
    }
}
