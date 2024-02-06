package com.muratov.springmvc.service

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import com.muratov.springmvc.dto.Entity

@Service
class AddressBookService {
    private var database: ConcurrentHashMap<Long, Entity> = ConcurrentHashMap()
    private var entityId: AtomicLong = AtomicLong(database.size.toLong())

    init {
        database[0] = Entity(0, "Test User 01", "Test address 01", "89876543211")
        database[1] = Entity(1, "Test User 02", "Test address 02", "89876543212")
        database[2] = Entity(2, "Test User 03", "Test address 03", "89876543213")
        database[3] = Entity(3, "Test User 04", "Test address 04", "89876543214")
        database[4] = Entity(4, "Test User 05", "Test address 05", "89876543215")
    }

    fun list(query: String?): Set<Map.Entry<Long, Entity>> {
        return if (query.isNullOrEmpty()) {
            database.entries
        } else {
            database.entries.apply {
                filter { entity ->
                    !entity.value.fullAddress.isNullOrEmpty() && entity.value.fullAddress!!.startsWith(query, true)
                }
            }
        }
    }

    fun view(entityId: Long) = database[entityId]

    fun add(entity: Entity): Entity {
        entityId = AtomicLong(database.size.toLong())
        database.putIfAbsent(entityId.incrementAndGet(), entity)
        return entity
    }

    fun edit(entityId: Long, entity: Entity) = database.put(entityId, entity)

    fun delete(entityId: Long) = database.remove(entityId)

    fun deleteAll() = database.clear()

    fun size() = database.size

    fun getNextEntityId(): Long = (size() + 1).toLong()
}