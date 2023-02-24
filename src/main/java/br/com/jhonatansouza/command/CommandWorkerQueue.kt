package br.com.jhonatansouza.command

class CommandWorkerQueue {

    private lateinit var jobs: JobHandler

    fun initialize(): JobHandler {
        this.jobs = JobHandler()
        this.jobs.tearDown()
        return this.jobs
    }

    fun <T> getJobResult(item: Command<out Any>): T = this.jobs.process
        .first { rtx -> rtx.containsKey(item) }
        .map { this.jobs.process.indexOf(mapOf(it.key to it.value)) }
        .map { index -> this.jobs.result[index].result as T }
        .first()

}

class ParamsJobHandler(val command: Any, val job: JobHandler){

    private val comand: Any = command

    fun withParam(params: Any): JobHandler{
        val receiver = mapOf(comand as Command<Any> to params)
        job.queue.add(receiver)
        return job
    }

}

class JobHandler {

    val queue = ArrayList<Map<out Command<Any>, Any>>()
    val process = ArrayList<Map<out Command<*>, *>>()
    val result = ArrayList<CommandResult<*>>()

    fun withCommand(item: Any): ParamsJobHandler {
        return ParamsJobHandler(item, this)
    }


    fun tearDown(): JobHandler {
        queue.removeAll(queue)
        result.removeAll(result)
        return this
    }

    fun then(): JobHandler {
        return this
    }

    fun execute(): JobResponse {
        result.addAll(ArrayList(queue
            .flatMap { job -> job.entries }
            .map {
                process.add(mapOf(it.toPair()))
                val result = it.key.execute(it.value)
                queue.remove(mapOf(it.toPair()))
                result
            }.apply {
                find { it -> !it.isExecuted }
                    ?.apply {
                        throw exception
                            ?: CommandException("Unable to handle {proccess - name} queue. error=$exception")
                    }
            }
        ))

        return JobResponse(this, result)
    }


}

class JobResponse(val same: JobHandler, val response: MutableList<CommandResult<*>>) {

    fun responseFirst(): Any? {
        return response.first().result
    }

    fun then() = same

}