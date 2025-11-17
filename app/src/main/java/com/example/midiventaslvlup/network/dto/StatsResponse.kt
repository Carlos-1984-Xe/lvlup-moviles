package com.example.midiventaslvlup.network.dto

import com.google.gson.annotations.SerializedName

data class StatsResponse(
    @SerializedName("totalUsers")
    val totalUsers: Int,

    @SerializedName("clientes")
    val clientes: Int,

    @SerializedName("admins")
    val admins: Int
)
