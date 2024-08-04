package com.example.recipemanagerapp

import android.content.Intent
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), ActionMode.Callback {

    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private val viewModel: RecipeViewModel by viewModels()
    private var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        recipeRecyclerView = findViewById(R.id.recipeRecyclerView)
        recipeRecyclerView.layoutManager = LinearLayoutManager(this)

        recipeAdapter = RecipeAdapter(emptyList(), { recipe ->
            val intent = Intent(this, RecipeScreenActivity::class.java)
            intent.putExtra("RECIPE_ID", recipe.id)
            startActivity(intent)
        }, {
            if (actionMode == null) {
                actionMode = startActionMode(this)
            }
        })

        recipeRecyclerView.adapter = recipeAdapter

        viewModel.getAllRecipes().observe(this) { recipes ->
            recipeAdapter.updateRecipes(recipes)
        }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, AddEditRecipeActivity::class.java)
            startActivity(intent)
        }

        // Start the recipe fetching service
        val serviceIntent = Intent(this, RecipeFetchService::class.java)
        startService(serviceIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        val inflater: MenuInflater = mode?.menuInflater ?: return false
        inflater.inflate(R.menu.menu_delete, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_delete -> {
                val selectedItems = recipeAdapter.getSelectedItems()
                viewModel.deleteRecipes(selectedItems)
                actionMode?.finish()
                true
            }
            else -> false
        }
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        actionMode = null
        recipeAdapter.clearSelection()
    }
}