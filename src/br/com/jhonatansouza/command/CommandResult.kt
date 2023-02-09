package br.com.jhonatansouza.command

data class CommandResult(
    var isExecuted: Boolean,
    val result: Any? = null,
    val exception: Exception? = null
)
