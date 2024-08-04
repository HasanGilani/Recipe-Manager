package com.example.recipemanagerapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeFetchService : LifecycleService() {

    private val TAG = "RecipeFetchService"
    private val handler = Handler()
    private val delay = 60000L // 1 minute delay
    private val firestore = FirebaseFirestore.getInstance()

    private var lastFetchedRecipeId: String? = null

    private val fetchRecipesRunnable = object : Runnable {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun run() {
            fetchNewRecipes()
            handler.postDelayed(this, delay)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service Created")
        handler.post(fetchRecipesRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service Destroyed")
        handler.removeCallbacks(fetchRecipesRunnable)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchNewRecipes() {
        Log.d(TAG, "Fetching new recipes...")
        CoroutineScope(Dispatchers.IO).launch {
            firestore.collection("recipes")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener { result ->
                    if (result.isEmpty) return@addOnSuccessListener
                    val document = result.documents[0]
                    val recipe = document.toObject(Recipe::class.java)
                    if (lastFetchedRecipeId == null || document.id != lastFetchedRecipeId) {
                        sendNotification(recipe?.title ?: "New Recipe")
                        lastFetchedRecipeId = document.id
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error fetching recipes: ", exception)
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification(recipeTitle: String) {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", false)
        if (!notificationsEnabled) return

        val channelId = "recipe_notifications"
        val channelName = "Recipe Notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, channelName, importance)
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("New Recipe Added")
            .setContentText("Check out the new recipe: $recipeTitle")
            .setSmallIcon(R.drawable.ic_notification)
            .build()

        notificationManager.notify(1, notification)
    }
}