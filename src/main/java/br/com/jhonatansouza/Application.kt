package br.com.jhonatansouza

import br.com.jhonatansouza.command.Command
import br.com.jhonatansouza.command.CommandResult
import br.com.jhonatansouza.command.CommandWorkerQueue

class Application

fun main(args: Array<String>){

    val commandWorker = CommandWorkerQueue()
    val soma200 = Sum()
    val sum400 = Sum()
    val namesCommand = NamesCommand()

    val command = commandWorker.initialize()
        .withCommand(namesCommand)
        .withParam("Jhonatan")
        .execute().responseFirst()


    println("Recebeu: ${command.toString()}")

}

class NamesCommand(): Command<String>{

    override fun execute(t: String): CommandResult<Names> {
        return CommandResult(
            result = Names("jhonatan", 28),
            isExecuted = true
        )
    }

}

class Sum() : Command<Long>{

    override fun execute(valor: Long): CommandResult<Long> {
        println("Resultado: " +  (valor + valor ))
        return CommandResult(
            isExecuted = true,
            result = (valor + valor)
        )
    }

}

class Message(): Command<String>{

    override fun execute(t: String): CommandResult<String> {
        println("Jhonatan teste Abarth - $t")
        return CommandResult(
            result = "Jhonatan",
            isExecuted = true
        )
    }


}

data class Names(val name: String, val age: Int)
