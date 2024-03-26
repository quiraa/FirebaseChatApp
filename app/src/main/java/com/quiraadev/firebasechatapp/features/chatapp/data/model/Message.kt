package com.quiraadev.firebasechatapp.features.chatapp.data.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Message(
	val text: String? = null,
	val name: String? = null,
	val photoUrl: String? = null,
	val timeStamp: String? = null,
) {


}
