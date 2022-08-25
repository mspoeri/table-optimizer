package de.p58i.utils.tableoptimizer.input

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import de.p58i.utils.tableoptimizer.model.Group
import de.p58i.utils.tableoptimizer.model.Pairing
import de.p58i.utils.tableoptimizer.model.Problem
import de.p58i.utils.tableoptimizer.model.Table
import de.p58i.utils.tableoptimizer.model.TableAffinity
import java.nio.file.Path

private object YamlInput {
    val mapper: ObjectMapper = ObjectMapper(YAMLFactory()).registerModule(kotlinModule())
}

fun yamlToProblem(yamlFile: Path): Problem {
    return YamlInput.mapper.readValue<InputProblem>(yamlFile.toFile()).toProblem()
}


data class InputGroup(
    val name: String,
    val people: Collection<String>,
) {
    fun toGroup() = Group(name = name, people = HashSet(people))
}

data class InputTable(
    val name: String,
    val seats: Int,
) {
    fun toTable() = Table(name = name, seats = seats.toUInt())
}

data class InputPairing(
    @JsonAlias("person-A") val personA: String,
    @JsonAlias("person-B") val personB: String,
    val score: Double,
) {
    fun toPairing() = Pairing(personA = personA, personB = personB, score = score)
}

data class InputTableAffinity(
    @JsonAlias("table-name") val tableName: String,
    @JsonAlias("group-name") val groupName: String,
    val score: Double,
) {
    fun toTableAffinity() = TableAffinity(tableName = tableName, groupName = groupName, score = score)
}

data class InputProblem(
    val groups: Collection<InputGroup>,
    val tables: Collection<InputTable>,
    val pairings: Collection<InputPairing>,
    @JsonAlias("table-affinities") val tableAffinities: Collection<InputTableAffinity>,
) {
    fun toProblem() = Problem(
        groups = groups.map { it.toGroup() }.toSet(),
        tables = tables.map { it.toTable() }.toSet(),
        pairings = pairings.map { it.toPairing() }.toSet(),
        tableAffinities = tableAffinities.map { it.toTableAffinity() }.toSet()
    )
}