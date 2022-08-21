package de.p58i.utils.tableoptimizer.solver

import de.p58i.utils.tableoptimizer.model.Problem
import de.p58i.utils.tableoptimizer.model.Solution
import org.slf4j.LoggerFactory

class RandomSolver: Solver {

    private val logger = LoggerFactory.getLogger(RandomSolver::class.java)

    override fun solve(problem: Problem): Solution {
        val tablesToSeat = problem.tables.map { it.copy(groups = HashSet()) }.toSet()

        problem.groups.shuffled().forEach { group ->
            logger.debug("Search table for group [${group.name}]")
            val tableToSeat = tablesToSeat.find{ table -> table.freeSeats() >= group.count()} ?: throw IllegalStateException("Too less seats")
            logger.debug("Found table[${tableToSeat.name}] for group[${group.name}]")
            tableToSeat.groups.add(group)
        }

        return Solution(tablesToSeat)
    }
}