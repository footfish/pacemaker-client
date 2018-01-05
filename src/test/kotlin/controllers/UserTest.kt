package controllers

import org.junit.Assert.*
import java.io.File
import org.junit.After
import org.junit.Before
import org.junit.Test
import models.User
import models.Fixtures.users

class UserTest {
  internal var pacemaker = PacemakerAPI() //uses default url
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
  fun testCreateUser() {
    val user = pacemaker.createUser(homer.firstname, homer.lastname, homer.email, homer.password)
    val user2 = pacemaker.getUserByEmail(homer.email)
    val user3 = pacemaker.getUser(user?.id!!)
		assertEquals(user2, user)
		assertEquals(user3, user)		
  }
	
	@Test
  fun testDeleteUser() {
    val user = pacemaker.createUser(homer.firstname, homer.lastname, homer.email, homer.password)
		pacemaker.deleteUser(user?.id!!)
		assertNull(pacemaker.getUser(user.id))
	}
  

	@Test
  fun testCreateUsers() {
    users.forEach(
      { user-> pacemaker.createUser(user.firstname, user.lastname, user.email, user.password) })
    val returnedUsers = pacemaker.getUsers()
    assertEquals(users.size, returnedUsers!!.size)
  }
	
	
	
}