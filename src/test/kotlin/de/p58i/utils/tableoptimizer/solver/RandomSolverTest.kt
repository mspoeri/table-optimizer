package de.p58i.utils.tableoptimizer.solver

import de.p58i.utils.tableoptimizer.model.Group
import de.p58i.utils.tableoptimizer.model.Problem
import de.p58i.utils.tableoptimizer.model.Table
import net.datafaker.Faker
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class RandomSolverTest{

    private val testSubject = RandomSolver()

    private val fakePeople = (1..50).map {  _ -> Faker.instance().name().fullName()}

    @Test
    internal fun noTables() {
        val problem = Problem(
            emptySet(),
            setOf(Group(
                people = HashSet(fakePeople)
            ))
        )
        assertThatThrownBy { testSubject.solve(problem)}.isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    internal fun tooLessTables() {
        val problem = Problem(
            setOf(
                Table(seats = 29u),
                Table(seats = 20u),
            ),
            fakePeople.map { Group(people = mutableSetOf(it)) }.toSet()
        )
        assertThatThrownBy { testSubject.solve(problem)}.isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    internal fun successfulSeated() {
        val problem = Problem(
            (1..5).map { _ -> Table(seats = 10u) }.toSet(),
            fakePeople.map { Group(people = mutableSetOf(it)) }.toSet()
        )
        val result = testSubject.solve(problem)

        println(result)
    }
}