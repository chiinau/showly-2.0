package com.michaldrabik.ui_progress_movies.progress

import com.michaldrabik.ui_base.UiModel
import com.michaldrabik.ui_model.SortOrder
import com.michaldrabik.ui_progress_movies.ProgressMovieItem

data class ProgressMoviesMainUiModel(
  val items: List<ProgressMovieItem>? = null,
  val isSearching: Boolean? = null,
  val resetScroll: Boolean? = null,
  val sortOrder: SortOrder? = null
) : UiModel() {

  override fun update(newModel: UiModel) =
    (newModel as ProgressMoviesMainUiModel).copy(
      items = newModel.items?.toList() ?: items,
      isSearching = newModel.isSearching ?: isSearching,
      resetScroll = newModel.resetScroll ?: resetScroll,
      sortOrder = newModel.sortOrder ?: sortOrder
    )
}