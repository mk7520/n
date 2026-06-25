package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.Comment
import com.example.data.model.Post
import com.example.data.model.Profile
import com.example.ui.components.getDrawableIdByName
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.SocialViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialAppLayout(viewModel: SocialViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val profile by viewModel.profileState.collectAsState()
    val posts by viewModel.filteredPostsState.collectAsState() // Using filtered posts reactive state
    val stories by viewModel.storiesState.collectAsState() // Collecting active stories
    val selectedPostId by viewModel.selectedPostId.collectAsState()
    val comments by viewModel.activeComments.collectAsState()
    val isDark by viewModel.isDarkTheme.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.currentCategory.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.tertiary
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "N",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp
                            )
                        }
                        
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Text(
                            text = "الملتقى",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            ),
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                },
                actions = {
                    // Theme Toggle Button (Light/Dark Mode Switcher)
                    IconButton(
                        onClick = { viewModel.toggleTheme() },
                        modifier = Modifier.testTag("theme_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isDark) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                            contentDescription = "تغيير المظهر",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    profile?.let {
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.Profile) },
                            modifier = Modifier.testTag("top_profile_button")
                        ) {
                            UserAvatar(name = it.username, handle = it.handle, size = 32.dp, avatarUrl = it.avatarUrl)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.shadow(2.dp)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentScreen is Screen.Home,
                    onClick = { viewModel.navigateTo(Screen.Home) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen is Screen.Home) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = stringResource(R.string.tab_home)
                        )
                    },
                    label = { Text(stringResource(R.string.tab_home), fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_home_tab")
                )

                NavigationBarItem(
                    selected = currentScreen is Screen.CreatePost,
                    onClick = { viewModel.navigateTo(Screen.CreatePost) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen is Screen.CreatePost) Icons.Filled.AddCircle else Icons.Outlined.AddCircle,
                            contentDescription = stringResource(R.string.tab_create)
                        )
                    },
                    label = { Text(stringResource(R.string.tab_create), fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_create_tab")
                )

                NavigationBarItem(
                    selected = currentScreen is Screen.Profile,
                    onClick = { viewModel.navigateTo(Screen.Profile) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen is Screen.Profile) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = stringResource(R.string.tab_profile)
                        )
                    },
                    label = { Text(stringResource(R.string.tab_profile), fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_profile_tab")
                )
            }
        },
        floatingActionButton = {
            if (currentScreen is Screen.Home) {
                ExtendedFloatingActionButton(
                    text = { Text("منشور جديد", fontWeight = FontWeight.Bold, color = Color.White) },
                    icon = { Icon(Icons.Filled.Edit, contentDescription = "أنشئ منشوراً", tint = Color.White) },
                    onClick = { viewModel.navigateTo(Screen.CreatePost) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("feed_fab")
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "ScreenTransition"
            ) { targetScreen ->
                when (targetScreen) {
                    is Screen.Home -> {
                        HomeFeedScreen(
                            posts = posts,
                            stories = stories,
                            onLikeClick = { viewModel.toggleLike(it) },
                            onBookmarkClick = { viewModel.toggleBookmark(it) },
                            onCommentClick = { viewModel.selectPostForComments(it) },
                            onDeleteClick = { viewModel.deletePost(it) },
                            onAddStoryClick = { media, text -> viewModel.createStory(media, text) },
                            onStoryWatched = { viewModel.watchStory(it) },
                            currentUserProfile = profile,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            selectedCategory = selectedCategory,
                            onCategorySelect = { viewModel.setCategory(it) },
                            onPublishQuickPost = { content, imageKey, category ->
                                if (content.isBlank()) {
                                    Toast.makeText(context, context.getString(R.string.toast_post_empty), Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.createPost(content, imageKey, category)
                                    Toast.makeText(context, "تم نشر منشورك بنجاح! 🎉", Toast.LENGTH_SHORT).show()
                                }
                            },
                            commentsFlowOfPost = { viewModel.getCommentsFlow(it) },
                            onSubmitCommentInline = { postId, content -> viewModel.addCommentDirect(postId, content) }
                        )
                    }
                    is Screen.CreatePost -> {
                        CreatePostScreen(
                            onPublishClick = { content, imageKey, category ->
                                if (content.isBlank()) {
                                    Toast.makeText(context, context.getString(R.string.toast_post_empty), Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.createPost(content, imageKey, category)
                                }
                            }
                        )
                    }
                    is Screen.Profile -> {
                        profile?.let { userProfile ->
                            val userPosts = posts.filter { it.authorHandle == userProfile.handle }
                            ProfileScreen(
                                profile = userProfile,
                                profilePosts = userPosts,
                                onSaveProfile = { name, handle, bio, avatarUrl ->
                                    viewModel.updateProfile(name, handle, bio, avatarUrl)
                                    Toast.makeText(context, context.getString(R.string.toast_profile_updated), Toast.LENGTH_SHORT).show()
                                },
                                onLikeClick = { viewModel.toggleLike(it) },
                                onBookmarkClick = { viewModel.toggleBookmark(it) },
                                onCommentClick = { viewModel.selectPostForComments(it) },
                                onDeleteClick = { viewModel.deletePost(it) },
                                commentsFlowOfPost = { viewModel.getCommentsFlow(it) },
                                onSubmitCommentInline = { postId, content -> viewModel.addCommentDirect(postId, content) }
                            )
                        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            // High-fidelity Modal overlay for comments when selectedPostId is set
            selectedPostId?.let { postId ->
                val parentPost = posts.find { it.id == postId }
                if (parentPost != null) {
                    CommentsSheet(
                        post = parentPost,
                        comments = comments,
                        onDismiss = { viewModel.selectPostForComments(null) },
                        onSubmitComment = { content ->
                            viewModel.submitComment(content)
                        }
                    )
                }
            }
        }
    }
}

// ==========================================
// AVATAR COMPOSABLE (Dynamic Gradients)
// ==========================================
@Composable
fun UserAvatar(
    name: String,
    handle: String,
    size: Dp = 44.dp,
    textStyle: TextStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
    avatarUrl: String? = null
) {
    // Generate beautiful dynamic colors based on user handle as fallback
    val colors = when (handle.lowercase().hashCode() % 5) {
        0 -> listOf(Color(0xFF0EA5E9), Color(0xFF2563EB)) // Blue - Sky
        1 -> listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)) // Purple - Indigo
        2 -> listOf(Color(0xFFEC4899), Color(0xFFBE185D)) // Pink - Rose
        3 -> listOf(Color(0xFFF59E0B), Color(0xFFD97706)) // Amber - Orange
        else -> listOf(Color(0xFF10B981), Color(0xFF047857)) // Emerald - Green
    }

    val initials = name.trim().split(" ")
        .filter { it.isNotEmpty() }
        .map { it.first() }
        .take(2)
        .joinToString("")
        .uppercase()

    val imageResId = if (!avatarUrl.isNullOrBlank()) getDrawableIdByName(avatarUrl) else 0

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), CircleShape)
            .background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center
    ) {
        if (imageResId != 0) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "الصورة الشخصية",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (!avatarUrl.isNullOrBlank()) {
            val painter = coil.compose.rememberAsyncImagePainter(
                model = coil.request.ImageRequest.Builder(LocalContext.current)
                    .data(avatarUrl)
                    .crossfade(true)
                    .build()
            )
            Image(
                painter = painter,
                contentDescription = "الصورة الشخصية",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = initials,
                color = Color.White,
                style = textStyle,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==========================================
// FEED SCREEN
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeFeedScreen(
    posts: List<Post>,
    stories: List<com.example.data.model.Story>,
    onLikeClick: (Int) -> Unit,
    onBookmarkClick: (Int) -> Unit,
    onCommentClick: (Int) -> Unit,
    onDeleteClick: (Post) -> Unit,
    onAddStoryClick: (String, String?) -> Unit,
    onStoryWatched: (Int) -> Unit,
    currentUserProfile: Profile?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    onPublishQuickPost: (String, String?, String) -> Unit,
    commentsFlowOfPost: ((Int) -> kotlinx.coroutines.flow.Flow<List<Comment>>)? = null,
    onSubmitCommentInline: ((Int, String) -> Unit)? = null
) {
    var showAddStoryDialog by remember { mutableStateOf(false) }
    var activeStoryViewList by remember { mutableStateOf<List<com.example.data.model.Story>?>(null) }
    var activeStoryIndex by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Feed lazy list containing stories, search, filters, and posts
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // 1. Stories row
            item {
                StoryBar(
                    stories = stories,
                    onAddClick = { showAddStoryDialog = true },
                    onStoryClick = { clickedStory ->
                        val authorStories = stories.filter { it.authorHandle == clickedStory.authorHandle }
                        val index = authorStories.indexOf(clickedStory).coerceAtLeast(0)
                        activeStoryViewList = authorStories
                        activeStoryIndex = index
                        onStoryWatched(clickedStory.id)
                    }
                )
            }

            // 1.5 Quick Post Card
            item {
                QuickPostCard(
                    profile = currentUserProfile,
                    onPublishClick = onPublishQuickPost
                )
            }

            // 2. Search and Category filter section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    // Search text field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_posts_input"),
                        placeholder = { Text("ابحث في المنشورات...", fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "بحث",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "مسح",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Horizontal Category Chips
                    val categories = listOf("الكل", "عام", "تقنية", "برمجة", "محفوظة")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        categories.forEach { category ->
                            val isSelected = selectedCategory == category
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                    .clickable { onCategorySelect(category) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                                    .testTag("category_chip_$category"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (category == "محفوظة") {
                                        Icon(
                                            imageVector = if (isSelected) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = if (category.startsWith("#") || category == "الكل" || category == "محفوظة") category else "#$category",
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Posts feed or empty state placeholder
            if (posts.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp, horizontal = 24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (searchQuery.isNotEmpty()) Icons.Outlined.SearchOff else Icons.Outlined.Forum,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "لا توجد نتائج تطابق بحثك" else stringResource(R.string.no_posts),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "جرّب البحث بكلمات أخرى أو تغيير التصنيف" else stringResource(R.string.empty_feed_tip),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(posts, key = { it.id }) { post ->
                    PostCardItem(
                        post = post,
                        onLikeClick = { onLikeClick(post.id) },
                        onBookmarkClick = { onBookmarkClick(post.id) },
                        onCommentClick = { onCommentClick(post.id) },
                        onDeleteClick = { onDeleteClick(post) },
                        canDelete = post.authorHandle == (currentUserProfile?.handle ?: ""),
                        commentsFlow = commentsFlowOfPost?.invoke(post.id),
                        onSubmitCommentInline = { content -> onSubmitCommentInline?.invoke(post.id, content) }
                    )
                }
            }
        }
    }

    // Story Addition Dialog
    if (showAddStoryDialog) {
        AddStoryDialog(
            onDismiss = { showAddStoryDialog = false },
            onSubmitStory = { media, text ->
                onAddStoryClick(media, text)
                showAddStoryDialog = false
            }
        )
    }

    // Dynamic Immersive Full-Screen Story Viewer
    activeStoryViewList?.let { storyList ->
        if (storyList.isNotEmpty()) {
            StoryViewerDialog(
                stories = storyList,
                initialIndex = activeStoryIndex,
                onDismiss = { activeStoryViewList = null },
                onStoryChanged = { idx ->
                    onStoryWatched(storyList[idx].id)
                }
            )
        }
    }
}

// ==========================================
// DYNAMIC QUICK POST CARD (In-feed text post creator)
// ==========================================
@Composable
fun QuickPostCard(
    profile: Profile?,
    onPublishClick: (String, String?, String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var textContent by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("عام") }
    var selectedImageKey by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current

    // Camera capture launcher
    val cameraLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val path = saveBitmapToFile(context, bitmap)
            if (path != null) {
                selectedImageKey = path
            }
        }
    }

    // Gallery picker launcher
    val galleryLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val path = saveUriToFile(context, uri)
            if (path != null) {
                selectedImageKey = path
            }
        }
    }

    var showEmojiPicker by remember { mutableStateOf(false) }
    val popularEmojis = listOf("😊", "😂", "❤️", "🔥", "👍", "🎉", "✨", "🚀", "💡", "💻", "📝", "💬", "🌟", "🤔", "👏", "🥳")
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
            .testTag("quick_post_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            if (!isExpanded) {
                // Collapsed state: Row with Avatar, Placeholder button, and Quick actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = true },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    UserAvatar(
                        name = profile?.username ?: "مستخدم",
                        handle = profile?.handle ?: "@user",
                        size = 38.dp,
                        avatarUrl = profile?.avatarUrl
                    )
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "بمَ تفكّر يا ${profile?.username?.split(" ")?.firstOrNull() ?: "صديقي"}؟ شاركهم الآن...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "كتابة",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else {
                // Expanded state: Full editor inline
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    UserAvatar(
                        name = profile?.username ?: "مستخدم",
                        handle = profile?.handle ?: "@user",
                        size = 38.dp,
                        avatarUrl = profile?.avatarUrl
                    )
                    Column {
                        Text(
                            text = profile?.username ?: "مستخدم جديد",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = profile?.handle ?: "@user",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    IconButton(
                        onClick = {
                            isExpanded = false
                            textContent = ""
                            selectedImageKey = null
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إلغاء",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Content Input area
                OutlinedTextField(
                    value = textContent,
                    onValueChange = { if (it.length <= 300) textContent = it },
                    placeholder = { 
                        Text(
                            "اكتب منشورك الاحترافي هنا... شارك المعرفة أو الأخبار أو الأفكار الملهمة ✨",
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        ) 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp, max = 150.dp)
                        .testTag("quick_post_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    textStyle = TextStyle(fontSize = 13.sp, lineHeight = 18.sp)
                )

                // Preview of captured/selected custom photo
                if (selectedImageKey != null && (selectedImageKey!!.startsWith("/") || selectedImageKey!!.contains("content://"))) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    ) {
                        val painter = coil.compose.rememberAsyncImagePainter(
                            model = coil.request.ImageRequest.Builder(LocalContext.current)
                                .data(selectedImageKey)
                                .crossfade(true)
                                .build()
                        )
                        Image(
                            painter = painter,
                            contentDescription = "معاينة الصورة المرفقة",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Close/Remove Button
                        IconButton(
                            onClick = { selectedImageKey = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                .size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "حذف الصورة",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action tools row: Camera, Gallery, Emoji
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Camera button
                    IconButton(
                        onClick = { cameraLauncher.launch(null) },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                            .testTag("quick_camera_btn")
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "التقاط صورة للكاميرا",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Gallery button
                    IconButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                            .testTag("quick_gallery_btn")
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "إرفاق صورة من المعرض",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Emoji button
                    IconButton(
                        onClick = { showEmojiPicker = !showEmojiPicker },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                            .testTag("quick_emoji_btn")
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "إضافة رموز تعبيرية",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    if (selectedImageKey != null && (selectedImageKey!!.startsWith("/") || selectedImageKey!!.contains("content://"))) {
                        Text(
                            text = "📸 صورة مخصصة",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                // Simple Emoji Picker Row
                if (showEmojiPicker) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            popularEmojis.forEach { emoji ->
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                        .clickable {
                                            if (textContent.length < 300) {
                                                textContent += emoji
                                            }
                                        }
                                        .testTag("quick_emoji_$emoji"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(emoji, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                
                // Character limit meter row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val progress = textContent.length / 300f
                    val progressColor = when {
                        progress > 0.9f -> Color.Red
                        progress > 0.75f -> Color(0xFFF59E0B) // Amber
                        else -> MaterialTheme.colorScheme.primary
                    }
                    
                    Text(
                        text = "${textContent.length}/300",
                        fontSize = 11.sp,
                        color = if (textContent.length >= 300) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    CircularProgressIndicator(
                        progress = progress,
                        modifier = Modifier.size(12.dp),
                        color = progressColor,
                        strokeWidth = 2.dp,
                        trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                }
                
                // Visual Theme Attachment (Template cards in compact row)
                Text(
                    text = "مظهر المنشور البصري (اختياري):",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val templates = listOf(
                        Pair("None", null),
                        Pair("post_workspace", R.drawable.post_workspace_1782290616565),
                        Pair("post_tech_design", R.drawable.post_tech_design_1782290633173),
                        Pair("banner_nexus", R.drawable.banner_nexus_1782290597497)
                    )
                    
                    templates.forEach { (key, drawableId) ->
                        val isSelected = selectedImageKey == key || (key == "None" && selectedImageKey == null)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable {
                                    selectedImageKey = if (key == "None") null else key
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (drawableId != null) {
                                Image(
                                    painter = painterResource(id = drawableId),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.2f))
                                )
                            } else {
                                Text("بدون", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "محدد",
                                        tint = if (drawableId != null) Color.White else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Category chooser & Publish actions row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: Compact Category chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val categoriesList = listOf("عام", "تقنية", "برمجة")
                        categoriesList.forEach { category ->
                            val isSelected = selectedCategory == category
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedCategory = category }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "#$category",
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                    
                    // Right: Publish action button
                    Button(
                        onClick = {
                            if (textContent.isBlank()) {
                                Toast.makeText(context, "الرجاء كتابة نص المنشور أولاً!", Toast.LENGTH_SHORT).show()
                            } else {
                                onPublishClick(textContent, selectedImageKey, selectedCategory)
                                textContent = ""
                                selectedImageKey = null
                                isExpanded = false
                            }
                        },
                        modifier = Modifier
                            .testTag("quick_publish_submit_btn")
                            .height(36.dp),
                        shape = RoundedCornerShape(18.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color.White
                            )
                            Text("نشر", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// STORIES BAR COMPOSABLE
// ==========================================
@Composable
fun StoryBar(
    stories: List<com.example.data.model.Story>,
    onAddClick: () -> Unit,
    onStoryClick: (com.example.data.model.Story) -> Unit
) {
    // Unique list of story authors to group them beautifully
    val uniqueAuthors = stories.distinctBy { it.authorHandle }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 12.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Add story column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable { onAddClick() }
                .testTag("add_story_action_btn")
        ) {
            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                // Outer circle container
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "إضافة قصة",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                // Add icon badge
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "قصتك",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Render distinct authors' latest stories
        uniqueAuthors.forEach { authorStory ->
            val hasUnread = stories.filter { it.authorHandle == authorStory.authorHandle }.any { !it.isWatched }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onStoryClick(authorStory) }
                    .testTag("story_view_btn_${authorStory.id}")
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            if (hasUnread) {
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            } else {
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.outlineVariant,
                                        MaterialTheme.colorScheme.outlineVariant
                                    )
                                )
                            }
                        )
                        .padding(2.5.dp) // Border thickness
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    UserAvatar(
                        name = authorStory.authorName,
                        handle = authorStory.authorHandle,
                        size = 56.dp,
                        textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                        avatarUrl = authorStory.authorAvatar
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = authorStory.authorName.split(" ").firstOrNull() ?: authorStory.authorName,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(60.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ==========================================
// DIALOG TO ADD A STORY
// ==========================================
@Composable
fun AddStoryDialog(
    onDismiss: () -> Unit,
    onSubmitStory: (String, String?) -> Unit
) {
    var storyText by remember { mutableStateOf("") }
    var selectedMediaUrl by remember { mutableStateOf("story_neon_space") } // Default wallpaper

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "مشاركة حالة جديدة ✨",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Preview with active style
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant,
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val imageResId = getDrawableIdByName(selectedMediaUrl)
                    if (imageResId != 0) {
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                                    )
                                )
                        )
                    }

                    // Text Overlay Preview
                    Text(
                        text = if (storyText.isBlank()) "اكتب هنا..." else storyText,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black.copy(alpha = 0.8f),
                                blurRadius = 4f
                            )
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input field for overlay text
                OutlinedTextField(
                    value = storyText,
                    onValueChange = { if (it.length <= 100) storyText = it },
                    placeholder = { Text("اكتب خاطرة أو فكرة...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("story_text_input"),
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Wallpaper options selector
                Text(
                    text = "اختر الخلفية البصرية:",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Option 1: Neon space
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedMediaUrl = "story_neon_space" }
                    ) {
                        Box(
                            modifier = Modifier
                                .height(50.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    if (selectedMediaUrl == "story_neon_space") 3.dp else 1.dp,
                                    if (selectedMediaUrl == "story_neon_space") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(8.dp)
                                )
                        ) {
                            val neonId = getDrawableIdByName("story_neon_space")
                            if (neonId != 0) {
                                Image(
                                    painter = painterResource(id = neonId),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("سايبر بانك", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    }

                    // Option 2: Sunrise landscape
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedMediaUrl = "story_nature_sun" }
                    ) {
                        Box(
                            modifier = Modifier
                                .height(50.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    if (selectedMediaUrl == "story_nature_sun") 3.dp else 1.dp,
                                    if (selectedMediaUrl == "story_nature_sun") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(8.dp)
                                )
                        ) {
                            val sunId = getDrawableIdByName("story_nature_sun")
                            if (sunId != 0) {
                                Image(
                                    painter = painterResource(id = sunId),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("شروق هادئ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إلغاء")
                    }
                    Button(
                        onClick = {
                            onSubmitStory(selectedMediaUrl, storyText.takeIf { it.isNotBlank() })
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("publish_story_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("نشر الآن", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// IMMERSIVE FULL-SCREEN STORY VIEWER DIALOG
// ==========================================
@Composable
fun StoryViewerDialog(
    stories: List<com.example.data.model.Story>,
    initialIndex: Int,
    onDismiss: () -> Unit,
    onStoryChanged: (Int) -> Unit
) {
    var currentIndex by remember { mutableStateOf(initialIndex) }
    val currentStory = stories.getOrNull(currentIndex) ?: stories.first()

    // Notify whenever story changes so database updates watched status
    LaunchedEffect(currentIndex) {
        onStoryChanged(currentIndex)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Immersive background image
            val imgResId = getDrawableIdByName(currentStory.mediaUrl)
            if (imgResId != 0) {
                Image(
                    painter = painterResource(id = imgResId),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                            )
                        )
                )
            }

            // Dark vignette overlay to make controls legible
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.5f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.6f)
                            )
                        )
                    )
            )

            // Content Column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(16.dp)
            ) {
                // Top Indicator bars
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    stories.forEachIndexed { idx, _ ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(
                                    if (idx <= currentIndex) Color.White else Color.White.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Author Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    UserAvatar(
                        name = currentStory.authorName,
                        handle = currentStory.authorHandle,
                        size = 40.dp,
                        avatarUrl = currentStory.authorAvatar
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentStory.authorName,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = currentStory.authorHandle,
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_story_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Story Overlay Text Content in Middle
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    currentStory.textOverlay?.let { text ->
                        Text(
                            text = text,
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = Color.Black.copy(alpha = 0.9f),
                                    blurRadius = 6f
                                )
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }

                // Bottom Navigation Hints
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button (Right side in Arabic locale)
                    TextButton(
                        onClick = {
                            if (currentIndex > 0) currentIndex--
                        },
                        enabled = currentIndex > 0
                    ) {
                        Text(
                            "السابق",
                            color = if (currentIndex > 0) Color.White else Color.White.copy(alpha = 0.3f),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Next button
                    TextButton(
                        onClick = {
                            if (currentIndex < stories.size - 1) currentIndex++
                            else onDismiss()
                        }
                    ) {
                        Text(
                            if (currentIndex < stories.size - 1) "التالي" else "إنهاء",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// CARD COMPOSABLE FOR POSTS
// ==========================================
@Composable
fun PostCardItem(
    post: Post,
    onLikeClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onCommentClick: () -> Unit,
    onDeleteClick: () -> Unit,
    canDelete: Boolean,
    commentsFlow: kotlinx.coroutines.flow.Flow<List<Comment>>? = null,
    onSubmitCommentInline: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    var isLiked by remember { mutableStateOf(post.isLikedByMe) }
    var likesCount by remember { mutableStateOf(post.likesCount) }
    var isBookmarked by remember { mutableStateOf(post.isBookmarked) }
    
    var showCommentsInline by remember { mutableStateOf(false) }
    var inlineCommentText by remember { mutableStateOf("") }
    
    val inlineComments by if (commentsFlow != null) {
        commentsFlow.collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList<Comment>()) }
    }
    
    // Smooth animated scaling when clicking like
    var isLikeTriggered by remember { mutableStateOf(false) }
    val likeScale by animateFloatAsState(
        targetValue = if (isLikeTriggered) 1.3f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        finishedListener = { isLikeTriggered = false }
    )

    // Keep state aligned with incoming room updates
    LaunchedEffect(post.isLikedByMe, post.likesCount, post.isBookmarked) {
        isLiked = post.isLikedByMe
        likesCount = post.likesCount
        isBookmarked = post.isBookmarked
    }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    var activeImageUrl by remember { mutableStateOf<String?>(null) }
    var activeImageRes by remember { mutableStateOf<Int?>(null) }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 650)) +
                slideInVertically(animationSpec = tween(durationMillis = 650), initialOffsetY = { it / 3 }),
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 7.dp)
                .testTag("post_card_item_${post.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                UserAvatar(
                    name = post.authorName,
                    handle = post.authorHandle,
                    size = 42.dp,
                    avatarUrl = post.authorAvatar
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = post.authorName,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.2.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        // Verified professional checkmark badge
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "موثق",
                            tint = Color(0xFF00BA7C),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = post.authorHandle,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Text(
                            text = formatTimestamp(post.timestamp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }

                // Category tag badge
                val categoryColor = when (post.category) {
                    "برمجة" -> Color(0xFF8B5CF6) // Elegant violet
                    "تقنية" -> Color(0xFF0EA5E9) // Vibrant ocean blue
                    else -> MaterialTheme.colorScheme.primary // Default theme primary
                }
                val categoryBg = categoryColor.copy(alpha = 0.08f)
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(categoryBg)
                        .border(0.5.dp, categoryColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "#${post.category}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = categoryColor,
                        fontSize = 10.sp
                    )
                }

                if (canDelete) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .testTag("delete_post_btn_${post.id}")
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "حذف المنشور",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Content & Image container
            val imageResId = getDrawableIdByName(post.postImage)
            if (imageResId != 0) {
                // CASE A: Post has a beautiful gradient visual background template.
                // We show a premium centered graphic layout where text overlays the background elegantly.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                        .height(210.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                            RoundedCornerShape(16.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = "صورة الخلفية",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { activeImageRes = imageResId }
                    )
                    
                    // Dark luxury gradient overlay for high contrast readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.25f),
                                        Color.Black.copy(alpha = 0.65f)
                                    )
                                )
                            )
                    )
                    
                    // Display Quote content inside
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "“",
                                style = TextStyle(
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 0.sp
                                )
                            )
                            
                            Text(
                                text = post.content,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 24.sp,
                                    textAlign = TextAlign.Center,
                                    shadow = androidx.compose.ui.graphics.Shadow(
                                        color = Color.Black.copy(alpha = 0.6f),
                                        blurRadius = 4f
                                    )
                                ),
                                color = Color.White,
                                maxLines = 5,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            } else {
                // CASE B: Standard post layout (highly legible, adaptive font size, with spacing)
                val isShortText = post.content.length < 90
                Text(
                    text = post.content,
                    style = if (isShortText) {
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp,
                            letterSpacing = 0.1.sp
                        )
                    } else {
                        MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 22.sp
                        )
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                )

                if (!post.postImage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        val painter = coil.compose.rememberAsyncImagePainter(
                            model = coil.request.ImageRequest.Builder(LocalContext.current)
                                .data(post.postImage)
                                .crossfade(true)
                                .build()
                        )
                        Image(
                            painter = painter,
                            contentDescription = "صورة المنشور المرفقة",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { activeImageUrl = post.postImage }
                                .testTag("post_image_click_${post.id}")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(4.dp))

            // Premium responsive action row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like Button
                val likeBgColor = if (isLiked) Color(0xFFF43F5E).copy(alpha = 0.08f) else Color.Transparent
                val likeContentColor = if (isLiked) Color(0xFFF43F5E) else MaterialTheme.colorScheme.onSurfaceVariant
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .testTag("like_btn_${post.id}")
                        .clip(RoundedCornerShape(12.dp))
                        .background(likeBgColor)
                        .clickable {
                            isLikeTriggered = true
                            val originallyLiked = isLiked
                            isLiked = !originallyLiked
                            likesCount = if (!originallyLiked) likesCount + 1 else maxOf(0, likesCount - 1)
                            onLikeClick()
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = stringResource(R.string.like_button_desc),
                        tint = likeContentColor,
                        modifier = Modifier
                            .size(20.dp)
                            .scale(likeScale)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$likesCount",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = likeContentColor
                    )
                }

                // Comment Button
                val commentBgColor = if (showCommentsInline) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent
                val commentContentColor = if (showCommentsInline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .testTag("comment_btn_${post.id}")
                        .clip(RoundedCornerShape(12.dp))
                        .background(commentBgColor)
                        .clickable {
                            showCommentsInline = !showCommentsInline
                            onCommentClick()
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = if (showCommentsInline) Icons.Filled.Comment else Icons.Outlined.ChatBubbleOutline,
                        contentDescription = stringResource(R.string.comment_button_desc),
                        tint = commentContentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.commentsCount}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = commentContentColor
                    )
                }

                // Bookmark Button
                val bookmarkBgColor = if (isBookmarked) Color(0xFFF59E0B).copy(alpha = 0.08f) else Color.Transparent
                val bookmarkContentColor = if (isBookmarked) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant
                
                IconButton(
                    onClick = {
                        onBookmarkClick()
                        val msg = if (!isBookmarked) "تمت الإضافة للمحفوظات! 📥" else "تمت الإزالة من المحفوظات"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .testTag("bookmark_btn_${post.id}")
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bookmarkBgColor)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "حفظ المنشور",
                        tint = bookmarkContentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Share Button
                val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                IconButton(
                    onClick = {
                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(post.content))
                        Toast.makeText(context, "تم نسخ محتوى المنشور إلى الحافظة! 📋✨", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .testTag("share_btn_${post.id}")
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = stringResource(R.string.share_button_desc),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Collapsible inline comments section
            AnimatedVisibility(
                visible = showCommentsInline,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.12f))
                        .padding(14.dp)
                ) {
                    Divider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Text(
                        text = "التعليقات المباشرة (${inlineComments.size}):",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    
                    // Comments list
                    if (inlineComments.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "لا توجد تعليقات بعد. كن أول من يترك تعليقاً ملهماً! 💬",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            // Show first 10 comments or all of them
                            inlineComments.take(10).forEach { comment ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    UserAvatar(
                                        name = comment.authorName,
                                        handle = comment.authorHandle,
                                        size = 32.dp,
                                        textStyle = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                        avatarUrl = comment.authorAvatar
                                    )
                                    
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                                            .border(
                                                0.5.dp,
                                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .padding(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = comment.authorName,
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = formatTimestamp(comment.timestamp),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                                fontSize = 9.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = comment.content,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            if (inlineComments.size > 10) {
                                Text(
                                    text = "عرض المزيد من التعليقات..",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clickable { onCommentClick() }
                                        .align(Alignment.CenterHorizontally)
                                        .padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                    
                    // Input comment field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inlineCommentText,
                            onValueChange = { if (it.length <= 150) inlineCommentText = it },
                            placeholder = { Text("اكتب تعليقاً...", fontSize = 12.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 40.dp, max = 56.dp)
                                .testTag("inline_comment_input_${post.id}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f),
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            textStyle = TextStyle(fontSize = 12.sp)
                        )
                        
                        Button(
                            onClick = {
                                if (inlineCommentText.isNotBlank()) {
                                    onSubmitCommentInline?.invoke(inlineCommentText)
                                    inlineCommentText = ""
                                } else {
                                    Toast.makeText(context, "الرجاء كتابة تعليق أولاً!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .testTag("inline_comment_submit_${post.id}")
                                .height(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp)
                        ) {
                            Text("تعليق", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    if (activeImageUrl != null || activeImageRes != null) {
        Dialog(
            onDismissRequest = {
                activeImageUrl = null
                activeImageRes = null
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
                    .clickable {
                        activeImageUrl = null
                        activeImageRes = null
                    },
                contentAlignment = Alignment.Center
            ) {
                if (activeImageRes != null && activeImageRes != 0) {
                    Image(
                        painter = painterResource(id = activeImageRes!!),
                        contentDescription = "صورة بالحجم الكامل",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                } else if (!activeImageUrl.isNullOrBlank()) {
                    val painter = coil.compose.rememberAsyncImagePainter(
                        model = coil.request.ImageRequest.Builder(LocalContext.current)
                            .data(activeImageUrl)
                            .crossfade(true)
                            .build()
                    )
                    Image(
                        painter = painter,
                        contentDescription = "صورة بالحجم الكامل",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }

                // Close button at top-end
                IconButton(
                    onClick = {
                        activeImageUrl = null
                        activeImageRes = null
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(24.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .size(44.dp)
                        .testTag("close_fullscreen_image")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق المعاينة",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
}


// ==========================================
// CREATE POST SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onPublishClick: (String, String?, String) -> Unit
) {
    var textContent by remember { mutableStateOf("") }
    var selectedImageKey by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf("عام") }

    val context = LocalContext.current

    // Camera capture launcher
    val cameraLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val path = saveBitmapToFile(context, bitmap)
            if (path != null) {
                selectedImageKey = path
            }
        }
    }

    // Gallery picker launcher
    val galleryLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val path = saveUriToFile(context, uri)
            if (path != null) {
                selectedImageKey = path
            }
        }
    }

    var showEmojiPicker by remember { mutableStateOf(false) }
    val popularEmojis = listOf("😊", "😂", "❤️", "🔥", "👍", "🎉", "✨", "🚀", "💡", "💻", "📝", "💬", "🌟", "🤔", "👏", "🥳")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.create_post_title),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            
            // Professional Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "منشئ احترافي",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Composer Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = textContent,
                    onValueChange = { if (it.length <= 300) textContent = it },
                    placeholder = { 
                        Text(
                            stringResource(R.string.post_hint), 
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        ) 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .testTag("create_post_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    textStyle = TextStyle(fontSize = 14.sp, lineHeight = 20.sp)
                )

                // Preview of captured/selected custom photo
                if (selectedImageKey != null && (selectedImageKey!!.startsWith("/") || selectedImageKey!!.contains("content://"))) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    ) {
                        val painter = coil.compose.rememberAsyncImagePainter(
                            model = coil.request.ImageRequest.Builder(LocalContext.current)
                                .data(selectedImageKey)
                                .crossfade(true)
                                .build()
                        )
                        Image(
                            painter = painter,
                            contentDescription = "معاينة الصورة المرفقة",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Close/Remove Button
                        IconButton(
                            onClick = { selectedImageKey = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                .size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "حذف الصورة",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action tools row: Camera, Gallery, Emoji
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Camera button
                    IconButton(
                        onClick = { cameraLauncher.launch(null) },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                            .testTag("post_camera_btn")
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "التقاط صورة بالكاميرا",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Gallery button
                    IconButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                            .testTag("post_gallery_btn")
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "إرفاق صورة من المعرض",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Emoji button
                    IconButton(
                        onClick = { showEmojiPicker = !showEmojiPicker },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                            .testTag("post_emoji_btn")
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "إضافة رموز تعبيرية",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    if (selectedImageKey != null && (selectedImageKey!!.startsWith("/") || selectedImageKey!!.contains("content://"))) {
                        Text(
                            text = "📸 تم إرفاق صورة",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                // Simple Emoji Picker Row
                if (showEmojiPicker) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            popularEmojis.forEach { emoji ->
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                        .clickable {
                                            if (textContent.length < 300) {
                                                textContent += emoji
                                            }
                                        }
                                        .testTag("emoji_$emoji"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(emoji, fontSize = 18.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Character Count Progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val progress = textContent.length / 300f
                    val progressColor = when {
                        progress > 0.9f -> Color.Red
                        progress > 0.75f -> Color(0xFFF59E0B)
                        else -> MaterialTheme.colorScheme.primary
                    }

                    Text(
                        text = "${textContent.length}/300",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (textContent.length >= 300) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CircularProgressIndicator(
                        progress = progress,
                        modifier = Modifier.size(14.dp),
                        color = progressColor,
                        strokeWidth = 2.5.dp,
                        trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category chooser
        Text(
            text = "اختر تصنيف المنشور:",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val categoriesList = listOf("عام", "تقنية", "برمجة")
            categoriesList.forEach { category ->
                val isSelected = selectedCategory == category
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                        .border(
                            1.dp,
                            if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedCategory = category }
                        .padding(vertical = 10.dp)
                        .testTag("create_category_$category"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#$category",
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Visual Layout Choice
        Text(
            text = "مظهر الخلفية البصرية:",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val templates = listOf(
                Pair("None", null),
                Pair("post_workspace", R.drawable.post_workspace_1782290616565),
                Pair("post_tech_design", R.drawable.post_tech_design_1782290633173),
                Pair("banner_nexus", R.drawable.banner_nexus_1782290597497)
            )

            templates.forEach { (key, drawableId) ->
                val isSelected = selectedImageKey == key || (key == "None" && selectedImageKey == null)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable {
                            selectedImageKey = if (key == "None") null else key
                        }
                ) {
                    if (drawableId != null) {
                        Image(
                            painter = painterResource(id = drawableId),
                            contentDescription = "مرفق صورة",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.15f))
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "بدون خلفية",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = "محدد", tint = Color.White)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Professional Feature: Live Post Preview
        Text(
            text = "معاينة مباشرة للمنشور قبل النشر:",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Mock Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("أنت", color = Color.White, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp))
                    }
                    
                    Column {
                        Text("أنت (معاينة)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                        Text("@you", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "#$selectedCategory",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                // Mock Content Area (with or without image background template)
                val hasBg = selectedImageKey != null
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    if (hasBg) {
                        val drawableId = when (selectedImageKey) {
                            "post_workspace" -> R.drawable.post_workspace_1782290616565
                            "post_tech_design" -> R.drawable.post_tech_design_1782290633173
                            else -> R.drawable.banner_nexus_1782290597497
                        }
                        Image(
                            painter = painterResource(id = drawableId),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.45f))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = if (hasBg) Alignment.Center else Alignment.TopStart
                    ) {
                        Text(
                            text = textContent.ifBlank { "اكتب شيئاً لتظهر معاينته هنا..." },
                            color = if (hasBg) Color.White else MaterialTheme.colorScheme.onSurface,
                            style = if (hasBg) MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ) else MaterialTheme.typography.bodyLarge,
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Publish Action Button
        Button(
            onClick = {
                onPublishClick(textContent, selectedImageKey, selectedCategory)
                textContent = ""
                selectedImageKey = null
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("publish_post_btn"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.Send, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.publish), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
        }
    }
}

// ==========================================
// PROFILE SCREEN
// ==========================================
@Composable
fun ProfileScreen(
    profile: Profile,
    profilePosts: List<Post>,
    onSaveProfile: (String, String, String, String?) -> Unit,
    onLikeClick: (Int) -> Unit,
    onBookmarkClick: (Int) -> Unit,
    onCommentClick: (Int) -> Unit,
    onDeleteClick: (Post) -> Unit,
    commentsFlowOfPost: ((Int) -> kotlinx.coroutines.flow.Flow<List<Comment>>)? = null,
    onSubmitCommentInline: ((Int, String) -> Unit)? = null
) {
    var isEditing by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Banner & Avatar Header Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                // Background cover banner
                val bannerResId = getDrawableIdByName(profile.bannerUrl)
                if (bannerResId != 0) {
                    Image(
                        painter = painterResource(id = bannerResId),
                        contentDescription = "غلاف الحساب",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.tertiaryContainer
                                    )
                                )
                            )
                    )
                }

                // User Avatar overlaying the bottom margin of the banner
                Box(
                    modifier = Modifier
                        .padding(start = 20.dp, top = 100.dp)
                        .size(90.dp)
                        .clip(CircleShape)
                        .border(4.dp, MaterialTheme.colorScheme.background, CircleShape)
                        .shadow(4.dp, CircleShape)
                ) {
                    UserAvatar(name = profile.username, handle = profile.handle, size = 90.dp, textStyle = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Black), avatarUrl = profile.avatarUrl)
                }
            }
        }

        // Action panel for Profile (edit toggle)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = profile.username,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = profile.handle,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Button(
                        onClick = { isEditing = !isEditing },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEditing) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primaryContainer,
                            contentColor = if (isEditing) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("toggle_edit_profile")
                    ) {
                        Icon(
                            imageVector = if (isEditing) Icons.Filled.Close else Icons.Filled.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEditing) "إلغاء التعديل" else stringResource(R.string.edit_profile),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Conditionally render Profile editing form
                if (isEditing) {
                    EditProfileForm(
                        profile = profile,
                        onSave = { name, handle, bio, avatarUrl ->
                            onSaveProfile(name, handle, bio, avatarUrl)
                            isEditing = false
                        }
                    )
                } else {
                    // Regular profile Bio Display
                    Text(
                        text = profile.bio,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Dashboard Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(count = profile.postsCount, label = stringResource(R.string.posts_count), modifier = Modifier.weight(1f))
                    StatCard(count = profile.followersCount, label = stringResource(R.string.followers_count), modifier = Modifier.weight(1f))
                    StatCard(count = profile.followingCount, label = stringResource(R.string.following_count), modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(20.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "منشوراتي الشخصية",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        // Profile Specific Posts list
        if (profilePosts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "لم تقم بنشر أي منشور بعد.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            items(profilePosts, key = { it.id }) { post ->
                PostCardItem(
                    post = post,
                    onLikeClick = { onLikeClick(post.id) },
                    onBookmarkClick = { onBookmarkClick(post.id) },
                    onCommentClick = { onCommentClick(post.id) },
                    onDeleteClick = { onDeleteClick(post) },
                    canDelete = true,
                    commentsFlow = commentsFlowOfPost?.invoke(post.id),
                    onSubmitCommentInline = { content -> onSubmitCommentInline?.invoke(post.id, content) }
                )
            }
        }
    }
}

// ==========================================
// FORM COMPOSABLE FOR EDITING PROFILE
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileForm(
    profile: Profile,
    onSave: (String, String, String, String?) -> Unit
) {
    var name by remember { mutableStateOf(profile.username) }
    var handle by remember { mutableStateOf(profile.handle) }
    var bio by remember { mutableStateOf(profile.bio) }
    var avatarUrl by remember { mutableStateOf<String?>(profile.avatarUrl) }

    val context = LocalContext.current

    // Camera capture launcher for Avatar
    val cameraLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val path = saveBitmapToFile(context, bitmap)
            if (path != null) {
                avatarUrl = path
            }
        }
    }

    // Gallery picker launcher for Avatar
    val galleryLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val path = saveUriToFile(context, uri)
            if (path != null) {
                avatarUrl = path
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Edit Avatar Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                UserAvatar(
                    name = name.ifBlank { "مستخدم" },
                    handle = handle.ifBlank { "@user" },
                    size = 64.dp,
                    avatarUrl = avatarUrl,
                    textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "تغيير الصورة الشخصية",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(
                            onClick = { cameraLauncher.launch(null) },
                            modifier = Modifier.testTag("avatar_camera_btn"),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("الكاميرا", fontSize = 12.sp)
                        }

                        TextButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier.testTag("avatar_gallery_btn"),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("المعرض", fontSize = 12.sp)
                        }

                        if (avatarUrl != null) {
                            TextButton(
                                onClick = { avatarUrl = null },
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("إزالة", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), thickness = 0.5.dp)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.username)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("edit_name_input"),
                singleLine = true
            )

            OutlinedTextField(
                value = handle,
                onValueChange = { handle = it },
                label = { Text(stringResource(R.string.user_handle)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("edit_handle_input"),
                singleLine = true
            )

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text(stringResource(R.string.bio)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("edit_bio_input"),
                maxLines = 3
            )

            Button(
                onClick = { onSave(name, handle, bio, avatarUrl) },
                modifier = Modifier
                    .align(Alignment.End)
                    .testTag("save_profile_button"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(R.string.save_changes), fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// STATISTIC CONTAINER CARD
// ==========================================
@Composable
fun StatCard(
    count: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatStatCount(count),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

// ==========================================
// COMMENTS DIALOG (Sliding Bottom Sheet vibe)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsSheet(
    post: Post,
    comments: List<Comment>,
    onDismiss: () -> Unit,
    onSubmitComment: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 28.dp) // Leave a neat gap at the top to feel like a high-fidelity bottom sheet
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header bar of comments overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.Comment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = stringResource(R.string.comments_title),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "(${comments.size})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("comments_close_btn")
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "إغلاق")
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Nested view: Original post snippet (so context is never lost)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        UserAvatar(name = post.authorName, handle = post.authorHandle, size = 32.dp, avatarUrl = post.authorAvatar)
                        Column {
                            Text(
                                text = post.authorName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = post.content,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Scrollable Comments List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (comments.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "لا توجد تعليقات بعد. كن أول من يعلق! ✨",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }
                    } else {
                        items(comments, key = { it.id }) { comment ->
                            CommentRowItem(comment = comment)
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Sticky Comment Input Footer
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = { Text(stringResource(R.string.comment_hint), fontSize = 14.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("comment_input_field"),
                            maxLines = 2,
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )

                        IconButton(
                            onClick = {
                                if (commentText.isNotBlank()) {
                                    onSubmitComment(commentText)
                                    commentText = ""
                                }
                            },
                            enabled = commentText.isNotBlank(),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                disabledContainerColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .size(44.dp)
                                .testTag("submit_comment_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Send,
                                contentDescription = stringResource(R.string.post_comment),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// INDIVIDUAL COMMENT ITEM ROW
// ==========================================
@Composable
fun CommentRowItem(comment: Comment) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        UserAvatar(name = comment.authorName, handle = comment.authorHandle, size = 32.dp, avatarUrl = comment.authorAvatar)
        
        Column(
            modifier = Modifier
                .weight(1f)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = comment.authorName,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = comment.authorHandle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formatTimestamp(comment.timestamp),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ==========================================
// UTILITIES (STAT FORMATTERS & TIMESTAMPS)
// ==========================================
private fun formatTimestamp(timeMs: Long): String {
    val date = Date(timeMs)
    val formatter = SimpleDateFormat("h:mm a", Locale("ar"))
    return formatter.format(date)
}

private fun formatStatCount(count: Int): String {
    return when {
        count >= 1000000 -> String.format("%.1fM", count / 1000000.0)
        count >= 1000 -> String.format("%.1fK", count / 1000.0)
        else -> count.toString()
    }
}

// Custom helpers to save bitmap/uri to local application cache files
private fun saveBitmapToFile(context: android.content.Context, bitmap: android.graphics.Bitmap): String? {
    return try {
        val fileName = "captured_post_image_${System.currentTimeMillis()}.png"
        val file = java.io.File(context.cacheDir, fileName)
        java.io.FileOutputStream(file).use { out ->
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun saveUriToFile(context: android.content.Context, uri: android.net.Uri): String? {
    return try {
        val fileName = "attached_post_image_${System.currentTimeMillis()}.png"
        val file = java.io.File(context.cacheDir, fileName)
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            java.io.FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
