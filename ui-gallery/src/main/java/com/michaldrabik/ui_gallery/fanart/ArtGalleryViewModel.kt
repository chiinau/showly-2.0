package com.michaldrabik.ui_gallery.fanart

import androidx.lifecycle.viewModelScope
import com.michaldrabik.ui_base.BaseViewModel
import com.michaldrabik.ui_model.IdTrakt
import com.michaldrabik.ui_model.ImageFamily
import com.michaldrabik.ui_model.ImageStatus
import com.michaldrabik.ui_model.ImageType
import kotlinx.coroutines.launch
import javax.inject.Inject

class ArtGalleryViewModel @Inject constructor(
  private val imagesCase: ArtLoadImagesCase
) : BaseViewModel<ArtGalleryUiModel>() {

  fun loadImages(id: IdTrakt, family: ImageFamily, type: ImageType) {
    viewModelScope.launch {
      val image = imagesCase.loadInitialImage(id, family, type)
      if (image.status == ImageStatus.AVAILABLE) {
        uiState = ArtGalleryUiModel(listOf(image), type)
      }
      try {
        val allImages = imagesCase.loadAllImages(id, family, type, image)
        uiState = ArtGalleryUiModel(allImages, type)
      } catch (t: Throwable) {
        // NOOP Don't show rest of the gallery
      }
    }
  }
}