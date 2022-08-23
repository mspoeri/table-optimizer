package de.p58i.utils.tableoptimizer.solver.evolutionary

import de.p58i.utils.tableoptimizer.model.Problem
import de.p58i.utils.tableoptimizer.model.Solution
import de.p58i.utils.tableoptimizer.solver.Solver
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
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

        var population = (1..populationSize.toInt()).map { randomSolution(problem) }.toMutableList()
        population.forEach { it.score = fitnessFunction(problem, it) }

        repeat(generations.toInt()) { generation ->
            population = evolve(population, problem)

            logger.debug("[${generation + 1}]/$generations] ${population.first().score} (${
                population.sumOf { individual -> individual.score }
            })")
        }

        return population.maxBy { individual -> individual.score }
    }

    private fun evolve(
        population: MutableList<Solution>, problem: Problem
    ): MutableList<Solution> {
        // assert(population.size.toUInt() == populationSize)
        val malePopulation = population.take(populationSize.toInt() / 2)
        val femalePopulation = population.takeLast(populationSize.toInt() / 2)

        runBlocking {
            population.addAll((0 until min(malePopulation.size, femalePopulation.size)).map { i ->
                async {
                    val child = mutationFunction(
                        pairingFunction(malePopulation[i], femalePopulation[i])
                    )
                    child.score = fitnessFunction(problem, child)
                    child
                }
            }.map { it.await() })
        }
        population.sortByDescending { individual -> individual.score }
        //assert(fitnessFunction(problem,population.first()) >= topScore)
        return population.take(populationSize.toInt()).toMutableList()

    }
}
