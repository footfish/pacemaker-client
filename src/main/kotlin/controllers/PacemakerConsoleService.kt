package controllers
import asg.cliche.Command
import asg.cliche.Param
import asg.cliche.ShellFactory
import asg.cliche.ShellDependent
import asg.cliche.Shell
import java.util.ArrayList
import java.util.Arrays
import java.util.HashSet
import models.Activity
import models.User
import parsers.AsciiTableParser
import parsers.Parser


class PacemakerConsoleService (apiUrl: String): ShellDependent{
  private val paceApi = PacemakerAPI(apiUrl)
  private val console = AsciiTableParser()
	private lateinit var myShell:Shell // required to pass parent shell ref. to sub-shell 
	override fun cliSetShell(myShell:Shell) { //ShellDependent.cliSetShell() informs object about the Shell operating it (for sub-shells).
		  this.myShell = myShell
	}
	
private	object consoleUser {
		var id: String? = null
	  fun loggedIn(): Boolean { if (id == null) return false else return true }
	}

	
	@Command(description = "Login: Log in a registered user in to pacemaker")
  fun login(@Param(name = "email") email:String,
            @Param(name = "password") password:String) {
    val user = paceApi.getUserByEmail(email)
    if (user != null)
    {
      if (user.password.equals(password) && !user.disabled)
      {
        consoleUser.id = user.id 
		    console.println("You have " + paceApi.getMessages(consoleUser.id!!)?.count() + " message(s)")  
		    ShellFactory.createSubshell(user.email, myShell, "Welcome " + user.firstname +", ?list for commands, type 'exit' to logout", UserConsole()).commandLoop()
      } else {
        console.println("Bad credentials") 
      }
    } else {
      console.println("Invalid user")
    }
	}
	
