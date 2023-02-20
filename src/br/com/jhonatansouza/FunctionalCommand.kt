package br.com.jhonatansouza

import br.com.jhonatansouza.command.CommandResult
import java.util.function.Consumer

interface FunctionalCommand  {

    fun execute(): CommandResult

}