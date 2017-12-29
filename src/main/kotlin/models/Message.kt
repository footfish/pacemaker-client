package models

import java.util.UUID

data class Message(
    var text: String = "",
    var fromId: String = "",
    var readFlag: Boolean = false,
    val id: String = UUID.randomUUID().toString()
    )