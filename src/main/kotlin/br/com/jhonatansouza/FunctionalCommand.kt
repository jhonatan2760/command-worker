package br.com.jhonatansouza

import br.com.jhonatansouza.CommandResult

interface FunctionalCommand {

    fun execute(): CommandResult<*>

}