package br.com.jhonatansouza.command

import br.com.jhonatansouza.command.enum.JobPosition
import br.com.jhonatansouza.command.exceptions.IndexNotFoundException
import java.lang.IndexOutOfBoundsException

class CommandWorkerQueue {

    private lateinit var jobs: JobHandler

    @Synchronized fun initialize(): JobHandler {
        this.jobs = JobHandler()
        this.jobs.tearDown()
        return this.jobs
    }

    @Synchronized fun <T> getJobResult(item: Command<out Any>): T {
        synchronized(this.jobs.getJobs()) {
            return this.jobs.getJobs()
                .first { rtx -> rtx.containsKey(item) }
                .map { this.jobs.getJobs().indexOf(mapOf(it.key to it.value)) }
                .map { index -> this.jobs.getResult()[index].result as T }
                .first()
        }
    }
}

class ParamsJobHandler(private val command: Any, private val job: JobHandler,private val queue: ArrayList<Map<out Command<Any>, Any>>){

    @Synchronized fun withParam(params: Any): JobHandler{
        val receiver = mapOf(command as Command<Any> to params)
        synchronized(queue) {
            queue.add(receiver)
        }
        return job
    }

}

class JobHandler {

    private val queue = ArrayList<Map<out Command<Any>, Any>>()
    private val process = ArrayList<Map<out Command<*>, *>>()
    private val result = ArrayList<CommandResult<*>>()

    @Synchronized fun withCommand(item: Any): ParamsJobHandler {
        return ParamsJobHandler(item, this, this.queue)
    }

    @Synchronized fun tearDown(): JobHandler {
        queue.removeAll(queue)
        result.removeAll(result)
        return this
    }

    fun then(): JobHandler {
        return this
    }

    fun getJobs() = process

    fun getResult() = result

    fun getResult(position: JobPosition): Any? {
        return try {
            result.get(position.number).result
        }catch (ex: IndexOutOfBoundsException){
            throw IndexNotFoundException("Index ${position.number} was not processed or found in the results")
        }
    }

    @Synchronized fun execute(): JobResponse {
        val results = mutableListOf<CommandResult<*>>()
        synchronized(queue) {
            synchronized(process) {
                queue
                    .flatMap { job -> job.entries }
                    .map {
                        process.add(mapOf(it.toPair()))
                        val result = it.key.execute(it.value)
                        results.add(result)
                        queue.remove(mapOf(it.toPair()))
                        result
                    }.apply {
                        find { it -> !it.isExecuted }
                            ?.apply {
                                throw exception
                                    ?: CommandException("Unable to handle {proccess - name} queue. error=$exception")
                            }
                    }
            }
        }
        result.addAll(results)
        return JobResponse(this)
    }
}

class JobResponse(private val same: JobHandler) {
    fun then() = same
}
