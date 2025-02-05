package com.michaldrabik.showly2.ui.main.cases.deeplink

import com.michaldrabik.data_local.LocalDataSource
import com.michaldrabik.data_remote.RemoteDataSource
import com.michaldrabik.repository.mappers.Mappers
import com.michaldrabik.repository.movies.MovieDetailsRepository
import com.michaldrabik.repository.shows.ShowDetailsRepository
import com.michaldrabik.showly2.utilities.deeplink.DeepLinkBundle
import com.michaldrabik.showly2.utilities.deeplink.DeepLinkResolver.Companion.TRAKT_TYPE_MOVIE
import com.michaldrabik.showly2.utilities.deeplink.DeepLinkResolver.Companion.TRAKT_TYPE_TV
import com.michaldrabik.ui_model.IdSlug
import javax.inject.Inject

class TraktDeepLinkCase @Inject constructor(
  private val remoteSource: RemoteDataSource,
  private val localSource: LocalDataSource,
  private val showDetailsRepository: ShowDetailsRepository,
  private val movieDetailsRepository: MovieDetailsRepository,
  private val mappers: Mappers
) {

  suspend fun findById(traktSlug: IdSlug, type: String) = when (type) {
    TRAKT_TYPE_TV -> {
      val localShow = showDetailsRepository.find(traktSlug)
      if (localShow != null) {
        DeepLinkBundle(show = localShow)
      }
      try {
        val show = remoteSource.trakt.fetchShow(traktSlug.id)
        val uiShow = mappers.show.fromNetwork(show)
        localSource.shows.upsert(listOf(mappers.show.toDatabase(uiShow)))
        DeepLinkBundle(show = uiShow)
      } catch (error: Throwable) {
        DeepLinkBundle.EMPTY
      }
    }
    TRAKT_TYPE_MOVIE -> {
      val localMovie = movieDetailsRepository.find(traktSlug)
      if (localMovie != null) {
        DeepLinkBundle(movie = localMovie)
      }
      try {
        val movie = remoteSource.trakt.fetchMovie(traktSlug.id)
        val uiMovie = mappers.movie.fromNetwork(movie)
        localSource.movies.upsert(listOf(mappers.movie.toDatabase(uiMovie)))
        DeepLinkBundle(movie = uiMovie)
      } catch (error: Throwable) {
        DeepLinkBundle.EMPTY
      }
    }
    else -> DeepLinkBundle.EMPTY
  }
}
