package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.draw.interfaces.RegistrationSeedDTO
import com.graphite.competitionplanner.registration.interfaces.RegistrationRankingDTO
import org.springframework.stereotype.Component
import kotlin.math.floor
import kotlin.math.log2

/**
 * Seeding players means that you assign the best players who are taking part in the competition or tournament to a
 * position in an ordered list before the draw commences.
 *
 * Their positions in the list are determined by their relative playing standard - with the best player ranked at
 * number one.
 *
 * By doing this you can then distribute these players evenly throughout the draw so that they will not meet in the
 * early round of a knock-out competition or tournament.
 *
 * The number of seeds will vary according to the number of entries in the competition or tournament. However, the
 * number of seeds will always be to the power of 2, so this means you should have either 1, 2, 4, 8, 16, 32, or 64 etc.
 * seeds. All other player will not be seeded and will be randomly drawn when the draw takes place
 */
@Component
class CreateSeed {

    fun execute(registrations: List<RegistrationRankingDTO>): List<RegistrationSeedDTO> {

        val sortedHighestRankFirst: List<RegistrationRankingDTO> = registrations.toList().sortedBy { -it.rank }
        val numberOfSeeds: Int = calculateNumberOfSeeds(registrations)

        return sortedHighestRankFirst.mapIndexed { index, it ->
            RegistrationSeedDTO(
                it.registrationId,
                it.competitionCategoryId,
                seed = if (index < numberOfSeeds) index + 1 else null
            )
        }
    }

    private fun calculateNumberOfSeeds(registrations: List<RegistrationRankingDTO>): Int {
        return with(registrations) {
            if (isEmpty()) 0
            else floor(log2(size.toDouble())).toInt()
        }
    }
}
