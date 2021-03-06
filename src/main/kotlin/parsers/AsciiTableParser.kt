package parsers
import com.bethecoder.ascii_table.ASCIITable
import com.bethecoder.ascii_table.impl.CollectionASCIITableAware
import com.bethecoder.ascii_table.spec.IASCIITableAware
import java.util.ArrayList
import java.util.Arrays
import models.Activity
import models.Location
import models.User
import models.Message
import models.Leader

class AsciiTableParser:Parser() {
	
  override fun renderUser(user:User?) {
    if (user != null) {
      renderUsers(Arrays.asList(user))
      println("ok")
    } else {
      println("Strange! User not found")
    }
  }

	override fun renderUsers(users:Collection<User>?) {
    if (users != null && !users.isEmpty())  {
        val userList = ArrayList<User>(users)
        val asciiTableAware = CollectionASCIITableAware<User>(userList, "id","firstname","lastname", "email", "password", "disabled", "admin")
        println(ASCIITable.getInstance().getTable(asciiTableAware))
    } else {
        println("No users found")
    }
  }
	
	override fun renderFriendUsers(users:Collection<User>?) {
    if (users != null && !users.isEmpty())  {
       val userList = ArrayList<User>(users)
       val asciiTableAware = CollectionASCIITableAware<User>(userList,"firstname","lastname", "email" )
       println(ASCIITable.getInstance().getTable(asciiTableAware))
    } else  {
       println("No friends (yet!)")
    }
  }
	
  override fun renderActivity(activity:Activity?) {
    if (activity != null) {
      renderActivities(Arrays.asList(activity))
    } else {
      println("not found")
    }
  }
	
  override fun renderActivities(activities:Collection<Activity>?) {
    if (activities != null && !activities.isEmpty()) {
      val activityList = ArrayList(activities)
      val asciiTableAware = CollectionASCIITableAware<Activity>(activityList,"id","type", "location", "distance")
      println(ASCIITable.getInstance().getTable(asciiTableAware))
    } else {
		  println("No activities")
    }
  }
	
  override fun renderLocations(locations:List<Location>?) {
    if (locations != null && !locations.isEmpty()) {
      val asciiTableAware = CollectionASCIITableAware<Location>(locations,"latitude", "longitude")
      println(ASCIITable.getInstance().getTable(asciiTableAware))
    } else {
      println("no locations found")
    }
  }
	
	  override fun renderMessages(messages:Collection<Message>?) {
    if (messages != null && !messages.isEmpty()) {
	    val messageList = ArrayList(messages)
      val asciiTableAware = CollectionASCIITableAware<Message>(messageList,"message", "from", "id")
      println(ASCIITable.getInstance().getTable(asciiTableAware))
    } else {
      println("no messages for you")
    }
  }
	
  override fun renderLeaders(leaders:Collection<Leader>?) {
    if (leaders != null && !leaders.isEmpty()) {
      val leadersList = ArrayList(leaders)
      val asciiTableAware = CollectionASCIITableAware<Leader>(leadersList,"firstname","lastname", "email", "distance")
      println(ASCIITable.getInstance().getTable(asciiTableAware))
    } else {
      println("No leader board yet")
    }
  }	
	
}