package com.example.midiventaslvlup.model.repository

import com.example.midiventaslvlup.model.local.UserDao
import com.example.midiventaslvlup.model.local.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    fun getAllUsers(): Flow<List<UserEntity>> {
        return userDao.getAllUsers()
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    suspend fun insertUser(user: UserEntity): Long {
        return userDao.insert(user)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.update(user)
    }

    suspend fun deleteUser(user: UserEntity) {
        userDao.delete(user)
    }
}
