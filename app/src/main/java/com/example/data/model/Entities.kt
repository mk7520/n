package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class Profile(
    @PrimaryKey val id: Int = 1, // There is only one current user profile locally
    val username: String,
    val handle: String,
    val bio: String,
    val avatarUrl: String,
    val bannerUrl: String,
    val followersCount: Int,
    val followingCount: Int,
    val postsCount: Int
)

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val authorName: String,
    val authorHandle: String,
    val authorAvatar: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val isBookmarked: Boolean = false,
    val category: String = "عام",
    val postImage: String? = null // Optional local drawable or custom generated asset URL
)

@Entity(tableName = "stories")
data class Story(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val authorName: String,
    val authorHandle: String,
    val authorAvatar: String,
    val mediaUrl: String, // name of local drawable
    val timestamp: Long = System.currentTimeMillis(),
    val textOverlay: String? = null,
    val isWatched: Boolean = false
)

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val postId: Int,
    val authorName: String,
    val authorHandle: String,
    val authorAvatar: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
