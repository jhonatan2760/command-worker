package br.com.jhonatansouza.command

interface Command {
    fun execute(t: Any): CommandResult
}