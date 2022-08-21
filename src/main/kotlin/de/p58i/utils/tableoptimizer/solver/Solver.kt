package de.p58i.utils.tableoptimizer.solver

import de.p58i.utils.tableoptimizer.model.Problem
import de.p58i.utils.tableoptimizer.model.Solution

interface Solver {
    fun solve(problem: Problem): Solution
}