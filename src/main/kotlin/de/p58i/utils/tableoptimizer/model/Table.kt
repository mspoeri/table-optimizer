package de.p58i.utils.tableoptimizer.model

import java.util.UUID

data class Table(
    val name: String = UUID.randomUUID().toString(),
    val groups: MutableSet<Group> = HashSet(),
    val seats: UInt,
) {
    fun occupiedSeats() : UInt = groups.sumOf { it.count() }
    fun freeSeats() : Int = seats.toInt() - occupiedSeats().toInt()
    fun isEmpty() : Boolean = freeSeats() == seats.toInt()

    override fun toString(): String {
        return  """
##Table($name)[${occupiedSeats()}/$seats]:
${groups.joinToString(separator = "\n") {
"""
${it.name}(${it.count()})
${it.people.joinToString(prefix = "-", separator = "\n-") { person -> person }}
""".trimIndent()
it.toString() 
}}
---
""".trimIndent()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Table){
            return false
        }

        return this.name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
