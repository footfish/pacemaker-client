package controllers
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import models.Activity
import models.Location
import models.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface PacemakerInterface {
  @GET("/users")
  fun getUsers():Call<List<User>>
  @DELETE("/users")
  fun deleteUsers():Call<String>
  @DELETE("/users/{id}")
  fun deleteUser(@Path("id") id:String):Call<User>
  @GET("/users/{id}")
  fun getUser(@Path("id") id:String):Call<User>
  @POST("/users")
  fun registerUser(@Body User:User):Call<User>
  @GET("/users/{id}/activities")
  fun getActivities(@Path("id") id:String):Call<List<Activity>>
  @GET("/users/{id}/activities/{type}")
  fun getActivitiesType(@Path("id") id:String, @Path("type") type:String):Call<List<Activity>>
  @POST("/users/{id}/activities")
  fun addActivity(@Path("id") id:String, @Body activity:Activity):Call<Activity>
  @GET("/users/{id}/friends/")
  fun getFriends(@Path("id") id:String):Call<List<User>>
  @POST("/users/{id}/friends/{email}")
  fun createFriend(@Path("id") id:String, @Path("email")friendEmail:String):Call<String>
  @DELETE("/users/{id}/activities")
  fun deleteActivities(@Path("id") id:String):Call<String>
  @GET("/users/{id}/activities/{activityId}")
  fun getActivity(@Path("id") id:String, @Path("activityId") activityId:String):Call<Activity>
  @POST("/users/{id}/activities/{activityId}/locations")
  fun addLocation(@Path("id") id:String, @Path("activityId") activityId:String,
                  @Body location:Location):Call<Location>
}

class PacemakerAPI(url:String) {
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
    println(e.message)
  }
  return users
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
      println(e.message)
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
      println(e.message)
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
        println(e.message)
        }
    return users
}
	
	fun createFriend(id:String, email:String) {
    try
    {
      val call = pacemakerInterface.createFriend(id, email)
      call.execute()
    }
    catch (e:Exception) {
      println(e.message)
    }
  }
	
  fun getActivities(id:String):Collection<Activity>? {
    var activities:Collection<Activity>? = null
    try
    {
      val call = pacemakerInterface.getActivities(id)
      val response = call.execute()
      activities = response.body()
    }
    catch (e:Exception) {
      println(e.message)
    }
    return activities
  }
	
	  fun getActivitiesType(id:String, type: String):Collection<Activity>? {
    var activities:Collection<Activity>? = null
    try
    {
      val call = pacemakerInterface.getActivitiesType(id,type)
      val response = call.execute()
      activities = response.body()
    }
    catch (e:Exception) {
      println(e.message)
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
      println(e.message)
    }
    return activity
  }
	
  fun deleteActivities(id:String) {
    try
    {
      val call = pacemakerInterface.deleteActivities(id)
      call.execute()
    }
    catch (e:Exception) {
      println(e.message)
    }
  }
	
  fun addLocation(id:String, activityId:String, latitude:Double, longitude:Double) {
    try
    {
      val call = pacemakerInterface.addLocation(id, activityId, Location(latitude, longitude))
      call.execute()
    }
    catch (e:Exception) {
      println(e.message)
    }
  }

	  fun getUserByEmail(email:String):User? {
    var users = getUsers()
    var foundUser:User? = null

  		  
    for (user in users.orEmpty())
    {
      if (user.email.equals(email))
      {
        foundUser = user
      }
    }
    return foundUser
  }
	
  fun getUser(id:String):User? {
    var user:User? = null
    try
    {
      val call = pacemakerInterface.getUser(id)
      val response = call.execute()
      user = response.body()
    }
    catch (e:Exception) {
      println(e.message)
    }
    return user
  }
  fun deleteUsers() {
    try
    {
      val call = pacemakerInterface.deleteUsers()
      call.execute()
    }
    catch (e:Exception) {
      println(e.message)
    }
  }
	
  fun deleteUser(id:String):User? {
    var user:User? = null
    try
    {
      val call = pacemakerInterface.deleteUser(id)
      val response = call.execute()
      user = response.body()
    }
    catch (e:Exception) {
      println(e.message)
    }
    return user
  }
}