package com.example.recipemanagerapp

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var recipeImage: ImageView
    private lateinit var recipeTitle: TextView
    private lateinit var deleteRecipeButton: Button
    private lateinit var setAlarmButton: Button
    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        recipeImage = findViewById(R.id.recipeImage)
        recipeTitle = findViewById(R.id.recipeTitle)
        //deleteRecipeButton = findViewById(R.id.deleteRecipeButton)
        //setAlarmButton = findViewById(R.id.setAlarmButton)

        val recipeId = intent.getStringExtra("RECIPE_ID")
        if (recipeId != null) {
            viewModel.getRecipeById(recipeId).observe(this) { recipe ->
                if (recipe != null) {
                    recipeTitle.text = recipe.title
                    Glide.with(this).load(recipe.imageUrl).into(recipeImage)
                }
            }
        }

        deleteRecipeButton.setOnClickListener {
            if (recipeId != null) {
                viewModel.getRecipeById(recipeId).observe(this) { recipe ->
                    if (recipe != null) {
                        viewModel.delete(recipe)
                        finish()
                    }
                }
            }
        }

        setAlarmButton.setOnClickListener {
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
