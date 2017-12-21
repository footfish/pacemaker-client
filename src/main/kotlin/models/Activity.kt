package models

import java.util.UUID

data class Activity(
    var type: String = "",
    var location: String = "",
    var distance: Float = 0.0f)
     {
    var id: String = UUID.randomUUID().toString()		//can't call optional parameters from Java, hence this line moved from constructor. 
    var route: MutableList<Location> = ArrayList()
}


