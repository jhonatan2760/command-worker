package br.com.jhonatansouza

import br.com.jhonatansouza.enums.JobPosition
import br.com.jhonatansouza.exceptions.IndexNotFoundException

class CommandWorkerQueue {

    private lateinit var jobs: JobHandler

    @Synchronized
    fun initialize(): JobHandler {
        this.jobs = JobHandler()
        this.jobs.tearDown()
        return this.jobs
    }

    @Synchronized
    fun <T> getJobResult(item: Command<out Any>): T {
        synchronized(this.jobs.getJobs()) {
            return this.jobs.getJobs()
                .first { rtx -> rtx.containsKey(item) }
                .map { this.jobs.getJobs().indexOf(mapOf(it.key to it.value)) }
                .map { index -> this.jobs.getResult()[index].result as T }
                .first()
        }
    }
}

class ParamsJobHandler(
    private val command: Any,
    private val job: JobHandler,
    private val queue: ArrayList<Map<out Command<Any>, Any>>
) {

    @Synchronized
    fun withParam(params: Any): WithJoHandler {
        val receiver = mapOf(command as Command<Any> to params)
        synchronized(queue) {
            queue.add(receiver)
        }
        return WithJoHandler(command, job, queue)
    }

}

class WithJoHandler(
    private val command: Any,
    private val job: JobHandler,
    private val queue: ArrayList<Map<out Command<Any>, Any>>
) {

    fun withCommand(command: Any): ParamsJobHandler {
        return job.withCommand(command)
    }

    fun execute() = job.execute()

}

class JobHandler {

    private val queue = ArrayList<Map<out Command<Any>, Any>>()
    private val process = ArrayList<Map<out Command<*>, *>>()
    private val result = ArrayList<CommandResult<*>>()

    @Synchronized
    fun withCommand(item: Any): ParamsJobHandler {
        return ParamsJobHandler(item, this, this.queue)
    }

    @Synchronized
    fun tearDown(): JobHandler {
        queue.removeAll(queue)
        result.removeAll(result)
        return this
    }

    fun then(): JobHandler {
        return this
    }

    fun getJobs() = process

    fun getResult() = result

    fun <T : Any> getResult(position: JobPosition, type: Class<T>): T {
        return try {
            result[position.number].result as T
        } catch (ex: IndexOutOfBoundsException) {
            throw IndexNotFoundException("Index ${position} was not processed or found in the results")
        }
    }

    @Synchronized
    fun execute(): JobResponse {
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
