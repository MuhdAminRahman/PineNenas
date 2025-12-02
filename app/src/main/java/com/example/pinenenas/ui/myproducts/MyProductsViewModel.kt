package com.example.pinenenas.ui.myproducts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pinenenas.data.local.AppDatabase
import com.example.pinenenas.data.local.ProductDao
import com.example.pinenenas.data.local.SessionManager
import com.example.pinenenas.data.model.Product
import kotlinx.coroutines.launch

class MyProductsViewModel(application: Application) : AndroidViewModel(application) {

    private val productDao: ProductDao
    private val sessionManager: SessionManager
    val userProducts: LiveData<List<Product>>

    init {
        val database = AppDatabase.getInstance(application)
        productDao = database.productDao()
        sessionManager = SessionManager(application)

        val userId = sessionManager.getLoggedInUserId()

        // Fetch products for the logged-in user. If no user is logged in, return an empty list.
        userProducts = if (userId != -1L) {
            productDao.getProductsForUser(userId)
        } else {
            MutableLiveData(emptyList()) // Return an empty LiveData if no user is logged in
        }    }

    fun insert(product: Product) = viewModelScope.launch {
        productDao.insert(product)
    }

    fun update(product: Product) = viewModelScope.launch {
        productDao.update(product)
    }

    fun delete(product: Product) = viewModelScope.launch {
        productDao.delete(product)
    }
    fun getCurrentUserId(): Long {
        return sessionManager.getLoggedInUserId()
    }
}

