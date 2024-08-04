package com.example.recipemanagerapp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import android.widget.ImageView
import android.widget.TextView

class RecipeScreenActivity : AppCompatActivity() {

    private lateinit var recipeTitle: TextView
    private lateinit var recipeImage: ImageView
    private lateinit var recipeIngredients: TextView
    private lateinit var recipeDetails: TextView
    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_screen)

        recipeTitle = findViewById(R.id.recipeTitle)
        recipeImage = findViewById(R.id.recipeImage)
        recipeIngredients = findViewById(R.id.recipeIngredients)
        recipeDetails = findViewById(R.id.recipeDetails)

        val recipeId = intent.getStringExtra("RECIPE_ID")
        if (recipeId != null) {
            viewModel.getRecipeById(recipeId).observe(this) { recipe ->
                if (recipe != null) {
                    recipeTitle.text = recipe.title
                    recipeIngredients.text = recipe.ingredients
                    recipeDetails.text = recipe.recipe
                    Glide.with(this).load(recipe.imageUrl).into(recipeImage)
                }
            }
        }
    }
}