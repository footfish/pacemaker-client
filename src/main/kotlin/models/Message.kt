package models

import java.util.UUID

data class Message(
    var message: String = "",
    var from: String = "",
    var read: Boolean = false,
    val id: String = UUID.randomUUID().toString()
    )