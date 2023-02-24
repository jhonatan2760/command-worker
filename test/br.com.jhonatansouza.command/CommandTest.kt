package br.com.jhonatansouza.command

import junit.framework.TestCase.assertEquals
import org.junit.Test

class CommandTest {

    @Test
    fun executeCommandAndGetResult() {

        val sumCommand = SumCommand()
        val jobs = CommandWorkerQueue()
        jobs.initialize()
            .withCommand(sumCommand, 24L)
            .execute()

        assertEquals(48L, jobs.getJobResult(sumCommand))
    }

}

class SumCommand : Command<Long> {
    override fun execute(value: Long): CommandResult {
        return CommandResult(
            isExecuted = true,
            result = (value + value)
        )
    }

}

class MinusCommand : Command<Long> {
    override fun execute(value: Long): CommandResult {
        return CommandResult(
            isExecuted = true,
            result = (value - value)
        )
    }

}