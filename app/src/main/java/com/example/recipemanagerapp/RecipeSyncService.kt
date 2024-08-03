package com.example.recipemanagerapp

import android.app.IntentService
import android.content.Intent

class RecipeSyncService : IntentService("RecipeSyncService") {
    override fun onHandleIntent(intent: Intent?) {
        // Sync recipes with an online database
    }
}