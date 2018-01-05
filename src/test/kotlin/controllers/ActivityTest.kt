package controllers

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import models.Activity
import models.User

class ActivityTest {
  internal var pacemaker = PacemakerAPI() //uses default url 
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
  }
/*	
  @Test
  fun testGetActivity() {
    val activity = Activity("run", "fridge", 0.5)
    val returnedActivity1 = pacemaker.createActivity(homer.id, activity.type, activity.location, activity.distance)
    val returnedActivity2 = pacemaker.getActivity(homer.id, returnedActivity1.id)
    assertEquals(returnedActivity1, returnedActivity2)
  }
  @Test
  fun testDeleteActivity() {
    val activity = Activity("sprint", "pub", 4.5)
    val returnedActivity = pacemaker.createActivity(homer.id, activity.type, activity.location, activity.distance)
    assertNotNull(returnedActivity)
    pacemaker.deleteActivities(homer.id)
    returnedActivity = pacemaker.getActivity(homer.id, returnedActivity.id)
    assertNull(returnedActivity)
  }
 */
}