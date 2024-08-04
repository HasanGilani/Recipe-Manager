package com.example.recipemanagerapp

import com.google.firebase.firestore.DocumentId

data class Recipe(
    @DocumentId val id: String = "",
    val title: String = "",
    var ingredients: String,
    var recipe: String,
    var imageUrl: String = ""
){
    // No-argument constructor required for Firebase
    constructor() : this("", "", "", "", null.toString())
}