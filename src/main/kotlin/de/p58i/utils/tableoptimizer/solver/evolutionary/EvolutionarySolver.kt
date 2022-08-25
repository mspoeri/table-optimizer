package de.p58i.utils.tableoptimizer.solver.evolutionary

import de.p58i.utils.tableoptimizer.model.Problem
import de.p58i.utils.tableoptimizer.model.Solution
import de.p58i.utils.tableoptimizer.solver.Solver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import kotlin.math.min

class EvolutionarySolver(
    val populationSize: UInt,
    val generations: UInt,
    val tribes: UInt = 1u,
    val tribeGenerations: UInt = generations / 4u,
    val fitnessFunction: (problem: Problem, solution: Solution) -> Double = ::defaultFitnessFunction,
    val pairingFunction: (solutionA: Solution, solutionB: Solution) -> Solution = ::defaultPairingFunction,
    val mutationFunction: (solution: Solution) -> Solution = ::defaultMutation,
) : Solver {
    private val tribeShuffles = (generations / tribeGenerations ).toInt()
    private val tribeSize = populationSize / tribes

    companion object {
        private val logger = LogManager.getLogger(EvolutionarySolver::class.java)
    }

    override fun solve(problem: Problem): Solution {
        var population =
            (1..populationSize.toInt()).map { randomSolution(problem).apply { score = fitnessFunction(problem, this) } }
                .toMutableList()

        for ( tribeShuffle in 0 until  tribeShuffles){
            runBlocking(Dispatchers.Default) {
                population = population.shuffled().chunked(tribeSize.toInt()).mapIndexed { index, tribe ->
                    val tribeTermination = DefaultTribeTermination()
                    async {
                        var evolvedTribe = tribe.toMutableList()
                        for( tribeGeneration in 1 ..tribeGenerations.toInt() ) {
                            evolvedTribe = evolve(evolvedTribe, problem)

                            if (logger.isDebugEnabled) {
                                logger.debug("(Tribe-$index)[${tribeShuffle * tribeGenerations.toInt() + tribeGeneration}/$generations] ${evolvedTribe.first().score} (${
                                    evolvedTribe.sumOf { individual -> individual.score }
                                })")
                            }
                            if (tribeTermination.terminate(evolvedTribe)){
                                break
                            }
                        }
                        evolvedTribe
                    }
                }.flatMap { it.await() }.sortedByDescending { it.score }.take(populationSize.toInt()).toMutableList()
            }
            if (population.first().score == problem.optimalScore){
                break
            }
            logger.debug("(Total)[${(tribeShuffle + 1) * tribeGenerations.toInt()}/$generations] ${population.first().score} (${
                population.sumOf { individual -> individual.score }
            })")
        }

        return population.maxBy { individual -> individual.score }
    }

    private fun evolve(
        population: MutableList<Solution>, problem: Problem
    ): MutableList<Solution> {
        // assert(population.size.toUInt() == populationSize)
        val shuffledPopulation = population.shuffled()
        val malePopulation = shuffledPopulation.take(tribeSize.toInt() / 2)
        val femalePopulation = shuffledPopulation.takeLast(tribeSize.toInt() / 2)

        runBlocking(Dispatchers.Default) {
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
        return population.take(tribeSize.toInt()).toMutableList()
    }
}
