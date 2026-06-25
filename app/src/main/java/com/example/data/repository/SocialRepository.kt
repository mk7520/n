package com.example.data.repository

import com.example.data.local.CommentDao
import com.example.data.local.PostDao
import com.example.data.local.ProfileDao
import com.example.data.local.StoryDao
import com.example.data.model.Comment
import com.example.data.model.Post
import com.example.data.model.Profile
import com.example.data.model.Story
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class SocialRepository(
    private val profileDao: ProfileDao,
    private val postDao: PostDao,
    private val commentDao: CommentDao,
    private val storyDao: StoryDao
) {
    val profileFlow: Flow<Profile?> = profileDao.getProfileFlow()
    val allPostsFlow: Flow<List<Post>> = postDao.getAllPostsFlow()
    val allStoriesFlow: Flow<List<Story>> = storyDao.getAllStoriesFlow()

    fun getCommentsForPostFlow(postId: Int): Flow<List<Comment>> =
        commentDao.getCommentsForPostFlow(postId)

    suspend fun getProfile(): Profile? = withContext(Dispatchers.IO) {
        profileDao.getProfile()
    }

    suspend fun insertProfile(profile: Profile) = withContext(Dispatchers.IO) {
        profileDao.insertProfile(profile)
    }

    suspend fun updateProfile(profile: Profile) = withContext(Dispatchers.IO) {
        profileDao.updateProfile(profile)
    }

    suspend fun createPost(content: String, imageUri: String? = null, category: String = "عام") = withContext(Dispatchers.IO) {
        val currentProfile = profileDao.getProfile() ?: return@withContext
        val post = Post(
            authorName = currentProfile.username,
            authorHandle = currentProfile.handle,
            authorAvatar = currentProfile.avatarUrl,
            content = content,
            postImage = imageUri,
            category = category
        )
        postDao.insertPost(post)
        
        // Dynamically increment the post count in the user's profile
        val updatedProfile = currentProfile.copy(postsCount = currentProfile.postsCount + 1)
        profileDao.updateProfile(updatedProfile)
    }

    suspend fun toggleLike(postId: Int) = withContext(Dispatchers.IO) {
        val post = postDao.getAllPostsFlow().firstOrNull()?.find { it.id == postId } ?: return@withContext
        val isCurrentlyLiked = post.isLikedByMe
        val updatedPost = post.copy(
            isLikedByMe = !isCurrentlyLiked,
            likesCount = if (isCurrentlyLiked) post.likesCount - 1 else post.likesCount + 1
        )
        postDao.updatePost(updatedPost)
    }

    suspend fun toggleBookmark(postId: Int) = withContext(Dispatchers.IO) {
        val post = postDao.getAllPostsFlow().firstOrNull()?.find { it.id == postId } ?: return@withContext
        val isCurrentlyBookmarked = post.isBookmarked
        val updatedPost = post.copy(isBookmarked = !isCurrentlyBookmarked)
        postDao.updatePost(updatedPost)
    }

    suspend fun createStory(mediaUrl: String, textOverlay: String? = null) = withContext(Dispatchers.IO) {
        val currentProfile = profileDao.getProfile() ?: return@withContext
        val story = Story(
            authorName = currentProfile.username,
            authorHandle = currentProfile.handle,
            authorAvatar = currentProfile.avatarUrl,
            mediaUrl = mediaUrl,
            textOverlay = textOverlay
        )
        storyDao.insertStory(story)
    }

    suspend fun markStoryAsWatched(storyId: Int) = withContext(Dispatchers.IO) {
        storyDao.markAsWatched(storyId)
    }

    suspend fun addComment(postId: Int, content: String) = withContext(Dispatchers.IO) {
        val currentProfile = profileDao.getProfile() ?: return@withContext
        
        // Insert the comment
        val comment = Comment(
            postId = postId,
            authorName = currentProfile.username,
            authorHandle = currentProfile.handle,
            authorAvatar = currentProfile.avatarUrl,
            content = content
        )
        commentDao.insertComment(comment)
        
        // Increment comment count on the post
        val post = postDao.getAllPostsFlow().firstOrNull()?.find { it.id == postId } ?: return@withContext
        val updatedPost = post.copy(commentsCount = post.commentsCount + 1)
        postDao.updatePost(updatedPost)
    }

    suspend fun deletePost(post: Post) = withContext(Dispatchers.IO) {
        postDao.deletePost(post)
        commentDao.deleteCommentsForPost(post.id)
        
        // Decrement post count in profile
        val currentProfile = profileDao.getProfile() ?: return@withContext
        val updatedProfile = currentProfile.copy(postsCount = maxOf(0, currentProfile.postsCount - 1))
        profileDao.updateProfile(updatedProfile)
    }

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val currentProfile = profileDao.getProfile()
        if (currentProfile == null) {
            // Seed Profile
            val defaultProfile = Profile(
                username = "محمد كمال",
                handle = "@mohammed",
                bio = "مهندس برمجيات ومصمم واجهات مستخدم 💻 | شغوف ببناء تطبيقات أندرويد جميلة وعصرية باستخدام Jetpack Compose 🎨✨",
                avatarUrl = "avatar_mohammed",
                bannerUrl = "banner_nexus",
                followersCount = 1450,
                followingCount = 382,
                postsCount = 3
            )
            profileDao.insertProfile(defaultProfile)

            // Seed Initial Posts
            val post1Id = postDao.insertPost(Post(
                authorName = "سارة أحمد",
                authorHandle = "@sara_dev",
                authorAvatar = "avatar_sara",
                content = "أهلاً بالجميع في نيكسس! 🌌 تطبيق تواصل اجتماعي مذهل بتصميم أندرويد 3 الحديث. الواجهات سريعة جداً والحركات سلسة للغاية! فخورة برؤية مثل هذه التطبيقات العربية الاحترافية. أحييكم على هذا العمل الرائع! 👏💻🚀",
                timestamp = System.currentTimeMillis() - 3600000, // 1 hour ago
                likesCount = 42,
                commentsCount = 2,
                isLikedByMe = true,
                postImage = "post_tech_design"
            ))

            val post2Id = postDao.insertPost(Post(
                authorName = "خالد عمر",
                authorHandle = "@khaled_ai",
                authorAvatar = "avatar_khaled",
                content = "الذكاء الاصطناعي ليس بديلاً عن المبرمج، بل هو أداة تزيد من إنتاجيته بمقدار الضعف إذا أحسن استخدامها. السر يكمن في صياغة الأوامر وفهم كيفية عمل النماذج البرمجية الكبيرة. ما رأيكم في مستقبل الذكاء الاصطناعي بمجال الهواتف المحمولة؟ 🧠📱",
                timestamp = System.currentTimeMillis() - 7200000, // 2 hours ago
                likesCount = 28,
                commentsCount = 3,
                isLikedByMe = false
            ))

            val post3Id = postDao.insertPost(Post(
                authorName = "محمد كمال",
                authorHandle = "@mohammed",
                authorAvatar = "avatar_mohammed",
                content = "الاهتمام بأدق التفاصيل والمسافات (Margins & Padding) وتناسق الألوان هو ما يفرق بين التطبيق العادي والتطبيق الاحترافي الاستثنائي. تجربة المستخدم تبدأ من المظهر البصري وتكتمل بالسرعة والاستجابة الفورية. ✨📐🎨 #برمجة_أندرويد #تصميم",
                timestamp = System.currentTimeMillis() - 14400000, // 4 hours ago
                likesCount = 89,
                commentsCount = 1,
                isLikedByMe = false,
                postImage = "post_workspace"
            ))

            // Seed Initial Comments
            commentDao.insertComment(Comment(
                postId = post1Id.toInt(),
                authorName = "محمد كمال",
                authorHandle = "@mohammed",
                authorAvatar = "avatar_mohammed",
                content = "شكراً جزيلاً لكِ يا سارة! دعمكم هو الدافع الأول لتطوير وابتكار تطبيقات عصرية تليق بالمستخدم العربي. 🌌✨",
                timestamp = System.currentTimeMillis() - 3000000
            ))
            commentDao.insertComment(Comment(
                postId = post1Id.toInt(),
                authorName = "خالد عمر",
                authorHandle = "@khaled_ai",
                authorAvatar = "avatar_khaled",
                content = "اتفق تماماً! التطبيق تحفة فنية ويستحق كل التقدير.",
                timestamp = System.currentTimeMillis() - 2500000
            ))

            commentDao.insertComment(Comment(
                postId = post2Id.toInt(),
                authorName = "سارة أحمد",
                authorHandle = "@sara_dev",
                authorAvatar = "avatar_sara",
                content = "كلام سليم خالد، المبرمج الذي يستغل أدوات الذكاء الاصطناعي بشكل ذكي سيكون له الأفضلية دائماً.",
                timestamp = System.currentTimeMillis() - 6500000
            ))
            commentDao.insertComment(Comment(
                postId = post2Id.toInt(),
                authorName = "يوسف علي",
                authorHandle = "@youssef",
                authorAvatar = "avatar_youssef",
                content = "أتساءل كيف ستتطور قدرات المعالجة المحلية على الأجهزة (On-device AI) في السنوات القادمة؟",
                timestamp = System.currentTimeMillis() - 6000000
            ))
            commentDao.insertComment(Comment(
                postId = post2Id.toInt(),
                authorName = "خالد عمر",
                authorHandle = "@khaled_ai",
                authorAvatar = "avatar_khaled",
                content = "بالتأكيد، المعالجات الجديدة تدعم محركات عصبية قوية جداً مما سيسمح بتشغيل نماذج متطورة دون الحاجة للاتصال بالإنترنت.",
                timestamp = System.currentTimeMillis() - 5500000
            ))

            commentDao.insertComment(Comment(
                postId = post3Id.toInt(),
                authorName = "سارة أحمد",
                authorHandle = "@sara_dev",
                authorAvatar = "avatar_sara",
                content = "نصيحة ذهبية وملاحظة دقيقة جداً! التفاصيل تصنع الفارق دوماً. 📐🎨",
                timestamp = System.currentTimeMillis() - 13000000
            ))

            // Seed Initial Stories
            storyDao.insertStory(Story(
                authorName = "سارة أحمد",
                authorHandle = "@sara_dev",
                authorAvatar = "avatar_sara",
                mediaUrl = "story_neon_space",
                textOverlay = "استكشاف آفاق جديدة في عالم التصميم الرقمي 🌌✨",
                timestamp = System.currentTimeMillis() - 1800000
            ))

            storyDao.insertStory(Story(
                authorName = "خالد عمر",
                authorHandle = "@khaled_ai",
                authorAvatar = "avatar_khaled",
                mediaUrl = "story_nature_sun",
                textOverlay = "تأمل وسلام داخلي بعيداً عن صخب التكنولوجيا 🌅🧘‍♂️",
                timestamp = System.currentTimeMillis() - 3600000
            ))
        }
    }
}
