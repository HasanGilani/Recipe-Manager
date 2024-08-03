package com.example.recipemanagerapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AddEditRecipeActivity : AppCompatActivity() {

    private lateinit var editTextRecipeTitle: EditText
    private lateinit var editTextRecipeDescription: EditText
    private lateinit var saveRecipeButton: Button
    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_recipe)

        editTextRecipeTitle = findViewById(R.id.editTextRecipeTitle)
        editTextRecipeDescription = findViewById(R.id.editTextRecipeDescription)
        saveRecipeButton = findViewById(R.id.saveRecipeButton)

        // Check if we are editing an existing recipe
        val recipeId = intent.getStringExtra("RECIPE_ID")
        if (recipeId != null) {
            viewModel.getRecipeById(recipeId).observe(this) { recipe ->
                if (recipe != null) {
                    editTextRecipeTitle.setText(recipe.title)
                    editTextRecipeDescription.setText(recipe.description)
                }
            }
        }

        saveRecipeButton.setOnClickListener {
            Log.d("AddEditRecipeActivity", "Save button clicked")
            val title = editTextRecipeTitle.text.toString()
            val description = editTextRecipeDescription.text.toString()
            if (recipeId == null) {
                val newRecipe = Recipe(title = title, description = description, imageUrl = "example.jpg") // Replace with actual image URL
                viewModel.insert(newRecipe)
                finish() // Close the activity
            } else {
                val updatedRecipe = Recipe(id = recipeId, title = title, description = description, imageUrl = "example.jpg") // Replace with actual image URL
                viewModel.update(updatedRecipe)
                finish() // Close the activity
            }
        }
    }
}