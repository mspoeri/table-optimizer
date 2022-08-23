package de.p58i.utils.tableoptimizer.model

data class Problem(
    val tables: Set<Table>,
    val groups: Set<Group>,
    val pairings: Set<Pairing> = emptySet(),
    val tableAffinities: Set<TableAffinity> = emptySet(),
) {
    val optimalScore =
        pairings.filter { it.score > 0 }.sumOf { it.score } + tableAffinities.filter { it.score > 0 }.sumOf { it.score }
}
