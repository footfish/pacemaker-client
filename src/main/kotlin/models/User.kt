package models

import java.util.UUID

data class User(
    val firstname: String = "",
    val lastname: String = "",
    val email: String = "",
    val password: String = "")
    {
		var id: String = UUID.randomUUID().toString() 	//can't call optional parameters from Java, hence this line moved from constructor. 
}
