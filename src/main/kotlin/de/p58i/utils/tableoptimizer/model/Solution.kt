package de.p58i.utils.tableoptimizer.model

data class Solution(
    val tables: Set<Table>
) {
    override fun toString(): String {
        val emptyTables = tables.filter { it.isEmpty() }
        val occupiedTables = tables.filter { !it.isEmpty() }

        return """
            Solution with ${occupiedTables.size} occupied and ${emptyTables.size} empty tables:
                Occupied:
                ---
                ${occupiedTables.joinToString(separator = "\n---\n") { it.toString() }.trimIndent()}
                
                
                Empty:
                ---
                ${emptyTables.joinToString(separator = "\n---\n") { "${it.name}[0/${it.seats}]" }.trimIndent()}
        """.trimIndent()
    }
}
