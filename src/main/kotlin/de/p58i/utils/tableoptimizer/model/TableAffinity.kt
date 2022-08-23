package de.p58i.utils.tableoptimizer.model

data class TableAffinity(
    val tableName: String,
    val groupName: String,
    val score: Double,
)
