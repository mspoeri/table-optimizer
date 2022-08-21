package de.p58i.utils.tableoptimizer.model

import java.util.UUID

data class Group(
    val name: String? = UUID.randomUUID().toString(),
    val people: Set<String>,
) {
    fun count(): UInt = people.size.toUInt()

    override fun toString(): String {
        val peopleList = people.joinToString(separator = " | ") { it }
        return """
            Group($name): 
                $peopleList
        """.trimIndent()
    }

}
