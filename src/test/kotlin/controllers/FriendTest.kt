package controllers

import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.*
import models.Activity
import models.User
import models.Leader
import models.Fixtures.users
import models.Fixtures.activities
import models.Fixtures.margeActivities
import models.Fixtures.lisasActivities

class FriendTest {
	internal var pacemaker = PacemakerAPI() //uses default url
	internal var badPacemaker = PacemakerAPI("http://localhost/")  //bad url - used in exception testing
  internal var homer:User = User()
  internal var ned:User = User()

	@Before
  fun setup() {
    pacemaker.deleteUsers()
    homer = pacemaker.createUser("homer", "simpson", "homer@simpson.com", "secret")!!
		ned = pacemaker.createUser("ned", "flanders", "ned@flanders.com", "secret")!!
  }
	
  @After
  fun tearDown() {}
  
	@Test
  fun testCreateFriend() {
		assertTrue {pacemaker.createFriend(homer.id, ned.email)}
		homer = pacemaker.getUser(homer.id)!!
		assertTrue {(homer.friend.contains(ned.id))} 

		assertFalse {pacemaker.createFriend("X", "X")}  // create invalid friend
		assertFalse {badPacemaker.createFriend("X", "X")}  // create friend server unavailable 
	}
	
	@Test
  fun testDeleteFriend() {
		pacemaker.createFriend(homer.id, ned.email)
		homer = pacemaker.getUser(homer.id)!!
		assertTrue {(homer.friend.contains(ned.id))} 
  	assertTrue {pacemaker.deleteFriend(homer.id, ned.email)}
		homer = pacemaker.getUser(homer.id)!!
		assertFalse {(homer.friend.contains(ned.id))} 

  	assertFalse {pacemaker.deleteFriend("X", "X")}  // delete invalid friend
  	assertFalse {badPacemaker.deleteFriend("X", "X")}  // delete friend server unavailable
	}
	
	@Test
  fun testGetFriends() {
		for (user in users ) {
			pacemaker.createUser(user.firstname,user.lastname,user.email, user.password)
			pacemaker.createFriend(ned.id, user.email)
		}
		assertEquals (pacemaker.getFriends(ned.id)!!.size, users.size)

		ned = pacemaker.getUser(ned.id)!!
		assertEquals (pacemaker.getFriends(ned.id)!!.size, ned.friend.size)
		for (user in pacemaker.getFriends(ned.id)!!) {
				assertTrue {(user.friend.contains(ned.id))}	
		}
		
		assertNull(pacemaker.getFriends("X"))  //get friends for invalid user 
		assertNull(badPacemaker.getFriends("X")) //get friends for server unavailable 
	}
	
	@Test
	fun testSendMessage() {
    assertFalse {pacemaker.sendMessage(homer.id, ned.email,"sent message")} //send to valid user, not friend
		 
		pacemaker.createFriend(homer.id, ned.email)
    assertTrue {pacemaker.sendMessage(homer.id, ned.email,"sent message")} //send to friend
		ned = pacemaker.getUser(ned.id)!!
		assertEquals(pacemaker.getMessages(ned.id)!!.first().message, "sent message")
		
		assertFalse {pacemaker.sendMessage("X", "X", "X")} //send to invalid user
		assertFalse {badPacemaker.sendMessage("X", "X", "X")} //send to server unavailable 
	}

	@Test
	fun testgetMessages() {
		for (user in users ) {
			var newUser = pacemaker.createUser(user.firstname,user.lastname,user.email, user.password)
			pacemaker.createFriend(ned.id, user.email)
			pacemaker.sendMessage(newUser?.id!!, ned.email,user.firstname)
		}
		
		assertEquals(pacemaker.getMessages(ned.id)!!.size, users.size)
		
		assertTrue {pacemaker.getMessages("X")!!.isEmpty()} //get for invalid user
		assertNull (badPacemaker.getMessages("X")) //get from server unavailable 

	}
	
	@Test
	fun testBroadcastMessage() {
		for (user in users ) {
			pacemaker.createUser(user.firstname,user.lastname,user.email, user.password)
			pacemaker.createFriend(ned.id, user.email)
		}
    assertTrue {pacemaker.broadcastMessage(ned.id, "broadcast message")} //broadcast to all friends (except homer)

		ned = pacemaker.getUser(ned.id)!!

		for (user in pacemaker.getUsers()!! ) {
		  if(user.email != "ned@flanders.com" && user.email!="homer@simpson.com") {
		    assertEquals(pacemaker.getMessages(user.id)!!.first().message, "broadcast message")
		  } else {
			  assertTrue {pacemaker.getMessages(user.id)!!.isEmpty()}
				}
		}
		
		assertFalse {pacemaker.broadcastMessage("X", "X")} //broadcast from unknown user 
		assertFalse {badPacemaker.broadcastMessage("X", "X")} //broadcast server unavailable  
	}