	@Command(description = "Admin: Log in a admin user in to pacemaker")
  fun admin(@Param(name = "email") email:String,
            @Param(name = "password") password:String) {
    val user = paceApi.getUserByEmail(email)
    if (user != null)
    {
      if (user.password.equals(password) && user.admin && !user.disabled)
      {
        consoleUser.id = user.id 
		    ShellFactory.createSubshell(user.email, myShell, "Welcome Administrator " + user.firstname +", ?list for commands, type 'exit' to logout", AdminConsole()).commandLoop()
      } else {
        console.println("Bad credentials")
      }
    } else {
      console.println("Invalid user")
    }

	}
	

inner class AdminConsole {     // Console for logged in admin. 
		
	@Command(description = "Exit: Logout current user")  //works with built in exit function 
  fun exit() {
 		println( "Logged out ")
		consoleUser.id = null
	}	
	
	@Command(description = "List Users: List all users emails, first and last names")
  fun listUsers() {
    console.renderUsers(paceApi.getUsers())
  }
	
	@Command(description = "Delete User: Delete a user ")
  fun DeleteUser(@Param(name = "id") id:String) {
		if (paceApi.deleteUser(id) ) {
		  console.println("deleted user with id $id")
		} else {
			console.println("Could not find user with id $id")
		}
  }

	@Command(description = "Block User: Block login for a user ")
  fun blockUser(@Param(name = "id") id:String) {
		var user = paceApi.getUser(id)
		if (user != null) {
		  if ( paceApi.updateUser(id, user.firstname, user.lastname, user.email, user.password, true, user.admin) != null) {
		    console.println("Blocked user with id $id")
		  } else {
		    console.println("Failed to update user with id $id")
		  }
		} else {
		  console.println("Could not find user with id $id")
		}
  }
	
 @Command(description = "Unblock User: Unblock login for a user ")
  fun unblockUser(@Param(name = "id") id:String) {
		var user = paceApi.getUser(id)
		if (user != null) {
		  if ( paceApi.updateUser(id, user.firstname, user.lastname, user.email, user.password, false, user.admin) != null) {
		    console.println("Unblocked user with id $id")
		  } else {
		    console.println("Failed to update user with id $id")
		  }
		} else {
		  console.println("Could not find user with id $id")
		}
  }

 @Command(description = "Make Admin: Make the user an Administrator")
  fun makeAdmin(@Param(name = "id") id:String) {
		var user = paceApi.getUser(id)
		if (user != null) {
		  if ( paceApi.updateUser(id, user.firstname, user.lastname, user.email, user.password, user.disabled, true) != null) {
		    console.println("User with id $id made Administrator")
		  } else {
		    console.println("Failed to update user with id $id")
		  }
		} else {
		  console.println("Could not find user with id $id")
		}
  }
	
 @Command(description = "Remove Admin: Remove any user Administrator privilages")
  fun removeAdmin(@Param(name = "id") id:String) {
		var user = paceApi.getUser(id)
		if (user != null) {
		  if ( paceApi.updateUser(id, user.firstname, user.lastname, user.email, user.password, user.disabled, false) != null) {
		    console.println("User with id $id is set to be a regular user")
		  } else {
		    console.println("Failed to update user with id $id")
		  }
		} else {
		  console.println("Could not find user with id $id")
		}
  }			
	
 @Command(description = "Set Password: Set the password for a user")
  fun setPass(@Param(name = "id") id:String, @Param(name = "password") password:String) {
		var user = paceApi.getUser(id)
		if (user != null) {
		  if ( paceApi.updateUser(id, user.firstname, user.lastname, user.email, password, user.disabled, user.admin) != null) {
		    console.println("Set new password to $password for User with id $id")
		  } else {
		    console.println("Failed to update user with id $id")
		  }
		} else {
		  console.println("Could not find user with id $id")
		}
  }		
		
  @Command(description = "Register-user: Create an account for a new user")
  fun registerUser(@Param(name = "first name") firstName:String,
               @Param(name = "last name") lastName:String, @Param(name = "email") email:String,
               @Param(name = "password") password:String) {
	  if (paceApi.createUser(firstName, lastName, email, password) != null) {
		  console.println("added user $email")
			} else {
		  console.println("Error: user $email not added")
			}
    
  }
}	
	
inner class UserConsole {     // Console for logged in user. 
		
	@Command(description = "Exit: Logout current user")  //works with built in exit function 
  fun exit() {
 		println( "Logged out ")
		consoleUser.id = null
	}	

  @Command(description = "Add activity: create and add an activity for the logged in user")
  fun addActivity(@Param(name = "type") type:String,
                  @Param(name = "location") location:String, @Param(name = "distance") distance:Float) {
    console.renderActivity(paceApi.createActivity(consoleUser.id!!, type, location, distance))
  }

		
  @Command(description = "List Activities: List all activities for logged in user")
  fun listActivities() {
    console.renderActivities(paceApi.getActivities(consoleUser.id!!))
  }
	
  @Command(description = "Add location: Append location to an activity")
  fun addLocation(@Param(name = "activity-id") id:String,
                  @Param(name = "longitude") longitude:Double, @Param(name = "latitude") latitude:Double) {
      val activity = paceApi.getActivity(consoleUser.id!!, id)
      if (activity !=  null)
      {
        if(paceApi.addLocation(consoleUser.id!!, activity.id, latitude, longitude)) {
          console.println("Location $latitude, $longitude added")
        } else {
          console.println("Error: Could not add location")
        }
      }
      else
      {
        console.println("Error: Activity not found")
      }
  }
	
  @Command(description = "Activity Report: List all activities for logged in user, sorted alphabetically by type")
  fun activityReport() {
	  	console.println("Activity report sorted by type: ")
      console.renderActivities(paceApi.getActivities(consoleUser.id!!)?.sortedWith(compareBy({ it.type }, { it.location } )))
  }
	
  @Command(description = "Activity Report: List all activities for logged in user by type. Sorted longest to shortest distance")
  fun activityReport(@Param(name = "byType: type") type:String) {
	  	console.println("Activity report for type '" + type + "':")
      console.renderActivities(paceApi.getActivities(consoleUser.id!!, type)?.sortedWith(compareBy({ it.distance })))
  }
	
 @Command(description = "List all locations for a specific activity")
 fun listActivityLocations(@Param(name = "activity-id") id:String) {
     val activity = paceApi.getActivity(consoleUser.id!!, id)
     if (activity != null)
       {
		   console.renderActivity(activity);
       console.renderLocations(activity.route);
       } else {
		   console.println("Activity not found")
		   }
}
	
  @Command(description = "Follow Friend: Follow a specific friend")
  fun follow(@Param(name = "email") email:String) {
      if (paceApi.createFriend(consoleUser.id!!, email)) { 
        console.println("Now following " + email + ", you now have " + paceApi.getFriends(consoleUser.id!!)?.count() + " friend(s)")
	    } else {
		    console.println("Can't follow " + email + " (hint: check the email address is a valid user)")
			} 
  }	  

	
  @Command(description = "List Friends: List all of the friends of the logged in user")
  fun listFriends() {
        val friends = paceApi.getFriends(consoleUser.id!!)
	      if (friends != null && friends.isNotEmpty()) {
	        console.println("You have " + friends.count() + " friend(s):")
	        }
	      console.renderFriendUsers(friends)
  }

  @Command(description = "Unfollow Friends: Stop following a friend")
  fun unfollowFriend(@Param(name = "email") email:String) {
      if(paceApi.deleteFriend(consoleUser.id!!, email)) {
	  		console.println("No longer following " + email)
			} else {
	  		console.println("Error: Not a friend of " + email)
			}
		}
	
  @Command(description = "Friend Activity Report: List all activities of specific friend, sorted alphabetically by type)")
  fun friendActivityReport(@Param(name = "email") email:String) {
      console.renderActivities(paceApi.getFriendActivities(consoleUser.id!!, email)?.sortedWith(compareBy({ it.type }, { it.location })))
		}

  @Command(description = "Message Friend: send a message to a friend")
  fun messageFriend(@Param(name = "email") email:String,
                    @Param(name = "message") message:String) {
      if(paceApi.sendMessage(consoleUser.id!!, email, message)){
          console.println("Message sent")
			} else {
  			  console.println("Message not sent (is $email a friend ?)")
			}

  }	  

  @Command(description = "List Messages: List all messages for the logged in user")
  fun listMessages() {		  
        console.renderMessages(paceApi.getMessages(consoleUser.id!!))
	}
	
  @Command(description = "Message All Friends: send a message to all friends")
  fun messageAllFriends(@Param(name = "message") message:String) {
      if(paceApi.broadcastMessage(consoleUser.id!!, message)) {
        console.println("Message sent")
			} else {
			  console.println("Message not sent (have you got friends ?)")
			}
		}
	
	@Command(description = "Distance Leader Board: list summary distances of all friends, sorted longest to shortest")
  fun distanceLeaderBoard() {
		  console.println("Summary distances of all friends, sorted longest to shortest:")
      console.renderLeaders(paceApi.getLeaderBoard(consoleUser.id!!)?.sortedWith(compareBy({ -it.distance })))
  }

  @Command(description = "Distance Leader Board: distance leader board refined by type")
  fun distanceLeaderBoardByType(@Param(name = "byType: type") type:String) {
		  console.println("Summary distances of all friends for type '" + type + "':")
      console.renderLeaders(paceApi.getLeaderBoard(consoleUser.id!!, type)?.sortedWith(compareBy({ -it.distance })))
		}

  @Command(description = "Location Leader Board: list sorted summary distances of all friends in named location")
  fun locationLeaderBoard(@Param(name = "location") locale:String) {
		  console.println("Summary distances of all friends for location '" + locale + "':")
      console.renderLeaders(paceApi.getLeaderBoard(consoleUser.id!!, locale = locale)?.sortedWith(compareBy({ -it.distance })))
  }
 }
}
	
