package br.com.jhonatansouza.command

class CommandWorkerQueue {
    private lateinit var jobs: JobHandler

    fun initialize(): JobHandler {
        this.jobs = JobHandler()
        this.jobs.tearDown()
        return this.jobs
    }

    fun <T> getJobResult(item: Command): T = this.jobs.process
        .first { rtx -> rtx.containsKey(item) }
        .map { this.jobs.process.indexOf(mapOf(it.key to it.value)) }
        .map { index -> this.jobs.result[index].result as T }
        .first()
}

class JobHandler {

    val queue = ArrayList<Map<Command, Any>>()
    val process = ArrayList<Map<Command, Any>>()
    val result = ArrayList<CommandResult>()

    fun addJob(item: Command, parameter: Any): JobHandler {
        val receiver = mapOf(item to parameter)
        queue.add(receiver)
        return this
    }

    fun tearDown(): JobHandler {
        queue.removeAll(queue)
        result.removeAll(result)
        return this
    }

    fun then(): JobHandler {
        return this
    }

    fun execute(): JobHandler {
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
                        throw exception ?: CommandException("Unable to handle statement-pdf queue. error=$exception")
                    }
            }
        ))

        return this
    }
}