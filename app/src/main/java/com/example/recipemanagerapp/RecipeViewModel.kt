package com.example.recipemanagerapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {
    private val repository = RecipeRepository()

    fun getAllRecipes() = repository.getAllRecipes()

    fun getRecipeById(id: String) = repository.getRecipeById(id)

    fun insert(recipe: Recipe) {
        viewModelScope.launch {
            repository.insert(recipe)
        }
    }

    fun update(recipe: Recipe) {
        viewModelScope.launch {
            repository.update(recipe)
        }
    }

    fun delete(recipe: Recipe) {
        viewModelScope.launch {
            repository.delete(recipe)
        }
    }

    fun deleteRecipes(recipes: List<Recipe>) {
        viewModelScope.launch {
            repository.deleteRecipes(recipes)
        }
    }
}
