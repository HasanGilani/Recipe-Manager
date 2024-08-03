package com.example.recipemanagerapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RecipeRepository()

    fun insert(recipe: Recipe) = viewModelScope.launch {
        repository.insert(recipe)
    }

    fun update(recipe: Recipe) = viewModelScope.launch {
        repository.update(recipe)
    }

    fun delete(recipe: Recipe) = viewModelScope.launch {
        repository.delete(recipe)
    }

    fun getRecipeById(id: String): LiveData<Recipe?> {
        val liveData = MutableLiveData<Recipe?>()
        viewModelScope.launch {
            liveData.value = repository.getRecipeById(id)
        }
        return liveData
    }

    fun getAllRecipes(): LiveData<List<Recipe>> {
        return repository.getAllRecipes()
    }
}