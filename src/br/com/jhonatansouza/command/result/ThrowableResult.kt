package br.com.jhonatansouza.command.result

import br.com.jhonatansouza.command.CommandException

data class ThrowableResult(val isExecuted: Boolean = false, val exception: CommandException)
