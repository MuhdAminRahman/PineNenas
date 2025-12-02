// Create a new file or add to your existing user DAO
// C:/.../app/src/main/java/com/example/pinenenas/data/local/UserProfileDao.kt
package com.example.pinenenas.data.local

import androidx.room.*
import com.example.pinenenas.data.model.UserDetail
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(userProfile: UserDetail)

    @Query("SELECT * FROM user_detail WHERE userId = :userId")
    fun getProfile(userId: Long): Flow<UserDetail?>

    @Query("SELECT * FROM user_detail ORDER BY shopName ASC")
    fun getAllShops(): Flow<List<UserDetail>>
}
    