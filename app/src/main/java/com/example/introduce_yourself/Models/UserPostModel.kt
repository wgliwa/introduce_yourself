package com.example.introduce_yourself.Models


data class UserPostModel(
    var post_title: String,
    var post_content: String,
    var date: java.time.LocalDateTime,
    val image: ByteArray,
    val likes: Int? = 0,
    val dislikes: Int? = 0,
    var id: Int? = 0,
    var current_user_L: Int? =0 //1 likes 0 nothing -1 dislikes
)