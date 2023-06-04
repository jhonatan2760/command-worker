package br.com.jhonatansouza

import br.com.jhonatansouza.command.CommandResult

interface FunctionalCommand {

    fun execute(): CommandResult<*>

}