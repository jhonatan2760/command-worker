package br.com.jhonatansouza

interface Command<T> {
    fun execute(t: T): CommandResult<*>
}