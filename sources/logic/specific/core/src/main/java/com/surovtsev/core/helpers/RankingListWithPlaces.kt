/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.core.helpers

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
