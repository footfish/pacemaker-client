package controllers

import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.*
import models.User
import models.Fixtures.users

class UserTest {
  internal var pacemaker = PacemakerAPI() //uses default url
	internal var badPacemaker = PacemakerAPI("http://localhost/")  //bad url - used in exception testing
  internal var homer = User("homer", "simpson", "homer@simpson.com", "secret")

	@Before
  fun setup() {
    pacemaker.deleteUsers()
  }

	@After
  fun tearDown() {
    //pacemaker.deleteUsers()		
	}

	@Test
  fun testGetUser() {
    val user = pacemaker.createUser(homer.firstname, homer.lastname, homer.email, homer.password)
    val user2 = pacemaker.getUserByEmail(homer.email)
    val user3 = pacemaker.getUser(user?.id!!)
		assertEquals(user2, user)
		assertEquals(user3, user)
		assertNull(pacemaker.getUserByEmail("X")) //get invalid user email 		
		assertNull(badPacemaker.getUserByEmail("X")) //get invalid user id 
		assertNull(badPacemaker.getUser("X")) //get user server unavailable
		assertNull(badPacemaker.getUsers()) //get users server unavailable
	}
	
	@Test
  fun testCreateUser() {
    val user = pacemaker.createUser(homer.firstname, homer.lastname, homer.email, homer.password)
    val user2 = pacemaker.getUserByEmail(homer.email)
		assertEquals(user2, user)
		assertNull(badPacemaker.createUser("X", "X", "X", "X")) //create server unavailable 
  }
	
	@Test
  fun testDeleteUser() {
    val user = pacemaker.createUser(homer.firstname, homer.lastname, homer.email, homer.password)
		pacemaker.deleteUser(user?.id!!)
		assertNull(pacemaker.getUser(user.id))
		//NTS - should check friend delete
		assertNull(pacemaker.deleteUser("X")) //delete invalid user
		assertNull(badPacemaker.deleteUser("X")) //delete server unavailable 
	}
  
	@Test
  fun testDeleteUsers() {
    val user = pacemaker.createUser(homer.firstname, homer.lastname, homer.email, homer.password)
    val user2 = pacemaker.getUserByEmail(homer.email)
		assertEquals(user2, user)
		pacemaker.deleteUsers()
		assertNull(pacemaker.getUser(user?.id!!))
		assertFalse {badPacemaker.deleteUsers()} //delete server unavailable 
	}

	@Test
  fun testCreateUsers() {
    users.forEach(
      { user-> pacemaker.createUser(user.firstname, user.lastname, user.email, user.password) })
    val returnedUsers = pacemaker.getUsers()
    assertEquals(users.size, returnedUsers!!.size)
  }
	
	
	
}