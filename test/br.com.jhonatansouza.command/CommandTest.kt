package br.com.jhonatansouza.command

import junit.framework.TestCase.assertEquals
import org.junit.Test
import kotlin.concurrent.thread

class CommandTest {

    @Test
    fun testExecuteInParallel() {
        val jobHandler = JobHandler()
        val iterations = 1000

        repeat(iterations) { i ->
            jobHandler.withCommand(MyCommand(i))
                .withParam("param$i")
                .then()
        }

        val responses = mutableListOf<JobResponse>()
        repeat(10) {
            thread {
                responses.add(jobHandler.execute())
            }
        }

        responses.forEach { response ->
            assertEquals(iterations, response.response.size)
            for (i in 0 until iterations) {
                assertEquals("result$i", response.response[i].result)
            }
        }
    }


}
class MyCommand(val id: Int) : Command<String> {
    override fun execute(params: String): CommandResult<String> {
        return CommandResult(isExecuted = true, result = "result$id")
    }
}
