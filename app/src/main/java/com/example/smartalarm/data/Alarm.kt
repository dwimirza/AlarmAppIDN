package com.example.smartalarm.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class Alarm(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: String,
    val time: String,
    val note: String,
    val type: Int
)