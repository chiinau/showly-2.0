// ktlint-disable max-line-length
package com.michaldrabik.data_local.sources

import com.michaldrabik.data_local.database.model.Episode

interface EpisodesLocalDataSource {

  suspend fun upsert(episodes: List<Episode>)

  suspend fun delete(items: List<Episode>)

  suspend fun upsertChunked(items: List<Episode>)

  suspend fun getAllForSeason(seasonTraktId: Long): List<Episode>

  suspend fun getAllByShowId(showTraktId: Long): List<Episode>

  suspend fun getAllByShowId(showTraktId: Long, seasonNumber: Int): List<Episode>

  suspend fun getAllByShowsIds(showTraktIds: List<Long>): List<Episode>

  suspend fun getAllByShowsIdsChunk(showTraktIds: List<Long>): List<Episode>

  suspend fun getFirstUnwatched(showTraktId: Long, toTime: Long): Episode?

  suspend fun getFirstUnwatched(showTraktId: Long, fromTime: Long, toTime: Long): Episode?

  suspend fun getTotalCount(showTraktId: Long, toTime: Long): Int

  suspend fun getTotalCount(showTraktId: Long): Int

  suspend fun getWatchedCount(showTraktId: Long, toTime: Long): Int

  suspend fun getWatchedCount(showTraktId: Long): Int

  suspend fun getAllWatchedForShows(showsIds: List<Long>): List<Episode>

  suspend fun getAllWatchedIdsForShows(showsIds: List<Long>): List<Long>

  suspend fun deleteAllUnwatchedForShow(showTraktId: Long)

  suspend fun deleteAllForShow(showTraktId: Long)
}
