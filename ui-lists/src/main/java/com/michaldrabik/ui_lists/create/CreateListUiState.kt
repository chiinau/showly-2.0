package com.michaldrabik.ui_lists.create

import com.michaldrabik.ui_base.utilities.Event
import com.michaldrabik.ui_model.CustomList

data class CreateListUiState(
  val listDetails: CustomList? = null,
  val isLoading: Boolean? = null,
  val onListUpdated: Event<CustomList>? = null,
)
