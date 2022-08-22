package de.p58i.utils.tableoptimizer.solver.evolutionary

import de.p58i.utils.tableoptimizer.model.Problem
import de.p58i.utils.tableoptimizer.model.Solution
import de.p58i.utils.tableoptimizer.solver.Solver
import org.slf4j.LoggerFactory
import kotlin.math.min

class EvolutionarySolver(
    val populationSize: UInt,
    val generations: UInt,
    val fitnessFunction: (problem: Problem, solution: Solution) -> Double = ::defaultFitnessFunction,
    val pairingFunction: (solutionA: Solution, solutionB: Solution) -> Solution = ::defaultPairingFunction,
    val mutationFunction: (solution: Solution) -> Solution = ::defaultMutation,
) : Solver {

    companion object {
        private val logger = LoggerFactory.getLogger(EvolutionarySolver::class.java)
    }

    override fun solve(problem: Problem): Solution {
        // assert vars
        var topScore = Double.NEGATIVE_INFINITY
        var guestCount = problem.groups.flatMap { it.people }.count()

        var population = (1..populationSize.toInt()).map { randomSolution(problem) }.toMutableList()
        repeat(generations.toInt()) { generation ->
            // assert(population.size.toUInt() == populationSize)
            val malePopulation = population.take(populationSize.toInt() / 2)
            val femalePopulation = population.takeLast(populationSize.toInt() / 2)

            repeat(min(malePopulation.size, femalePopulation.size)) { i ->
                val child = mutationFunction(
                    pairingFunction(malePopulation[i], femalePopulation[i])
                )
                //assert(child.guestCount() == guestCount)
                population.add(child)
            }
            population.forEach { individual ->individual.score = fitnessFunction(problem, individual) }
            population.sortByDescending { individual -> individual.score }
            //assert(fitnessFunction(problem,population.first()) >= topScore)
            topScore = population.first().score
            population = population.take(populationSize.toInt()).toMutableList()

            logger.debug("[${generation + 1}]/$generations] $topScore (${
                population.sumOf { individual -> individual.score}
            })")
        }

        return population.maxBy { individual -> individual.score }
    }
}

private fun Solution.guestCount() = this.tables.flatMap { it.groups }.flatMap { it.people }.count()