package com.example.recipemanagerapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecipeAdapter(
    private var recipes: List<Recipe>,
    private val onItemClick: (Recipe) -> Unit,
    private val onItemLongClick: () -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private val selectedItems = mutableSetOf<Recipe>()
    var isSelectionMode = false
        private set

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeTitle: TextView = itemView.findViewById(R.id.recipeTitle)
        val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)

        fun bind(recipe: Recipe, isSelected: Boolean, isSelectionMode: Boolean) {
            recipeTitle.text = recipe.title
            checkBox.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
            checkBox.isChecked = isSelected
            if (recipe.imageUrl != null && recipe.imageUrl!!.isNotEmpty()) {
                recipeImage.visibility = View.VISIBLE
                Glide.with(itemView.context).load(recipe.imageUrl).into(recipeImage)
            } else {
                recipeImage.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe, selectedItems.contains(recipe), isSelectionMode)

        holder.itemView.setOnClickListener {
            if (isSelectionMode) {
                toggleSelection(recipe, position)
            } else {
                onItemClick(recipe)
            }
        }

        holder.itemView.setOnLongClickListener {
            if (!isSelectionMode) {
                isSelectionMode = true
                notifyItemRangeChanged(0, recipes.size)
            }
            toggleSelection(recipe, position)
            onItemLongClick()
            true
        }
    }

    override fun getItemCount() = recipes.size

    private fun toggleSelection(recipe: Recipe, position: Int) {
        if (selectedItems.contains(recipe)) {
            selectedItems.remove(recipe)
        } else {
            selectedItems.add(recipe)
        }
        notifyItemChanged(position)
    }

    fun updateRecipes(newRecipes: List<Recipe>) {
        recipes = newRecipes
        selectedItems.clear()
        isSelectionMode = false
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<Recipe> {
        return selectedItems.toList()
    }

    fun clearSelection() {
        selectedItems.clear()
        isSelectionMode = false
        notifyDataSetChanged()
    }
}
