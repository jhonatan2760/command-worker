package br.com.jhonatansouza.command

data class CommandResult<T>(
    var isExecuted: Boolean,
    val result: T? = null,
    val exception: Exception? = null
)
