package br.com.jhonatansouza.command

interface Command<T> {
    fun execute(t: T): CommandResult
}