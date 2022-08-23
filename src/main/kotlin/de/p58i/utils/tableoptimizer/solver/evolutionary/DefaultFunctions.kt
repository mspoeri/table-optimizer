package de.p58i.utils.tableoptimizer.solver.evolutionary

import de.p58i.utils.tableoptimizer.model.Group
import de.p58i.utils.tableoptimizer.model.Pairing
import de.p58i.utils.tableoptimizer.model.Problem
import de.p58i.utils.tableoptimizer.model.Solution
import de.p58i.utils.tableoptimizer.model.Table
import de.p58i.utils.tableoptimizer.model.TableAffinity
import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random

object DefaultConstants {
    const val mutationProbability = 0.20
}

fun defaultMutation(solution: Solution): Solution {
    if (Random.nextDouble() <= DefaultConstants.mutationProbability) {
        swapRandomGroup(solution)
    }

    return solution
}

fun randomSolution(problem: Problem): Solution {
    val groupsToSeat = problem.groups.shuffled().toMutableList()
    val tablesToSeat = problem.tables.map { it.copy(groups = HashSet()) }.toSet()

    groupsToSeat.forEach { group ->
        val matchingTable = tablesToSeat.shuffled().find { it.freeSeats() >= group.count().toInt() }
        matchingTable?.groups?.add(group)
    }
    groupsToSeat.removeAll(tablesToSeat.flatMap { it.groups })

    groupsToSeat.forEach { group ->
        tablesToSeat.shuffled().first().groups.add(group)
    }

    return Solution(tablesToSeat)
}

fun defaultPairingFunction(solutionA: Solution, solutionB: Solution): Solution {
    val pivotCount = min(solutionA.tables.size, solutionB.tables.size) / 2

    val childTableSet = HashSet(solutionA.tables.shuffled().take(pivotCount).map { it.copy(groups = HashSet(it.groups)) })
    childTableSet.addAll(solutionB.tables.filter { bTable -> !childTableSet.contains(bTable) }.map { it.copy(groups = HashSet(it.groups)) })

    removeDoubleGroups(childTableSet)
    addMissingGroups(childTableSet, solutionA.tables.flatMap { it.groups })

    return Solution(childTableSet)
}

fun defaultFitnessFunction(problem: Problem, solution: Solution): Double {
    val overPlacement = solution.tables.sumOf { min(it.freeSeats(), 0) }
    val pairingScore = problem.pairings.filter { it.applies(solution) }.sumOf { pairing -> pairing.score }
    val tableScore = problem.tableAffinities.filter { it.applies(solution) }.sumOf { affinity -> affinity.score }

    return (100 * overPlacement) + pairingScore + tableScore
}

fun removeDoubleGroups(tables: Set<Table>) {
    tables.forEach { table ->
        table.groups.forEach { group ->
            tables.filter { it != table }.forEach { it.groups.remove(group) }
        }
    }
}

fun addMissingGroups(tables: Collection<Table>, allGroups: Collection<Group>){
    val placedGroups = tables.flatMap { it.groups }
    val missingGroups = allGroups.filter { !placedGroups.contains(it) }

    missingGroups.forEach { tables.place(it) }
}

private fun Collection<Table>.place(group: Group) {
    this.filter { !it.isEmpty() }.maxByOrNull { it.freeSeats() }?.groups?.add(group) ?:
    this.maxBy { it.freeSeats() }.groups.add(group)
}

private fun Pairing.applies(solution: Solution): Boolean {
    return solution.tables.map { this.applies(it) }.find { it } ?: false
}

private fun Pairing.applies(table: Table): Boolean {
    return table.groups.flatMap { it.people }.containsAll(listOf(this.personA, this.personB))
}

private fun TableAffinity.applies(solution: Solution): Boolean {
    return solution.tables.map { this.applies(it) }.find { it } ?: false
}
private fun TableAffinity.applies(table: Table): Boolean {
    return table.name == this.tableName && table.groups.map { it.name }.contains(this.groupName)
}

private fun swapRandomGroup(solution: Solution) {
    val randomTable = solution.tables.filter { table -> !table.isEmpty() }.shuffled().first()
    val randomGroup = randomTable.groups.shuffled().first()

    randomTable.groups.remove(randomGroup)

    val fittingTable =
        solution.tables.filter { it != randomTable }.firstOrNull { it.freeSeats() >= randomGroup.count().toInt() }
    if (fittingTable != null) {
        fittingTable.groups.add(randomGroup)
        return
    }

    val swapTable = solution.tables.firstOrNull { table ->
        table.groups.filter { it.count() >= randomGroup.count() }
            .find { it.count().toInt() <= randomTable.freeSeats() } != null
    } ?: solution.tables.shuffled().first()

    val swapGroup = swapTable.groups.minBy { abs((it.count().toInt() - randomGroup.count().toInt())) }

    swapTwoGroups(randomTable, randomGroup, swapTable, swapGroup)
}

fun swapTwoGroups(tableA: Table, groupA: Group, tableB: Table, groupB: Group) {
    tableA.groups.remove(groupA)
    tableB.groups.remove(groupB)

    tableA.groups.add(groupB)
    tableB.groups.add(groupA)
}