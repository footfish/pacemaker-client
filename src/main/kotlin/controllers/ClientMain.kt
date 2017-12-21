package controllers

import asg.cliche.Shell;
import asg.cliche.ShellFactory;
import java.io.IOException

object ClientMain {
		val console = PacemakerConsoleService()
    @Throws(IOException::class)
    @JvmStatic fun main(args:Array<String>) {
      ShellFactory.createConsoleShell( "$", "Welcome to pacemaker-console - ?help for instructions", console).commandLoop()
    } 
}
