package com.example.pinenenas.ui.marketplace

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.pinenenas.data.local.AppDatabase
import com.example.pinenenas.data.model.UserDetail

class MarketplaceViewModel(application: Application) : AndroidViewModel(application) {

    private val userDetailDao = AppDatabase.getInstance(application).userDetail()

    // Expose a LiveData stream of all shop profiles from the database
    val allShops: LiveData<List<UserDetail>> = userDetailDao.getAllShops().asLiveData()

}
