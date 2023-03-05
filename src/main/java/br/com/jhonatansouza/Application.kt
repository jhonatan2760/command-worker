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

    val commandWorker = CommandWorkerQueue().initialize()

            val soma200 = Sum()
            val sum400 = Sum()
            val namesCommand = NamesCommand()

            val command = commandWorker.withCommand(namesCommand)
                .withParam(Names("jhonatan Thread - ", 3))
                .execute()
                .then().withCommand(NamesCommand())
                .withParam(Names("Jhonatan Teste", 9))
                .execute()
                .then()
                .withCommand(ApiCommand())
                .withParam("Teste API")
                .execute()

            println(command.then().getResult(JobPosition.THIRD))
}

class ApiCommand: Command<String> {
    override fun execute(t: String): CommandResult<String> {
         val url = URL("https://jsonplaceholder.typicode.com/posts/1")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode
        println("Api Request")
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val input = BufferedReader(InputStreamReader(connection.inputStream))
            var inputLine: String?
            val response = StringBuffer()
            while (input.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
            input.close()

            return CommandResult(
                result = response.toString(),
                isExecuted = true
            )
        } else {
            println("API request failed: HTTP error code $responseCode")
            return CommandResult(
                isExecuted = false
            )
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

class Sum() : Command<Long> {

    override fun execute(valor: Long): CommandResult<Long> {
        println("Resultado: " + (valor + valor))
        return CommandResult(
            isExecuted = true,
            result = (valor + valor)
        )
    }

}

class Message() : Command<String> {

    override fun execute(t: String): CommandResult<String> {
        println("Jhonatan teste Abarth - $t")
        return CommandResult(
            result = "Jhonatan",
            isExecuted = true
        )
    }


}

data class Names(val name: String, val age: Int)
