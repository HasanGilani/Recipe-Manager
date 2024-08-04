package com.example.recipemanagerapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.util.UUID

class AddEditRecipeActivity : AppCompatActivity() {

    private lateinit var editTextRecipeTitle: EditText
    private lateinit var editTextRecipeIngredients: EditText
    private lateinit var editTextRecipe: EditText
    private lateinit var saveRecipeButton: Button
    private lateinit var selectImageButton: Button
    private lateinit var recipeImageView: ImageView
    private val viewModel: RecipeViewModel by viewModels()
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_recipe)

        editTextRecipeTitle = findViewById(R.id.editTextRecipeTitle)
        editTextRecipeIngredients = findViewById(R.id.editTextRecipeIngredients)
        editTextRecipe = findViewById(R.id.editTextRecipe)
        saveRecipeButton = findViewById(R.id.saveRecipeButton)
        selectImageButton = findViewById(R.id.selectImageButton)
        recipeImageView = findViewById(R.id.recipeImageView)

        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        val recipeId = intent.getStringExtra("RECIPE_ID")
        if (recipeId != null) {
            viewModel.getRecipeById(recipeId).observe(this) { recipe ->
                if (recipe != null) {
                    editTextRecipeTitle.setText(recipe.title)
                    editTextRecipeIngredients.setText(recipe.ingredients)
                    editTextRecipe.setText(recipe.recipe)
                    recipe.imageUrl?.let {
                        Glide.with(this).load(it).into(recipeImageView)
                        recipeImageView.visibility = View.VISIBLE
                    }
                }
            }
        }

        saveRecipeButton.setOnClickListener {
            val title = editTextRecipeTitle.text.toString()
            val ingredients = editTextRecipeIngredients.text.toString()
            val recipeText = editTextRecipe.text.toString()

            if (title.isEmpty() || ingredients.isEmpty() || recipeText.isEmpty() || imageUri == null) {
                Toast.makeText(this, "Please fill all fields and upload an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (recipeId == null) {
                val newRecipe = Recipe(title = title, ingredients = ingredients, recipe = recipeText)
                uploadImageAndSaveRecipe(newRecipe)
            } else {
                val updatedRecipe = Recipe(id = recipeId, title = title, ingredients = ingredients, recipe = recipeText)
                uploadImageAndSaveRecipe(updatedRecipe)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            recipeImageView.setImageURI(imageUri)
            recipeImageView.visibility = View.VISIBLE
        }
    }

    private fun uploadImageAndSaveRecipe(recipe: Recipe) {
        val storageReference = FirebaseStorage.getInstance().reference
        val imageReference = storageReference.child("recipe_images/${UUID.randomUUID()}.jpg")
        imageUri?.let { uri ->
            imageReference.putFile(uri)
                .addOnSuccessListener {
                    Log.d("AddEditRecipeActivity", "Image uploaded successfully")
                    imageReference.downloadUrl.addOnSuccessListener { downloadUrl ->
                        Log.d("AddEditRecipeActivity", "Image URL: $downloadUrl")
                        recipe.imageUrl = downloadUrl.toString()
                        if (recipe.id.isEmpty()) {
                            viewModel.insert(recipe)
                        } else {
                            viewModel.update(recipe)
                        }
                        finish()
                    }.addOnFailureListener {
                        Log.e("AddEditRecipeActivity", "Failed to get download URL", it)
                    }
                }.addOnFailureListener {
                    Log.e("AddEditRecipeActivity", "Image upload failed", it)
                }
        }
    }
}
