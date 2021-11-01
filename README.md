# competition-planner

Competition-planner is a progressive web app for creating and hosting competitions in racket sports. The goal is to be
able to use one and the same software for

- Creating/preparing the competition
- Hosting it during the event
- Reporting scores and results

## Test suites

There are two kinds of test suite configurations:

- All but benchmarks - which runs all test cases except the bench marking tests
- Benchmark - which runs only the bench marking tests

Benchmarking tests are detected on the class name. It should begin with the word Benchmark e.g. BenchmarkPlayerApi could
be the name of a benchmarking test that target the player api.