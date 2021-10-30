package com.surovtsev.cool3dminesweeper.viewmodels.rankinscreenviewmodel.helpers

import com.surovtsev.cool3dminesweeper.models.game.database.RankingData

class RankingDataWithPlaces(
    val rankingData: RankingData,
    val place: Int
)

typealias RankingListWithPlaces = List<RankingDataWithPlaces>

object RankingListWithPlacesHelper {
    fun create(
        rankingData: List<RankingData>
    ): RankingListWithPlaces {
        val comparator: Comparator<Pair<RankingData, Int>> =
            Comparator { a, b ->
                compareValues(
                    a.first.elapsed,
                    b.first.elapsed
                )
            }

        val unsortedPairs = rankingData
            .mapIndexed { idx, rD ->
                rD to idx
            }

        val sortedPairs = unsortedPairs
            .sortedWith(
                comparator
            )

        val indexes = IntArray(rankingData.size)

        sortedPairs.mapIndexed { index, pair ->
            indexes[pair.second] = index
        }

        val res = rankingData.indices.toList().map { idx ->
            RankingDataWithPlaces(
                rankingData[idx],
                indexes[idx]
            )
        }

        return res
    }
}
