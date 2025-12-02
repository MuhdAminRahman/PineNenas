// /app/src/main/java/com/example/pinenenas/ui/myshop/MyShopViewModelFactory.kt
package com.example.pinenenas.ui.myshop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras

class MyShopViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        // Get the Application object from CreationExtras
        val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
        // Get the SavedStateHandle from CreationExtras
        val savedStateHandle = extras.createSavedStateHandle()

        if (modelClass.isAssignableFrom(MyShopViewModel::class.java)) {
            return MyShopViewModel(application, savedStateHandle) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
