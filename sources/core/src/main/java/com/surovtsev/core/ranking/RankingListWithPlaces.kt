package com.surovtsev.core.ranking

import com.surovtsev.core.room.dao.RankingList
import com.surovtsev.core.room.entities.Ranking

class RankingDataWithPlaces(
    val rankingData: Ranking.RankingData,
    val place: Int
)


object RankingListWithPlacesHelper {
    fun create(
        rankingList: RankingList
    ): RankingListWithPlaces {
        val comparator: Comparator<Pair<Ranking, Int>> =
            Comparator { a, b ->
                compareValues(
                    a.first.rankingData.elapsed,
                    b.first.rankingData.elapsed
                )
            }

        val unsortedPairs = rankingList
            .mapIndexed { idx, rD ->
                rD to idx
            }

        val sortedPairs = unsortedPairs
            .sortedWith(
                comparator
            )

        val indexes = IntArray(rankingList.size)

        sortedPairs.mapIndexed { index, pair ->
            indexes[pair.second] = index
        }

        val res = rankingList.indices.toList().map { idx ->
            RankingDataWithPlaces(
                rankingList[idx].rankingData,
                indexes[idx]
            )
        }

        return res
    }
}
