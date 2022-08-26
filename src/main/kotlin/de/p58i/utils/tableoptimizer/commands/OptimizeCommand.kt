package de.p58i.utils.tableoptimizer.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path
import de.p58i.utils.tableoptimizer.io.solutionsToYAML
import de.p58i.utils.tableoptimizer.io.yamlToProblem
import de.p58i.utils.tableoptimizer.solver.evolutionary.EvolutionarySolver
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import java.nio.file.Path

class OptimizeCommand : CliktCommand() {
    private val populationSize: Int by option("--population-size", help = "Number of individuals to be calculated per generation").int().default(200)
    private val generations: Int by option("--generations", help = "Number of generations to be calculated").int().default(100)
    private val tribes: Int by option("--tribes", help = "Number of tribes the population is split into").int().default(2)
    private val inputFilePath by argument(help = "Path to read input out of a valid yaml").path(mustExist = true)
    private val outputFilePath by option("-o", "--output", help = "Path to store solution as YAML").path(mustExist = false)

    private val verbose by option("-v", "--verbose", help = "Enables debug print").flag(default = false)

    override fun run() {
        if (verbose) {
            Configurator.setRootLevel(Level.DEBUG)
        }

        val solver = EvolutionarySolver(
            populationSize.toUInt(),
            generations.toUInt(),
            tribes.toUInt(),
        )
        val problem = yamlToProblem(inputFilePath)
        val solution = solver.solve(problem)

        val optimality = "%.2f".format((solution.score / problem.optimalScore) * 100.0)
        echo("Solution found with score: [${solution.score} / ${problem.optimalScore}] ($optimality%)")
        echo(solution.toString())
        val outputFile: Path? = outputFilePath
        if (outputFile != null) {
            solutionsToYAML(outputFile, listOf(solution))
        }
    }
}