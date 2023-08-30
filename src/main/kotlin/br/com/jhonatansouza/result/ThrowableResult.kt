package br.com.jhonatansouza.result

import br.com.jhonatansouza.CommandException

data class ThrowableResult(val isExecuted: Boolean = false, val exception: CommandException)
