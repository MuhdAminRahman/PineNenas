// /app/src/main/java/com/example/pinenenas/ui/myshop/MyShopViewModel.kt
package com.example.pinenenas.ui.myshop

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.example.pinenenas.data.local.AppDatabase
import com.example.pinenenas.data.model.Product

class MyShopViewModel(application: Application, savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {

    private val productDao = AppDatabase.getInstance(application).productDao()

    // Get the userId from the navigation arguments
    private val userId: Long? = savedStateHandle.get<Long>("userId")

    // LiveData holding the products for the specific user
    // Fetch products only if the userId is valid
    val shopProducts: LiveData<List<Product>> = if (userId != null && userId != -1L) {
        // No conversion needed!
        productDao.getProductsForUser(userId)
    } else {
        // If no valid userId, return an empty list.
        MutableLiveData(emptyList())
    }
}
