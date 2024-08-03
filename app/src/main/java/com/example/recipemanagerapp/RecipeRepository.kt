package com.example.recipemanagerapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RecipeRepository {
    private val db = FirebaseFirestore.getInstance()
    private val recipesCollection = db.collection("recipes")

    suspend fun insert(recipe: Recipe) {
        Log.d("RecipeRepository", "Inserting recipe into Firestore: $recipe")
        recipesCollection.add(recipe).await()
    }

    suspend fun update(recipe: Recipe) {
        Log.d("RecipeRepository", "Updating recipe in Firestore: $recipe")
        recipesCollection.document(recipe.id).set(recipe).await()
    }

    suspend fun delete(recipe: Recipe) {
        Log.d("RecipeRepository", "Deleting recipe from Firestore: $recipe")
        recipesCollection.document(recipe.id).delete().await()
    }

    suspend fun getRecipeById(id: String): Recipe? {
        val document = recipesCollection.document(id).get().await()
        Log.d("RecipeRepository", "Fetched recipe: ${document.toObject(Recipe::class.java)}")
        return document.toObject(Recipe::class.java)
    }

    fun getAllRecipes(): LiveData<List<Recipe>> {
        val liveData = MutableLiveData<List<Recipe>>()
        recipesCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("RecipeRepository", "Error fetching recipes", e)
                liveData.value = emptyList()
                return@addSnapshotListener
            }
            val recipes = snapshot?.toObjects(Recipe::class.java) ?: emptyList()
            liveData.value = recipes
            Log.d("RecipeRepository", "Fetched recipes: $recipes")
        }
        return liveData
    }
}