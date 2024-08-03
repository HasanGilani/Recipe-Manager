package com.example.recipemanagerapp

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var recipeImage: ImageView
    private lateinit var recipeTitle: TextView
    private lateinit var recipeDescription: TextView
    private lateinit var deleteRecipeButton: Button
    private lateinit var setAlarmButton: Button
    private lateinit var recipeRepository: RecipeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        recipeImage = findViewById(R.id.recipeImage)
        recipeTitle = findViewById(R.id.recipeTitle)
        recipeDescription = findViewById(R.id.recipeDescription)
        deleteRecipeButton = findViewById(R.id.deleteRecipeButton)
        setAlarmButton = findViewById(R.id.setAlarmButton)
        recipeRepository = RecipeRepository()

        // Get the recipe data from the intent
        val recipeId = intent.getStringExtra("RECIPE_ID")
        if (recipeId != null) {
            lifecycleScope.launch {
                val recipe = recipeRepository.getRecipeById(recipeId)
                if (recipe != null) {
                    recipeTitle.text = recipe.title
                    recipeDescription.text = recipe.description
                    // Load image using Glide or Picasso
                    // Glide.with(this@RecipeDetailActivity).load(recipe.imageUrl).into(recipeImage)
                }
            }
        }

        deleteRecipeButton.setOnClickListener {
            if (recipeId != null) {
                lifecycleScope.launch {
                    val recipe = recipeRepository.getRecipeById(recipeId)
                    if (recipe != null) {
                        recipeRepository.delete(recipe)
                        finish() // Close the activity
                    }
                }
            }
        }

        setAlarmButton.setOnClickListener {
            // Set a cooking alarm using the AlarmManager
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_HOUR, 12)
                putExtra(AlarmClock.EXTRA_MINUTES, 0)
                putExtra(AlarmClock.EXTRA_MESSAGE, recipeTitle.text.toString())
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
    }
}