package com.example.recipemanagerapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore

class RecipeRepository {

    private val db = FirebaseFirestore.getInstance()
    private val recipesLiveData = MutableLiveData<List<Recipe>>()

    fun getAllRecipes(): LiveData<List<Recipe>> {
        db.collection("recipes")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle the error
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val recipes = snapshot.toObjects(Recipe::class.java)
                    // Handle legacy data with 'description' field
                    recipes.forEach { recipe ->
                        if (recipe.ingredients.isEmpty() && snapshot.documents.isNotEmpty()) {
                            val document = snapshot.documents.firstOrNull { it.getString("title") == recipe.title }
                            recipe.ingredients = document?.getString("description") ?: ""
                        }
                    }
                    recipesLiveData.value = recipes
                }
            }
        return recipesLiveData
    }

    fun insert(recipe: Recipe) {
        db.collection("recipes").add(recipe)
    }

    fun update(recipe: Recipe) {
        db.collection("recipes").document(recipe.id).set(recipe)
    }

    fun delete(recipe: Recipe) {
        db.collection("recipes").document(recipe.id).delete()
    }

    fun deleteRecipes(recipes: List<Recipe>) {
        val batch = db.batch()
        recipes.forEach { recipe ->
            val docRef = db.collection("recipes").document(recipe.id)
            batch.delete(docRef)
        }
        batch.commit()
    }

    fun getRecipeById(id: String): LiveData<Recipe> {
        val liveData = MutableLiveData<Recipe>()
        db.collection("recipes").document(id).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val recipe = document.toObject(Recipe::class.java)
                    liveData.value = recipe
                }
            }
        return liveData
    }
}