	@Test
	fun testGetFriendActivities() {
		pacemaker.createFriend(homer.id, ned.email)
		for (activity in activities) {
		  pacemaker.createActivity (ned.id, activity.type, activity.location, activity.distance)  
		}
  val returnedActivities = pacemaker.getFriendActivities(homer.id, ned.email)
  assertEquals(activities.size, returnedActivities!!.size)

	assertTrue {pacemaker.getFriendActivities(ned.id, homer.email)!!.isEmpty()} 	
	assertNull(pacemaker.getFriendActivities("X", "X")) 
	assertNull(badPacemaker.getFriendActivities("X", "X")) 
	}
		
	@Test
	fun testGetLeaderBoard() {
		for (friend in users ) { //add friends for ned 
			pacemaker.createUser(friend.firstname,friend.lastname,friend.email, friend.password)
			pacemaker.createFriend(ned.id, friend.email)
		}
		
		var marge = pacemaker.getUserByEmail("marge@simpson.com") //add activities for marge
		var margeDistance: Double = 0.0
		var margeDistanceWalk: Double = 0.0
		var margeDistanceSchool: Double = 0.0
		for (activity in margeActivities) {
		  pacemaker.createActivity(marge?.id!!, activity.type, activity.location, activity.distance)
			margeDistance += activity.distance
			if (activity.type == "walk") margeDistanceWalk += activity.distance
			if (activity.location == "school") margeDistanceSchool += activity.distance
		}

		var lisa = pacemaker.getUserByEmail("lisa@simpson.com") //add activities for lisa
		var lisaDistance: Double = 0.0
		var lisaDistanceWalk: Double = 0.0
		var lisaDistanceSchool: Double = 0.0		
		for (activity in lisasActivities) {
		  pacemaker.createActivity(lisa?.id!!, activity.type, activity.location, activity.distance)
			lisaDistance += activity.distance
			if (activity.type == "walk") lisaDistanceWalk += activity.distance
			if (activity.location == "school") lisaDistanceSchool += activity.distance
		}

		var bart = pacemaker.getUserByEmail("bart@simpson.com") //add activities for bart
		var bartDistance: Double = 0.0
		var bartDistanceWalk: Double = 0.0
		var bartDistanceSchool: Double = 0.0
		for (activity in activities) {
		  pacemaker.createActivity(bart?.id!!, activity.type, activity.location, activity.distance)
		  bartDistance += activity.distance
			if (activity.type == "walk") bartDistanceWalk += activity.distance
			if (activity.location == "school") bartDistanceSchool += activity.distance
		}
		
		var nedsLeaderBoard = pacemaker.getLeaderBoard(ned.id)
		assertEquals(nedsLeaderBoard?.size, users.size) //leaderboard size should equal number of friends
		for (leader in nedsLeaderBoard!!) {
			when (leader.id) {
				bart?.id -> assertEquals(leader.distance,bartDistance)
				lisa?.id -> assertEquals(leader.distance,lisaDistance)
				marge?.id -> assertEquals(leader.distance,margeDistance)
			}
		} 

		nedsLeaderBoard = pacemaker.getLeaderBoard(ned.id, type="walk")
		assertEquals(nedsLeaderBoard?.size, users.size) //leaderboard size should equal number of friends
		for (leader in nedsLeaderBoard!!) {
			when (leader.id) {
				bart?.id -> assertEquals(leader.distance,bartDistanceWalk)
				lisa?.id -> assertEquals(leader.distance,lisaDistanceWalk)
				marge?.id -> assertEquals(leader.distance,margeDistanceWalk)
			}
		} 
		
		nedsLeaderBoard = pacemaker.getLeaderBoard(ned.id, locale="school")
		assertEquals(nedsLeaderBoard?.size, users.size) //leaderboard size should equal number of friends
		for (leader in nedsLeaderBoard!!) {
			when (leader.id) {
				bart?.id -> assertEquals(leader.distance,bartDistanceSchool)
				lisa?.id -> assertEquals(leader.distance,lisaDistanceSchool)
				marge?.id -> assertEquals(leader.distance,margeDistanceSchool)
			}
		}
		
		nedsLeaderBoard = pacemaker.getLeaderBoard(ned.id, locale="school", type="walk")
		assertEquals(nedsLeaderBoard?.size, users.size) //leaderboard size should equal number of friends
		for (leader in nedsLeaderBoard!!) {
			when (leader.id) {
				bart?.id -> assertEquals(leader.distance,0.0)
				lisa?.id -> assertEquals(leader.distance,0.0)
				marge?.id -> assertEquals(leader.distance,0.0)
			}
		} 

		assertTrue {pacemaker.getLeaderBoard("X", "X")!!.isEmpty()} //NTS: better if result was null
		assertTrue {badPacemaker.getLeaderBoard("X", "X")!!.isEmpty()}  //NTS: better if result was null 
	}

			
}