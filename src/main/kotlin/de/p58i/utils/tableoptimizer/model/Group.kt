package de.p58i.utils.tableoptimizer.model

import java.util.UUID

data class Group(
    val name: String = UUID.randomUUID().toString(),
    val people: Set<String>,
) {
    fun count(): UInt = people.size.toUInt()

    override fun toString(): String {
        val peopleList = people.joinToString(separator = "\n-") { it }
        return """
            **Group($name): 
            $peopleList
        """.trimIndent()
    }


    override fun equals(other: Any?): Boolean {
        if (other !is Group){
            return false
        }

        return this.name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
