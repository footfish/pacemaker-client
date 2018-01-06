package controllers
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import models.Activity
import models.Location
import models.User
import models.Message
import models.Leader
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.net.URLEncoder

internal interface PacemakerInterface {
  @GET("/users")
  fun getUsers(@Query("id") id:String? = null,@Query("email") email:String? = null):Call<List<User>>
  @DELETE("/users")
  fun deleteUsers():Call<String>
  @DELETE("/users/{id}")
  fun deleteUser(@Path("id") id:String):Call<String>
  @GET("/users/{id}")
  fun getUser(@Path("id") id:String):Call<User>
  @PUT("/users/{id}")
  fun updateUser(@Path("id") id:String, @Body User:User):Call<User>
  @POST("/users")
  fun registerUser(@Body User:User):Call<User>
  @GET("/users/{id}/activities")
  fun getActivities(@Path("id") id:String, @Query("type") type:String? = null):Call<List<Activity>>
  @POST("/users/{id}/activities")
  fun addActivity(@Path("id") id:String, @Body activity:Activity):Call<Activity>
  @GET("/users/{id}/friends/")
  fun getFriends(@Path("id") id:String):Call<List<User>>
  @POST("/users/{id}/friends/{email}")
  fun createFriend(@Path("id") id:String, @Path("email")friendEmail:String):Call<String>
  @DELETE("/users/{id}/friends/{email}")
  fun deleteFriend(@Path("id") id:String, @Path("email")friendEmail:String):Call<String>
  @GET("/users/{id}/friends/{email}/activities")
  fun getFriendActivities(@Path("id") id:String, @Path("email")friendEmail:String):Call<List<Activity>>
  @DELETE("/users/{id}/activities")
  fun deleteActivities(@Path("id") id:String):Call<String>
  @GET("/users/{id}/activities/{activityId}")
  fun getActivity(@Path("id") id:String, @Path("activityId") activityId:String):Call<Activity>
  @POST("/users/{id}/activities/{activityId}/locations")
  fun addLocation(@Path("id") id:String, @Path("activityId") activityId:String,
                  @Body location:Location):Call<Location>
  @POST("/users/{id}/messages/{email}")
  fun sendMessage(@Path("id") id:String, @Path("email")friendEmail:String, @Body message:Message):Call<String>
  @GET("/users/{id}/messages")
  fun getMessages(@Path("id") id:String):Call<List<Message>>
  @POST("/users/{id}/messages/")
  fun broadcastMessage(@Path("id") id:String, @Body message:Message):Call<String>
}

class PacemakerAPI(url:String="http://localhost:7000") {
  internal var pacemakerInterface:PacemakerInterface
	
  init{
    val gson = GsonBuilder().create()
    val retrofit = Retrofit.Builder().baseUrl(url)
    .addConverterFactory(GsonConverterFactory.create(gson)).build()
    pacemakerInterface = retrofit.create(PacemakerInterface::class.java)
  }
	
	fun getUsers():Collection<User>? {
  var users:Collection<User>? = null
  try
  {
    val call = pacemakerInterface.getUsers()
    val response = call.execute()
    users = response.body()
  }
  catch (e:Exception) {
    println("Oops something bad happened, this message may help -> " + e.message)
  }
  return users
}

  fun updateUser(id: String, firstName:String, lastName:String, email:String, password:String, disabled:Boolean, admin:Boolean):User? {
    var returnedUser:User? = null
    try
    {
      val call = pacemakerInterface.updateUser(id, User(firstname=firstName, lastname=lastName, email=email, password=password,disabled=disabled,admin=admin))
      val response = call.execute()
      returnedUser = response.body()
    }
    catch (e:Exception) {
      println("Oops something bad happened, this message may help -> " + e.message)
    }
    return returnedUser
  }
	
		
  fun createUser(firstName:String, lastName:String, email:String, password:String):User? {
    var returnedUser:User? = null
    try
    {
      val call = pacemakerInterface.registerUser(User(firstName, lastName, email, password))
      val response = call.execute()
      returnedUser = response.body()
    }
    catch (e:Exception) {
      println("Oops something bad happened, this message may help -> " + e.message)
    }
    return returnedUser
  }
	
