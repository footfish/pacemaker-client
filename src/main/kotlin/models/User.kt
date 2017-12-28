package models

import java.util.UUID

data class User(
    val firstname: String = "",
    val lastname: String = "",
    val email: String = "",
    val password: String = "",
	  val friend: MutableList<String> = ArrayList(),
		val id: String = UUID.randomUUID().toString())

    
