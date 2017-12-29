package controllers
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
	
private	object consoleUser {
		var id: String? = null
	  fun loggedIn(): Boolean { if (id == null) return false else return true }
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
    val user = paceApi.getUserByEmail(email)
    if (user != null)
    {
      if (user.password.equals(password))
      {
        consoleUser.id = user.id 
        console.println("Logged in " + user.email)
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
   if (consoleUser.loggedIn())
    {
 		println( "Logged out ")
		consoleUser.id = null
	  } else {
 		println("Nobody logged in")	   
		}  
	}
			
  @Command(description = "Add activity: create and add an activity for the logged in user")
  fun addActivity(@Param(name = "type") type:String,
                  @Param(name = "location") location:String, @Param(name = "distance") distance:Float) {
   if (consoleUser.loggedIn())
    {
      console.renderActivity(paceApi.createActivity(consoleUser.id!!, type, location, distance))
    }
    else
    {
      console.println("Not permitted, log in please!")
    }
  }

		
  @Command(description = "List Activities: List all activities for logged in user")
  fun listActivities() {
   if (consoleUser.loggedIn())
    {
      console.renderActivities(paceApi.getActivities(consoleUser.id!!))
    }
    else
    {
      console.println("Not permitted, log in please!")
    }
  }
	
  @Command(description = "Add location: Append location to an activity")
  fun addLocation(@Param(name = "activity-id") id:String,
                  @Param(name = "longitude") longitude:Double, @Param(name = "latitude") latitude:Double) {
   if (consoleUser.loggedIn())
    {
      val activity = paceApi.getActivity(consoleUser.id!!, id)
      if (activity !=  null)
      {
        paceApi.addLocation(consoleUser.id!!, activity.id, latitude, longitude)
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
	
  @Command(description = "Activity Report: List all activities for logged in user, sorted alphabetically by type")
  fun activityReport() {
   if (consoleUser.loggedIn())
    {
      console.renderActivities(paceApi.getActivities(consoleUser.id!!)?.sortedWith(compareBy({ it.type }, { it.location } )))
    }
    else
    {
      console.println("Not permitted, log in please!")
    }
  }
	
  @Command(description = "Activity Report: List all activities for logged in user by type. Sorted longest to shortest distance")
  fun activityReport(@Param(name = "byType: type") type:String) {
   if (consoleUser.loggedIn())
    {
      console.renderActivities(paceApi.getActivitiesType(consoleUser.id!!, type)?.sortedWith(compareBy({ it.distance })))
	    }
    else
    {
      console.println("Not permitted, log in please!")
    }
  }
	
 @Command(description = "List all locations for a specific activity")
 fun listActivityLocations(@Param(name = "activity-id") id:String) {
   if (consoleUser.loggedIn())
     {
     val activity = paceApi.getActivity(consoleUser.id!!, id)
     if (activity != null)
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
  fun follow(@Param(name = "email") email:String) {
   if (consoleUser.loggedIn())
      {
      paceApi.createFriend(consoleUser.id!!, email)
		  console.renderUsers(paceApi.getFriends(consoleUser.id!!))
	    }
    else
     {
      console.println("Not permitted, log in please!")
     }
  }	  

	
  @Command(description = "List Friends: List all of the friends of the logged in user")
  fun listFriends() {
	    if (consoleUser.loggedIn())
      {
	      console.renderUsers(paceApi.getFriends(consoleUser.id!!))
      }
		else
		 {
      console.println("Not permitted, log in please!")
     }
  }

  @Command(description = "Unfollow Friends: Stop following a friend")
  fun unfollowFriend(@Param(name = "email") email:String) {
   if (consoleUser.loggedIn())
      {
      paceApi.deleteFriend(consoleUser.id!!, email)
		  console.renderUsers(paceApi.getFriends(consoleUser.id!!))
	    }
    else
     {
      console.println("Not permitted, log in please!")
     }	  
		}
	
  @Command(description = "Friend Activity Report: List all activities of specific friend, sorted alphabetically by type)")
  fun friendActivityReport(@Param(name = "email") email:String) {
   if (consoleUser.loggedIn())
      {
      console.renderActivities(paceApi.getFriendActivities(consoleUser.id!!, email)?.sortedWith(compareBy({ it.type }, { it.location })))
	    }
    else
     {
      console.println("Not permitted, log in please!")
     }	  
		}

  @Command(description = "Message Friend: send a message to a friend")
  fun messageFriend(@Param(name = "email") email:String,
                    @Param(name = "message") message:String) {
   if (consoleUser.loggedIn())
    {
      paceApi.sendMessage(consoleUser.id!!, email, message)
    }
    else
    {
      console.println("Not permitted, log in please!")
    } 
  }	  

  @Command(description = "List Messages: List all messages for the logged in user")
  fun listMessages() {		  
   if (consoleUser.loggedIn())
    {
        console.renderMessages(paceApi.getMessages(consoleUser.id!!))
    }
    else
    {
      console.println("Not permitted, log in please!")
    } 
	  
	}		
}		

	
  // Good Commands


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