  fun createActivity(id:String, type:String, location:String, distance:Float):Activity? {
    var returnedActivity:Activity? = null
    try
    {
      val call = pacemakerInterface.addActivity(id, Activity(type, location, distance))
      val response = call.execute()
      returnedActivity = response.body()
    }
    catch (e:Exception) {
      println("Oops something bad happened, this message may help -> " + e.message)
    }
    return returnedActivity
  }
	
	
	fun getFriends(id:String):Collection<User>? {
	  var users:Collection<User>? = null
	      try
        {
	        val call = pacemakerInterface.getFriends(id)
	        val response = call.execute()
	        users = response.body()
        }
        catch (e:Exception) {
        println("Oops something bad happened, this message may help -> " + e.message)
        }
    return users
}
	
	fun createFriend(id:String, email:String): Boolean {
    try
    {
      val call = pacemakerInterface.createFriend(id, URLEncoder.encode(email, "UTF-8"))
      if (call.execute().code() == 204)
		    return true 
    }
    catch (e:Exception) {
      println("Oops something bad happened, this message may help -> " + e.message)
    }
	  return false 
  }

	fun deleteFriend(id:String, email:String):Boolean {
    try
    {
      val call = pacemakerInterface.deleteFriend(id, URLEncoder.encode(email, "UTF-8"))
      if(call.execute().code() == 204) {
		  return true
			}
    }
    catch (e:Exception) {
      println("Oops something bad happened, this message may help -> " + e.message)
    }
		return false
  }
	
  fun getFriendActivities(id:String, email: String):Collection<Activity>? {
    var activities:Collection<Activity>? = null
    try
    {
      val call = pacemakerInterface.getFriendActivities(id, URLEncoder.encode(email, "UTF-8"))
      val response = call.execute()
      activities = response.body()
    }
    catch (e:Exception) {
      println("Oops something bad happened, this message may help -> " + e.message)
    }
    return activities
  }

  fun getActivities(id:String, type: String? = null):Collection<Activity>? {
    var activities:Collection<Activity>? = null
    try
    {
      val call = pacemakerInterface.getActivities(id,type)
      val response = call.execute()
      activities = response.body()
    }
    catch (e:Exception) {
      println("Oops something bad happened, this message may help -> " + e.message)
    }
    return activities
  }
	
  fun getActivity(userId:String, activityId:String):Activity? {
    var activity:Activity? = null
    try
    {
      val call = pacemakerInterface.getActivity(userId, activityId)
      val response = call.execute()
      activity = response.body()
    }
    catch (e:Exception) {
      println("Oops something bad happened, this message may help -> " + e.message)
    }
    return activity
  }
	
  fun deleteActivities(id:String):Boolean {
    try
    {
      val call = pacemakerInterface.deleteActivities(id)
      if (call.execute().code() == 204) {
		  return true 
			}
    }
    catch (e:Exception) {
      println("Oops something bad happened, this message may help -> " + e.message)
    }
	  return false 
  }
	
  fun addLocation(id:String, activityId:String, latitude:Double, longitude:Double): Boolean {
    try
    {
      val call = pacemakerInterface.addLocation(id, activityId, Location(latitude, longitude))
      if (call.execute().code() == 200) {
        return true
			  }
		  
    }
    catch (e:Exception) {
      println("Oops something bad happened, this message may help -> " + e.message)
    }
	  return false 
  }

