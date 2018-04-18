package tdz

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters
import com.beust.jcommander.ParameterException

import com.jcabi.manifests.Manifests

private class GlobalOptions {
  @Parameter(names = arrayOf("-h", "--help"),
             help = true,
             description = "Print this help text")
  var help: Boolean = false

  @Parameter(names = arrayOf("-v", "--verbose"),
             description = "Enable verbose output")
  var verbose: Boolean = false

  @Parameter(names = arrayOf("-V", "--version"),
             description = "Print the version number")
  var printVersion: Boolean = false
}

open class TdzCommand {
  @Parameter(names = arrayOf("-h", "--help"),
             help = true,
             hidden = true,
             description = "Print this help text")
  var help: Boolean = false
}

fun handleCommandSpecificHelp(jc: JCommander, name: String, c: TdzCommand) {
  if (c.help) {
    jc.usage(name)
    System.exit(0)
  }
}

@Parameters(commandDescription = "Display tasks currently due")
private class CommandDue : TdzCommand() {}

@Parameters(commandDescription = "Create a new task")
private class CommandTask : TdzCommand() {}

fun main(args: Array<String>) {
  val globalOpts = GlobalOptions()
  val task = CommandTask()
  val due = CommandDue()

  val jc = JCommander.newBuilder()
                     .programName("tdz")
                     .addObject(globalOpts)
                     .addCommand("due", due)
                     .addCommand("task", task)
                     .build()

  try {
    jc.parse(*args)
  } catch (e: ParameterException) {
    println("${e.message}\n\nFor usage instructions, see --help.")
    System.exit(1)
  }

  if (globalOpts.help) {
    jc.usage()
    return
  }

  if (globalOpts.printVersion) {
    printVersion()
    return
  }

  when (jc.getParsedCommand() ?: "due") {
    "due" -> {
      handleCommandSpecificHelp(jc, "due", due)
      tasksDue()
    }

    "task" -> {
      handleCommandSpecificHelp(jc, "task", task)
      addTask()
    }
  }

  // System.exit(0)
}

private fun printVersion() {
  val version = if (Manifests.exists("tdz-version")) {
    Manifests.read("tdz-version")
  } else {
    "(unknown / development version)"
  }

  println("tdz ${version}")
}
