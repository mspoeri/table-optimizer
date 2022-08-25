package de.p58i.utils.tableoptimizer.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path
import de.p58i.utils.tableoptimizer.input.yamlToProblem
import de.p58i.utils.tableoptimizer.solver.evolutionary.EvolutionarySolver
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator

class OptimizeCommand : CliktCommand() {
    private val populationSize: Int by option("--population-size").int().default(200)
    private val generations: Int by option("--generations").int().default(100)
    private val tribes: Int by option("--tribes").int().default(2)
    private val inputFilePath by argument().path(mustExist = true)

    private val verbose by option("-v", "--verbose").flag(default = false)

    override fun run() {
        if (verbose){
            Configurator.setRootLevel(Level.DEBUG)
        }

        val solver = EvolutionarySolver(
            populationSize.toUInt(),
            generations.toUInt(),
            tribes.toUInt(),
        )
        val problem = yamlToProblem(inputFilePath)
        val solution = solver.solve(problem)

        echo(solution.toString())
    }

}