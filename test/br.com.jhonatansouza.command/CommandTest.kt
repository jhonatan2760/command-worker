package br.com.jhonatansouza.command

import br.com.jhonatansouza.command.enum.JobPosition
import br.com.jhonatansouza.command.exceptions.IndexNotFoundException
import org.junit.Assert
import org.junit.Assert.assertThrows
import org.junit.Test

class CommandTest {


    @Test
    fun executeCommandAndResultProcessResult() {
        val namesCommand = NamesCommand()
        val commadWorker = CommandWorkerQueue().initialize()

        val command = commadWorker.withCommand(namesCommand)
            .withParam(Names("Command Worker Test 1", 3))
            .then().withCommand(NamesCommand())
            .withParam(Names("Command Worker Test 2", 9))
            .then()
            .execute()

        Assert.assertEquals("Command Worker Test 1", command.then().getResult(JobPosition.FIRST, Names::class.java).name )
    }

    @Test
    fun assertThrowsIndexNotFoundException() {
        val namesCommand = NamesCommand()
        val commadWorker = CommandWorkerQueue().initialize()

        val command = commadWorker.withCommand(namesCommand)
            .withParam(Names("Command Worker Test 1", 3))
            .then().withCommand(NamesCommand())
            .withParam(Names("Command Worker Test 2", 9))
            .then()
            .execute()

        assertThrows(IndexNotFoundException::class.java) {
            command.then().getResult(JobPosition.THIRD)
        }

    }


}

class NamesCommand() : Command<Names> {

    override fun execute(t: Names): CommandResult<Names> {
        return CommandResult(
            result = t,
            isExecuted = true
        )
    }

}


data class Names(val name: String, val age: Int)
