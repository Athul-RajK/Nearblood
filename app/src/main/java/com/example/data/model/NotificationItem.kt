package com.example.data.model

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: String = "info", // "approval", "reminder", "eligibility", "birthday"
    val read: Boolean = false
)
