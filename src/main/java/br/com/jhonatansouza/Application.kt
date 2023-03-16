package br.com.jhonatansouza

import br.com.jhonatansouza.command.Command
import br.com.jhonatansouza.command.CommandResult
import br.com.jhonatansouza.command.CommandWorkerQueue
import br.com.jhonatansouza.command.enum.JobPosition
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Application

fun main(args: Array<String>) {

    /**
     * Falta deixar mais idiomatico
     */

    val result =   CommandWorkerQueue().initialize()
        .withCommand(SumCommand())
        .withParam(40.0).execute()

    println(result.then().getResult())
}

class SumCommand : Command<Double>{
    override fun execute(t: Double): CommandResult<*> {
        return CommandResult(
            result = t + t,
            isExecuted = true
        )
    }

}
