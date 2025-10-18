package com.example.midiventaslvlup.model.repository

import com.example.midiventaslvlup.model.local.UserDao
import com.example.midiventaslvlup.model.local.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    fun getAllUsers(): Flow<List<UserEntity>> {
        return userDao.getAllUsers()
    }

    suspend fun insertUser(user: UserEntity): Long {
        return userDao.insert(user)
    }
}
