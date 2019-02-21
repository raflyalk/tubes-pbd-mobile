package com.example.freya

class Admin(n_username: String, n_email: String) {
    lateinit var username: String
    lateinit var email: String

    init {
        username = n_username
        email = n_email
    }

}