package com.michaldrabik.ui_gallery.fanart

import com.michaldrabik.common.Config.FANART_GALLERY_IMAGES_LIMIT
import com.michaldrabik.common.di.AppScope
import com.michaldrabik.ui_base.images.MovieImagesProvider
import com.michaldrabik.ui_base.images.ShowImagesProvider
import com.michaldrabik.ui_model.IdTrakt
import com.michaldrabik.ui_model.Image
import com.michaldrabik.ui_model.ImageFamily
import com.michaldrabik.ui_model.ImageFamily.MOVIE
import com.michaldrabik.ui_model.ImageFamily.SHOW
import com.michaldrabik.ui_model.ImageStatus.AVAILABLE
import com.michaldrabik.ui_model.ImageType
import com.michaldrabik.ui_repository.movies.MoviesRepository
import com.michaldrabik.ui_repository.shows.ShowsRepository
import javax.inject.Inject

@AppScope
class ArtLoadImagesCase @Inject constructor(
  private val showsRepository: ShowsRepository,
  private val moviesRepository: MoviesRepository,
  private val showImagesProvider: ShowImagesProvider,
  private val movieImagesProvider: MovieImagesProvider
) {

  suspend fun loadInitialImage(id: IdTrakt, family: ImageFamily, type: ImageType) =
    when (family) {
      SHOW -> {
        val show = showsRepository.detailsShow.load(id)
        showImagesProvider.findCachedImage(show, type)
      }
      MOVIE -> {
        val movie = moviesRepository.movieDetails.load(id)
        movieImagesProvider.findCachedImage(movie, type)
      }
      else -> throw IllegalStateException()
    }

  suspend fun loadAllImages(
    id: IdTrakt,
    family: ImageFamily,
    type: ImageType,
    initialImage: Image
  ): List<Image> {
    val images = mutableListOf<Image>()
    if (initialImage.status == AVAILABLE) {
      images.add(initialImage)
    }

    var remoteImages: List<Image> = emptyList()
    if (family == SHOW) {
      val show = showsRepository.detailsShow.load(id)
      remoteImages = showImagesProvider.loadRemoteImages(show, type)
    } else if (family == MOVIE) {
      val movie = moviesRepository.movieDetails.load(id)
      remoteImages = movieImagesProvider.loadRemoteImages(movie, type)
    }
    images.addAll(remoteImages.filter { it.fileUrl != initialImage.fileUrl })
    return images.take(FANART_GALLERY_IMAGES_LIMIT)
  }
}