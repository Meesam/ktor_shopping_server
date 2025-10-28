// RedisClientManager.kt

import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.resource.ClientResources

// Use an appropriate URI, e.g., "redis://password@host:port/database"
const val REDIS_URI = "redis://localhost:6379/0"

object RedisClientManager {
    private lateinit var client: RedisClient
    private lateinit var connection: StatefulRedisConnection<String, String>

    fun initialize() {
        // Optional: configure client resources like an event loop group
        val resources = ClientResources.create()
        client = RedisClient.create(resources, REDIS_URI)

        // Connect. StatefulRedisConnection is thread-safe.
        connection = client.connect()
    }

    fun getAsyncCommands(): RedisAsyncCommands<String, String> {
        // Use the async commands for non-blocking I/O, which aligns with Ktor's Coroutines model
        return connection.async()
    }

    fun close() {
        connection.close()
        client.shutdown()
    }
}