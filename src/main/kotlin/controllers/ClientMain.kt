package controllers

import asg.cliche.*
import asg.cliche.Shell
import asg.cliche.ShellFactory
import asg.cliche.ShellDependent
import java.io.IOException

		val console = PacemakerConsoleService("http://localhost:7000")
		
    @Throws(IOException::class)
     fun main(args:Array<String>) {
      ShellFactory.createConsoleShell( "$", "Welcome to pacemaker-console - ?help for instructions", console).commandLoop()
    } 
