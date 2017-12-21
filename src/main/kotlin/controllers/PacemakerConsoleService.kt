package controllers
import com.google.common.base.Optional
import asg.cliche.Command
import asg.cliche.Param
import java.util.ArrayList
import java.util.Arrays
import java.util.HashSet
import models.Activity
import models.User
import parsers.AsciiTableParser
import parsers.Parser


class PacemakerConsoleService {
  private val paceApi = PacemakerAPI("http://localhost:7000")
  private val console = AsciiTableParser()
	private var loggedInUser:User? = null
	
	 fun getLoggedInUser():String {
		val result: String? = loggedInUser?.email 	 

		if (result  != null)
    {
     return result 
    }
    else
    {
      return "$"
    }
  }
	
  @Command(description = "Register: Create an account for a new user")
  fun register(@Param(name = "first name") firstName:String,
               @Param(name = "last name") lastName:String, @Param(name = "email") email:String,
               @Param(name = "password") password:String) {
    console.renderUser(paceApi.createUser(firstName, lastName, email, password))
  }

	@Command(description = "List Users: List all users emails, first and last names")
  fun listUsers() {
    console.renderUsers(paceApi.getUsers())
  }

	@Command(description = "Login: Log in a registered user in to pacemaker")
  fun login(@Param(name = "email") email:String,
            @Param(name = "password") password:String) {
    val user = Optional.fromNullable(paceApi.getUserByEmail(email))
    if (user.isPresent())
    {
      if (user.get().password.equals(password))
      {
        loggedInUser = user.get() 
        console.println("Logged in " + getLoggedInUser())
        console.println("ok")
      }
      else
      {
        console.println("Error on login")
      }
    }
  }

	@Command(description = "Logout: Logout current user")
  fun logout() {
    val user = Optional.fromNullable(loggedInUser)
    if (user.isPresent())
    {
      console.println("Logging out " + getLoggedInUser())
      console.println("ok")
      loggedInUser = null
    }
    else
    {
      console.println("Sorry, you must be logged in to log out :)")
    }
  }
	
  @Command(description = "Add activity: create and add an activity for the logged in user")
  fun addActivity(@Param(name = "type") type:String,
                  @Param(name = "location") location:String, @Param(name = "distance") distance:Float) {
    val user = Optional.fromNullable(loggedInUser)
    if (user.isPresent())
    {
      console.renderActivity(paceApi.createActivity(user.get().id, type, location, distance))
    }
    else
    {
      console.println("Not permitted, log in please!")
    }
  }
	
  @Command(description = "List Activities: List all activities for logged in user")
  fun listActivities() {
    val user = Optional.fromNullable(loggedInUser)
    if (user.isPresent())
    {
      console.renderActivities(paceApi.getActivities(user.get().id))
    }
    else
    {
      console.println("Not permitted, log in please!")
    }
  }
	
  @Command(description = "Add location: Append location to an activity")
  fun addLocation(@Param(name = "activity-id") id:String,
                  @Param(name = "longitude") longitude:Double, @Param(name = "latitude") latitude:Double) {
    val user = Optional.fromNullable(loggedInUser)
    if (user.isPresent())
    {
      val activity = Optional.fromNullable(paceApi.getActivity(user.get().id, id))
      if (activity.isPresent())
      {
        paceApi.addLocation(user.get().id, activity.get().id, latitude, longitude)
        console.println("ok")
      }
      else
      {
        console.println("not found")
      }
    }
    else
    {
      console.println("Not permitted, log in please!")
    }
  }
	
  @Command(description = "ActivityReport: List all activities for logged in user, sorted alphabetically by type")
  fun activityReport() {
    val user = Optional.fromNullable(loggedInUser)
    if (user.isPresent())
    {
      console.renderActivities(paceApi.getActivities(user.get().id))
    }
    else
    {
      console.println("Not permitted, log in please!")
    }
  }
	
  @Command(description = "Activity Report: List all activities for logged in user by type. Sorted longest to shortest distance")
  fun activityReport(@Param(name = "byType: type") type:String) {
    val user = Optional.fromNullable(loggedInUser)
    if (user.isPresent())
    {
    /*  val reportActivities = ArrayList<E>()
      val usersActivities = paceApi.getActivities(user.get().getId())
      usersActivities.forEach({ a-> if (a.getType().equals(type))
                               reportActivities.add(a) })
      reportActivities.sort({ a1, a2-> if (a1.getDistance() >= a2.getDistance())
                             return@reportActivities.sort -1
                             else
                             return@reportActivities.sort 1 })
      console.renderActivities(reportActivities)*/
    }
    else
    {
      console.println("Not permitted, log in please!")
    }
  }
	
 @Command(description = "List all locations for a specific activity")
 fun listActivityLocations(@Param(name = "activity-id") id:String) {
   val user = Optional.fromNullable(loggedInUser)
   if (user.isPresent())
     {
     val activity = Optional.fromNullable(paceApi.getActivity(user.get().id, id))
     if (activity.isPresent())
       {
        // 	console.renderLocations(activity.get().route);
       }
     }
	   else
	   {
     console.println("Not permitted, log in please!")
	   }
}
	
  @Command(description = "Follow Friend: Follow a specific friend")
  fun follow(@Param(name = "email") email:String) {}
	
  @Command(description = "List Friends: List all of the friends of the logged in user")
  fun listFriends() {}
	
  @Command(description = "Friend Activity Report: List all activities of specific friend, sorted alphabetically by type)")
  fun friendActivityReport(@Param(name = "email") email:String) {}
	
  // Good Commands
  @Command(description = "Unfollow Friends: Stop following a friend")
  fun unfollowFriend() {}
  @Command(description = "Message Friend: send a message to a friend")
  fun messageFriend(@Param(name = "email") email:String,
                    @Param(name = "message") message:String) {}
  @Command(description = "List Messages: List all messages for the logged in user")
  fun listMessages() {}
  @Command(description = "Distance Leader Board: list summary distances of all friends, sorted longest to shortest")
  fun distanceLeaderBoard() {}
  // Excellent Commands
  @Command(description = "Distance Leader Board: distance leader board refined by type")
  fun distanceLeaderBoardByType(@Param(name = "byType: type") type:String) {}
  @Command(description = "Message All Friends: send a message to all friends")
  fun messageAllFriends(@Param(name = "message") message:String) {}
  @Command(description = "Location Leader Board: list sorted summary distances of all friends in named location")
  fun locationLeaderBoard(@Param(name = "location") message:String) {}
  // Outstanding Commands
  // Todo
}