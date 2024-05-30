package com.example.Project_Spark

import java.util.UUID

data class User(
    val friends: List<String> = emptyList()
)

data class Friend(
    val uid: String = "",
    val name: String = "",
    val imageUrl: String = ""
)

data class Team(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val department: String = "",
    val description: String = "",
    val members: List<String> = emptyList()
)

data class Meeting(
    val date: String = "",
    val teamName: String = "",
    val members: List<String> = emptyList()
)
