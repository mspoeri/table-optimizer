package de.p58i.utils.tableoptimizer.input

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

internal class YamlInputKtTest{

    @Test
    internal fun readTestYaml() {
        val testFile = Path(YamlInputKtTest::class.java.getResource("/test-input.yaml")!!.path)

        val resultingProblem = yamlToProblem(testFile)

        assertThat(resultingProblem.groups).hasSize(2)
        assertThat(resultingProblem.tables).hasSize(2)
        assertThat(resultingProblem.pairings).hasSize(2)
        assertThat(resultingProblem.tableAffinities).hasSize(2)
    }
}