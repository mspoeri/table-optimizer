package de.p58i.utils.tableoptimizer.solver.evolutionary

import de.p58i.utils.tableoptimizer.model.Solution
import java.util.LinkedList

class DefaultTribeTermination {
    companion object{
        const val historyCount = 10
    }
    private val populationScoreHistory: MutableList<Double> = LinkedList()

    fun terminate(population: List<Solution>): Boolean {
        val populationScore = population.sumOf { it.score }
        populationScoreHistory.add(populationScore)
        if (populationScoreHistory.size > historyCount){
            populationScoreHistory.removeFirst()
        }
        return populationScoreHistory.size >= historyCount
                && populationScoreHistory.fold(true){ acc, d -> acc && (d == populationScore)}
    }
}