package de.p58i.utils.tableoptimizer.model

data class Problem(
    val tables: Set<Table>,
    val groups: Set<Group>,
    val pairings: Set<Pairing> = emptySet()
)