  fun getUserByEmail(email:String):User? {
    var user:User? = null
	    try {
			  val call = pacemakerInterface.getUsers(email = URLEncoder.encode(email,"UTF-8"))
			  val response = call.execute()
			  val userList = response.body()
			  if ( userList != null && userList.isNotEmpty()) {
			    	      user = userList.first()
					} 
	    }
	      catch (e:Exception) {
	        println("Oops something bad happened, this message may help -> " + e.message)
	      }
    return user
   }
	
  fun getUser(id:String):User? {
    var user:User? = null
    try  {
		  val call = pacemakerInterface.getUsers(id = id)
		  val response = call.execute()
		  val userList = response.body()
			  if ( userList != null && userList.isNotEmpty()) {
			    	      user = userList.first()
					} 
    }
    catch (e:Exception) {
      println("Oops something bad happened, this message may help -> " + e.message)
    }
    return user
  }
	
  fun deleteUsers():Boolean {
    try
    {
      val call = pacemakerInterface.deleteUsers()
      if (call.execute().code() == 204) {
        return true
			}
    }
    catch (e:Exception) {
      println("Oops something bad happened, this message may help -> " + e.message)
    }
	  return false 
  }
	
  fun deleteUser(id:String):Boolean {
    try
    {
      val call = pacemakerInterface.deleteUser(id)
      if (call.execute().code() == 204) {
        return true
	 	  }
    }
    catch (e:Exception) {
      println("Oops something bad happened, this message may help -> " + e.message)
    }
    return false
  }
	
	fun sendMessage(id:String, email:String, message: String): Boolean{
    try
    {
     val call = pacemakerInterface.sendMessage(id, URLEncoder.encode(email, "UTF-8"), Message(message, id))
     if (call.execute().code() == 204) {
       return true
		 }
    }
    catch (e:Exception) {
      println("Oops something bad happened, this message may help -> " + e.message)
    }
		return false 
	}
	
	fun getMessages(id:String):Collection<Message>? {
    var messages:Collection<Message>? = null
    try
    {
     val call = pacemakerInterface.getMessages(id)
     val response = call.execute()
     messages = response.body()
    }
    catch (e:Exception) {
      println("Oops something bad happened, this message may help -> " + e.message)
    }
		return messages
	}
	
	fun broadcastMessage(id:String, message: String): Boolean {
    try
    {
     val call = pacemakerInterface.broadcastMessage(id, Message(message, id))
     if(call.execute().code() == 204){
       return true 
     }
    }
    catch (e:Exception) {
      println("Oops something bad happened, this message may help -> " + e.message)
    }
		return false
	}
	
	fun getLeaderBoard(id:String, type: String?=null, locale: String?=null):Collection<Leader>? {
		var leaders:MutableList<Leader>? = ArrayList()
		val friendlist = getFriends(id)
		  if (friendlist != null) {
		    for (friend in friendlist){
		      val activitieslist = getFriendActivities(id, friend.email) //NTS: Improve by extending REST call to support query string filter for 'type' and 'location'. 
		          if (activitieslist != null){
		             if (type == null && locale == null) {
	                 leaders?.add(Leader(friend.id, friend.firstname, friend.lastname, friend.email, activitieslist.sumByDouble { it.distance.toDouble() }))
		             }
		             else if (type != null && locale == null) {
   					       leaders?.add(Leader(friend.id, friend.firstname, friend.lastname, friend.email, activitieslist.sumByDouble { if(it.type == type) it.distance.toDouble() else 0.0 }))
                 }
		             else if (type == null && locale != null) {
		               leaders?.add(Leader(friend.id, friend.firstname, friend.lastname, friend.email, activitieslist.sumByDouble { if(it.location == locale) it.distance.toDouble() else 0.0 }))
                 }
		             else if (type != null && locale != null) {
		               leaders?.add(Leader(friend.id, friend.firstname, friend.lastname, friend.email, activitieslist.sumByDouble { if(it.location == locale && it.type == type) it.distance.toDouble() else 0.0 }))
                 }
				      }
		    }
	   }
	 return leaders
   }

	
}