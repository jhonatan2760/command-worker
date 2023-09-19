package br.com.jhonatansouza

import br.com.jhonatansouza.enums.JobPosition
import br.com.jhonatansouza.exceptions.IndexNotFoundException
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions

class CommandWorkerQueueTest {


    @Test
    fun executeCommandAndResultProcessResult() {
        val namesCommand = NamesCommand()
        val commadWorker = CommandWorkerQueue().initialize()

        val command = commadWorker.withCommand(namesCommand)
            .withParam(Names("Command Worker Test 1", 3))
            .withCommand(NamesCommand())
            .withParam(Names("Command Worker Test 2", 9))
            .execute()

        Assert.assertEquals(
            "Command Worker Test 1",
            command.then().getResult(JobPosition.FIRST, Names::class.java).name
        )
    }

    @Test
    fun assertThrowsIndexNotFoundException() {
        val namesCommand = NamesCommand()
        val commadWorker = CommandWorkerQueue().initialize()

        val command = commadWorker.withCommand(namesCommand)
            .withParam(Names("Command Worker Test 1", 3))
            .withCommand(NamesCommand())
            .withParam(Names("Command Worker Test 2", 9))
            .execute()

        assertThrows(IndexNotFoundException::class.java) {
            command.then().getResult(JobPosition.THIRD, Names::class.java)
        }

    }

    @Test
    fun assertSearchOfResultOfNotResultingIndexShouldThrowsIndexNotFoundException() {
        val namesCommand = NamesCommand()
        val commadWorker = CommandWorkerQueue().initialize()

        val command = commadWorker.withCommand(namesCommand)
            .withParam(Names("Command Worker Test 1", 3))
            .withCommand(NamesCommand())
            .withParam(Names("Command Worker Test 2", 9))
            .execute()

    }

    @Test
    fun assertCommandWorkerWillBeExecuted() {
        val namesCommand = NamesCommand()
        val commadWorker = CommandWorkerQueue().initialize()

        Assert.assertNotEquals(commadWorker.withCommand(namesCommand)
            .withParam(Names("Command Worker Test 1", 3))
            .withCommand(NamesCommand())
            .withParam(Names("Command Worker Test 2", 9))
            .execute(), namesCommand)


    }

    @Test
    fun assertThatAfterTearDownTheResultListShouldBeEmpty() {
        val namesCommand = NamesCommand()
        val commadWorker = CommandWorkerQueue().initialize()

        val command = commadWorker.withCommand(namesCommand)
            .withParam(Names("Command Worker Test 1", 3))
            .withCommand(NamesCommand())
            .withParam(Names("Command Worker Test 2", 9))
            .execute()

        command.then().tearDown();
        assertEquals(command.then().getResult().isEmpty(), true)
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
