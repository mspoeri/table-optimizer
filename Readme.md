# Table Optimizer
We all know the problem. You plan a dinner party with many guests and several tables and now you have to choose the right composition of the tables.
While it is often clear how individuals or groups should (or should not ) sit together, the big picture is often unclear.

This is where the table optimizer gets you covered.
Instead of wasting hours moving people from table to table, let your computer do the work for you.

Just specify the pairings you have in mind and the table optimizer will handle the rest.

Happy Seating!

## Usage

Specify and fill your input problem as YAML like this:

```yaml

tables:
  - name: Table-1
    seats: 8
  - name: Table-2
    seats: 4

groups:
  - name: Group-1
    people:
      - "Person-1_1"
      - "Person-1_2"
  - name: Group-2
    people:
      - "Person-2_1"
      - "Person-2_2"

pairings:
  - person-A: "Person-1_1"
    person-B: "Person-2_1"
    score: 5.6
  - person-A: "Person-1_2"
    person-B: "Person-2_2"
    score: 4.7

table-affinities:
  - table-name: Table-1
    group-name: Group-2
    score: 47.11
  - table-name: Table-2
    group-name: Group-1
    score: 13.37
```

then just run the application with Gradle:

```shell
 ./gradlew run --args='test-input.yaml' 
```

For more options see

```shell
./gradlew run --args='--help'
```