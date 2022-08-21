package de.p58i.utils.tableoptimizer.model

import java.util.UUID

data class Table(
    val name: String = UUID.randomUUID().toString(),
    val groups: MutableSet<Group> = HashSet(),
    val seats: UInt,
) {
    fun occupiedSeats() : UInt = groups.sumOf { it.count() }
    fun freeSeats() : UInt = seats - occupiedSeats()
    fun isEmpty() : Boolean = freeSeats() == seats

    override fun toString(): String {
        return """
            Table($name)[${occupiedSeats()}/$seats]:
            ---
            ${groups.joinToString(separator = "\n\n###\n\n") { it.toString() }}
        """.trimIndent()
    }
}
