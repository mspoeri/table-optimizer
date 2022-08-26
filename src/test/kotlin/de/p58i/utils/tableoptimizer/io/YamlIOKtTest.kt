package de.p58i.utils.tableoptimizer.io

import de.p58i.utils.tableoptimizer.model.Group
import de.p58i.utils.tableoptimizer.model.Solution
import de.p58i.utils.tableoptimizer.model.Table
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

internal class YamlIOKtTest{

    @Test
    internal fun readTestYaml() {
        val testFile = Path(YamlIOKtTest::class.java.getResource("/test-input.yaml")!!.path)

        val resultingProblem = yamlToProblem(testFile)

        assertThat(resultingProblem.groups).hasSize(2)
        assertThat(resultingProblem.tables).hasSize(2)
        assertThat(resultingProblem.pairings).hasSize(2)
        assertThat(resultingProblem.tableAffinities).hasSize(2)
    }

    @Test
    internal fun writeTestSolutions() {
        val testSolutions= listOf(
            Solution(
                tables = setOf(
                    Table(
                        name = "Test-Table 1",
                        seats = 2u,
                        groups = mutableSetOf(
                            Group(
                                name = "Test-Group-1",
                                people = mutableSetOf(
                                    "Person_1-1",
                                    "Person_1-2",
                                )
                            )
                        )
                    ),
                    Table(
                        name = "Test-Table 2",
                        seats = 2u,
                        groups = mutableSetOf(
                            Group(
                                name = "Test-Group-2",
                                people = mutableSetOf(
                                    "Person_2-1",
                                    "Person_2-2",
                                )
                            )
                        )
                    )
                )
            ),
            Solution(
                tables = setOf(
                    Table(
                        name = "Test-Table 1",
                        seats = 2u,
                        groups = mutableSetOf(
                            Group(
                                name = "Test-Group-1",
                                people = mutableSetOf(
                                    "Person_1-1",
                                    "Person_1-2",
                                )
                            )
                        )
                    ),
                    Table(
                        name = "Test-Table 2",
                        seats = 2u,
                        groups = mutableSetOf(
                            Group(
                                name = "Test-Group-2",
                                people = mutableSetOf(
                                    "Person_2-1",
                                    "Person_2-2",
                                )
                            )
                        )
                    )
                )
            ),
        )
        solutionsToYAML(Path("test-out.yaml"), testSolutions)
    }
}