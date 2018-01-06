package controllers

import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.*
import models.Activity
import models.User
import models.Fixtures.activities
import models.Fixtures.locations

class ActivityTest {
  internal var pacemaker = PacemakerAPI() //uses default url
	internal var badPacemaker = PacemakerAPI("http://localhost/") //bad url - used in exception testing
  internal var homer = User("homer", "simpson", "homer@simpson.com", "secret")
  
	@Before
  fun setup() {
    pacemaker.deleteUsers()
    homer = pacemaker.createUser(homer.firstname, homer.lastname, homer.email, homer.password)!!
  }
	
  @After
  fun tearDown() {}
  
	@Test
  fun testCreateActivity() {
    val activity = Activity("walk", "shop", 2.5f)
    val returnedActivity = pacemaker.createActivity(homer.id, activity.type, activity.location, activity.distance)
		assertEquals(activity.type, returnedActivity?.type)
		assertEquals(activity.location, returnedActivity?.location)
		assertEquals(activity.distance, returnedActivity?.distance)
    assertNotNull(returnedActivity?.id)
    assertNull(badPacemaker.createActivity("X", "X", "X", 0F))	
  }
	
	@Test
  fun testCreateActivities() {
  for (activity in activities) {
	   pacemaker.createActivity (homer.id, activity.type, activity.location, activity.distance)  
		}
  val returnedActivities = pacemaker.getActivities(homer.id)
  assertEquals(activities.size, returnedActivities!!.size)
	assertNull(badPacemaker.getActivities(homer.id))
  }
	

	@Test
  fun testGetActivity() {
    val activity = Activity("run", "fridge", 0.5f)
    val returnedActivity1 = pacemaker.createActivity(homer.id, activity.type, activity.location, activity.distance)
    val returnedActivity2 = pacemaker.getActivity(homer.id, returnedActivity1!!.id)
    assertEquals(returnedActivity1, returnedActivity2)
		assertNull(badPacemaker.getActivity("X", "X"))
  }
 
  @Test
  fun testDeleteActivities() {
    val activity = Activity("sprint", "pub", 4.5f)
    var returnedActivity = pacemaker.createActivity(homer.id, activity.type, activity.location, activity.distance)
    assertNotNull(returnedActivity)
    pacemaker.deleteActivities(homer.id)
    returnedActivity = pacemaker.getActivity(homer.id, returnedActivity!!.id)
    assertNull(returnedActivity)
	  assertEquals(pacemaker.deleteActivities ("X"),false) //test delete of non-existant user 
    assertEquals(badPacemaker.deleteActivities ("X"),false)
  }

	@Test
  fun testAddLocations() {
    val activity = Activity("walk", "shop", 2.5f)
    var returnedActivity = pacemaker.createActivity(homer.id, activity.type, activity.location, activity.distance)
		for (location in locations) {
			 pacemaker.addLocation(homer.id, returnedActivity?.id!!, location.latitude, location.longitude)
		}
		returnedActivity = pacemaker.getActivity(homer.id, returnedActivity?.id!!)
		assertEquals(returnedActivity?.route?.size, locations.size)
		for (i in 0..locations.size-1)
			  {
		    assertEquals(locations[i].longitude, returnedActivity?.route!![i].longitude)
		    assertEquals(locations[i].latitude, returnedActivity.route[i].latitude)
			  }
		
		assertFalse {pacemaker.addLocation("X", "X", 0.0, 0.0)}  //test adding bad location
		assertFalse {badPacemaker.addLocation("X", "X", 0.0, 0.0)}  //test adding location server unavailable
	}
		
}