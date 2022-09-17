package com.graphite.competitionplanner.player.repository

import com.graphite.competitionplanner.club.repository.ClubRepository
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper.Benchmark
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BenchmarkPlayerRepository(
    @Autowired val clubRepository: ClubRepository,
    @Autowired val repository: PlayerRepository
) {

    private val dataGenerator = DataGenerator()

    @Test
    fun comparingFetchingMultiplePlayers() {
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val playersIds = mutableListOf<Int>()
        for (i in 1..100) {
            playersIds.add(repository.store(dataGenerator.newPlayerSpec(clubId = club.id)).id)
        }

        for (i in 1..10) {
            val duration = Benchmark.realtime {
                repository.findAllForIds(playersIds)
            }
            println("Find all Execution took $duration")
        }

        for (i in 1..10) {
            val durationTwo = Benchmark.realtime {
                for (id in playersIds) {
                    repository.findById(id)
                }
            }
            println("Single Execution took $durationTwo")
        }
    }
}