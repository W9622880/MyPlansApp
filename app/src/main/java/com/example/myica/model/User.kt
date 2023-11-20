package com.example.myica.model

data class User(
    val __v: Int,
    val _id: String,
    val address: String,
    val bio: String,
    val count: Int,
    val country: String,
    val coverPictureURL: String,
    val created: String,
    val dateOfBirth: String,
    val favouriteFoods: String,
    val firstName: String,
    val followers: Int,
    val following: Int,
    val gender: String,
    val hasPassword: Boolean,
    val hasPaymentMethod: Boolean,
    val isBusinessOwner: Boolean,
    val isFollowing: Boolean,
    val isPackageDue: Boolean,
    val isPhoneNumberVerified: Boolean,
    val isProfileIncomplete: Boolean,
    val lastName: String,
    val noOfRates: Int,
    val `package`: String,
    val packageDueDate: String,
    val profilePictureURL: String,
    val rating: Int,
    val referralCode: String,
    val reviews: Int,
    val status: String,
    val timeStamp: Long,
    val type: String
)