package com.sportech.base

object AppConfig {
    const val CLUB_URL = "${{ github.event.inputs.club_url }}"
    const val APP_ID = "${{ github.event.inputs.app_id }}"
    const val SPORTECH_API = "https://api-sportech.trycloudflare.com/api/status/"
}
