@file:Suppress("ClassName")

package de.p58i.utils.tableoptimizer.solver.evolutionary

import de.p58i.utils.tableoptimizer.model.Group
import de.p58i.utils.tableoptimizer.model.Pairing
import de.p58i.utils.tableoptimizer.model.Problem
import de.p58i.utils.tableoptimizer.model.Solution
import de.p58i.utils.tableoptimizer.model.Table
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DefaultFunctionsKtTest {

    @Nested
    inner class `defaultFitnessFunction()` {

        @Test
        fun `empty solution`() {
            val testProblem = Problem(emptySet(), emptySet(), emptySet())
            val testSolution = Solution(emptySet())

            assertThat(defaultFitnessFunction(testProblem, testSolution)).isEqualTo(0.0)
        }

        @Test
        fun `no pairing - no over placement`() {
            val testProblem = Problem(mutableSetOf(Table(name = "Test-Table", seats = 0u)), emptySet(), emptySet())
            val testSolution = Solution(
                mutableSetOf(
                    Table(
                        name = "Test-Table",
                        seats = 5u,
                        groups = mutableSetOf(Group(people = setOf("1", "2", "3")), Group(people = setOf("4", "5")))
                    )
                )
            )
            assertThat(defaultFitnessFunction(testProblem, testSolution)).isEqualTo(0.0)
        }

        @Test
        fun `no pairing - but over placement`() {
            val testProblem = Problem(mutableSetOf(Table(name = "Test-Table", seats = 0u)), emptySet(), emptySet())
            val testSolution = Solution(
                mutableSetOf(
                    Table(
                        name = "Test-Table",
                        seats = 4u,
                        groups = mutableSetOf(Group(people = setOf("1", "2", "3")), Group(people = setOf("4", "5")))
                    )
                )
            )
            assertThat(defaultFitnessFunction(testProblem, testSolution)).isEqualTo(-100.0)
        }

        @Test
        fun `negative pairing - miss`() {
            val testProblem = Problem(
                mutableSetOf(Table(name = "Test-Table", seats = 0u)), emptySet(), setOf(Pairing("1", "2", -500.0))
            )
            val testSolution = Solution(
                mutableSetOf(
                    Table(
                        name = "Test-Table-1",
                        seats = 4u,
                        groups = mutableSetOf(Group(people = setOf("1", "3")), Group(people = setOf("5")))
                    ), Table(
                        name = "Test-Table-2", seats = 4u, groups = mutableSetOf(Group(people = setOf("2", "4")))
                    )
                )
            )
            assertThat(defaultFitnessFunction(testProblem, testSolution)).isEqualTo(.0)
        }

        @Test
        fun `negative pairing - hit`() {
            val testProblem = Problem(
                mutableSetOf(Table(name = "Test-Table", seats = 0u)), emptySet(), setOf(Pairing("1", "2", -500.0))
            )
            val testSolution = Solution(
                mutableSetOf(
                    Table(
                        name = "Test-Table-1",
                        seats = 4u,
                        groups = mutableSetOf(Group(people = setOf("1", "2")), Group(people = setOf("5")))
                    ), Table(
                        name = "Test-Table-2", seats = 4u, groups = mutableSetOf(Group(people = setOf("3", "4")))
                    )
                )
            )
            assertThat(defaultFitnessFunction(testProblem, testSolution)).isEqualTo(-500.0)
        }
    }

    @Nested
    inner class `swapTwoGroups()` {
        @Test
        fun `only two groups`() {
            val testGroupA = Group("Test-Group-A", emptySet())
            val testGroupB = Group("Test-Group-B", emptySet())
            val testTableA = Table("Test-Table-A", mutableSetOf(testGroupA), 0u)
            val testTableB = Table("Test-Table-B", mutableSetOf(testGroupB), 0u)

            assertThat(testTableA.groups.contains(testGroupA)).isTrue
            assertThat(testTableA.groups.contains(testGroupB)).isFalse
            assertThat(testTableB.groups.contains(testGroupB)).isTrue
            assertThat(testTableB.groups.contains(testGroupA)).isFalse

            swapTwoGroups(testTableA, testGroupA, testTableB, testGroupB)

            assertThat(testTableA.groups.contains(testGroupA)).isFalse
            assertThat(testTableA.groups.contains(testGroupB)).isTrue
            assertThat(testTableB.groups.contains(testGroupB)).isFalse
            assertThat(testTableB.groups.contains(testGroupA)).isTrue
        }
    }
}