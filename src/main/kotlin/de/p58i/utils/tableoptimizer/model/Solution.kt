package de.p58i.utils.tableoptimizer.model

data class Solution(
    val tables: Set<Table>,
) {
    var score = Double.NEGATIVE_INFINITY
    override fun toString(): String {
        val emptyTables = tables.filter { it.isEmpty() }
        val occupiedTables = tables.filter { !it.isEmpty() }

        return """
Solution with ${occupiedTables.size} occupied and ${emptyTables.size} empty tables:

Occupied:
---

${occupiedTables.joinToString(separator = "\n\n###\n\n") {table -> 
"""
${table.name}[${table.occupiedSeats()}/${table.seats}]
${table.groups.joinToString("\n---\n") { group ->
"""
* ${group.name}(${group.count()})
${group.people.joinToString("\n") { 
"""
- $it    
""".trimIndent()
}}
""".trimIndent()
}}
""".trimIndent()
}}


Empty:
---
${emptyTables.joinToString(separator = "\n---\n") { "${it.name}[0/${it.seats}]" }}
""".trimIndent()
    }
}
