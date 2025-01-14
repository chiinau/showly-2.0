package com.michaldrabik.showly2.ui.main.cases.deeplink

import com.michaldrabik.data_local.LocalDataSource
import com.michaldrabik.data_remote.RemoteDataSource
import com.michaldrabik.repository.mappers.Mappers
import com.michaldrabik.repository.movies.MovieDetailsRepository
import com.michaldrabik.repository.shows.ShowDetailsRepository
import com.michaldrabik.showly2.utilities.deeplink.DeepLinkBundle
import com.michaldrabik.showly2.utilities.deeplink.DeepLinkResolver.Companion.SOURCE_TMDB
import com.michaldrabik.showly2.utilities.deeplink.DeepLinkResolver.Companion.TMDB_TYPE_MOVIE
import com.michaldrabik.showly2.utilities.deeplink.DeepLinkResolver.Companion.TMDB_TYPE_TV
import com.michaldrabik.ui_model.IdTmdb
import javax.inject.Inject

class TmdbDeepLinkCase @Inject constructor(
  private val remoteSource: RemoteDataSource,
  private val localSource: LocalDataSource,
  private val showDetailsRepository: ShowDetailsRepository,
  private val movieDetailsRepository: MovieDetailsRepository,
  private val mappers: Mappers
) {

  suspend fun findById(tmdbId: IdTmdb, type: String): DeepLinkBundle {
    val localShow = showDetailsRepository.find(tmdbId)
    if (localShow != null && type == TMDB_TYPE_TV) {
      return DeepLinkBundle(show = localShow)
    }

    val localMovie = movieDetailsRepository.find(tmdbId)
    if (localMovie != null && type == TMDB_TYPE_MOVIE) {
      return DeepLinkBundle(movie = localMovie)
    }

    val searchResult = remoteSource.trakt.fetchSearchId(SOURCE_TMDB, tmdbId.id.toString())
    if (searchResult.isNotEmpty()) {
      searchResult
        .filter { it.show != null || it.movie != null }
        .forEach { result ->
          val show = result.show
          val movie = result.movie
          if (show != null && type == TMDB_TYPE_TV) {
            val uiShow = mappers.show.fromNetwork(show)
            localSource.shows.upsert(listOf(mappers.show.toDatabase(uiShow)))
            return DeepLinkBundle(show = uiShow)
          }
          if (movie != null && type == TMDB_TYPE_MOVIE) {
            val uiMovie = mappers.movie.fromNetwork(movie)
            localSource.movies.upsert(listOf(mappers.movie.toDatabase(uiMovie)))
            return DeepLinkBundle(movie = uiMovie)
          }
        }
    }

    return DeepLinkBundle.EMPTY
  }
}
