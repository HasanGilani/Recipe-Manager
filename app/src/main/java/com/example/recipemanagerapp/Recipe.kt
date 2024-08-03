package com.example.recipemanagerapp

import com.google.firebase.firestore.DocumentId

data class Recipe(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = ""
)