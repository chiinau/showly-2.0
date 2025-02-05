package com.michaldrabik.ui_base.common.sheets.sort_order

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import com.michaldrabik.ui_base.BaseBottomSheetFragment
import com.michaldrabik.ui_base.R
import com.michaldrabik.ui_base.common.sheets.sort_order.views.SortOrderItemView
import com.michaldrabik.ui_base.databinding.ViewSortOrderBinding
import com.michaldrabik.ui_base.utilities.extensions.onClick
import com.michaldrabik.ui_base.utilities.extensions.requireSerializable
import com.michaldrabik.ui_base.utilities.extensions.requireString
import com.michaldrabik.ui_base.utilities.extensions.requireStringArray
import com.michaldrabik.ui_model.SortOrder
import com.michaldrabik.ui_model.SortType
import com.michaldrabik.ui_navigation.java.NavigationArgs.ARG_REQUEST_KEY
import com.michaldrabik.ui_navigation.java.NavigationArgs.ARG_SELECTED_SORT_ORDER
import com.michaldrabik.ui_navigation.java.NavigationArgs.ARG_SELECTED_SORT_TYPE
import com.michaldrabik.ui_navigation.java.NavigationArgs.ARG_SORT_ORDERS
import com.michaldrabik.ui_navigation.java.NavigationArgs.REQUEST_SORT_ORDER
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SortOrderBottomSheet : BaseBottomSheetFragment<SortOrderViewModel>() {

  companion object {
    fun createBundle(
      options: List<SortOrder>,
      selectedOrder: SortOrder,
      selectedType: SortType,
      requestKey: String = REQUEST_SORT_ORDER
    ) = bundleOf(
      ARG_SORT_ORDERS to options.map { it.name },
      ARG_SELECTED_SORT_ORDER to selectedOrder,
      ARG_SELECTED_SORT_TYPE to selectedType,
      ARG_REQUEST_KEY to requestKey
    )
  }

  override val layoutResId = R.layout.view_sort_order
  private val view by lazy { viewBinding as ViewSortOrderBinding }

  private val requestKey by lazy { requireString(ARG_REQUEST_KEY, default = REQUEST_SORT_ORDER) }
  private val initialSortOrder by lazy { requireSerializable<SortOrder>(ARG_SELECTED_SORT_ORDER) }
  private val initialSortType by lazy { requireSerializable<SortType>(ARG_SELECTED_SORT_TYPE) }
  private val initialOptions by lazy { requireStringArray(ARG_SORT_ORDERS).map { SortOrder.valueOf(it) } }

  private lateinit var selectedSortOrder: SortOrder
  private lateinit var selectedSortType: SortType

  override fun getTheme(): Int = R.style.CustomBottomSheetDialog

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val contextThemeWrapper = ContextThemeWrapper(activity, R.style.AppTheme)
    val view = inflater.cloneInContext(contextThemeWrapper).inflate(layoutResId, container, false)
    return createViewBinding(ViewSortOrderBinding.bind(view))
  }

  override fun createViewModel() = ViewModelProvider(this)[SortOrderViewModel::class.java]

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    selectedSortOrder = initialSortOrder
    selectedSortType = initialSortType
    setupView()
  }

  @SuppressLint("SetTextI18n")
  private fun setupView() {
    with(view) {
      viewSortOrderItemsLayout.removeAllViews()
      initialOptions.forEach { item ->
        val itemView = SortOrderItemView(requireContext()).apply {
          onItemClickListener = itemClickListener
          bind(item, initialSortType, item == initialSortOrder)
        }
        viewSortOrderItemsLayout.addView(itemView)
      }

      viewSortOrderButtonApply.onClick { onApplySortOrder() }
    }
  }

  private fun onItemClicked(
    sortOrder: SortOrder,
    sortType: SortType
  ) {
    view.viewSortOrderItemsLayout.children.forEach { child ->
      with(child as SortOrderItemView) {
        if (sortOrder == child.sortOrder) {
          if (sortOrder == selectedSortOrder) {
            val newSortType = if (sortType == SortType.ASCENDING) SortType.DESCENDING else SortType.ASCENDING
            selectedSortType = newSortType
            bind(sortOrder, newSortType, true, animate = true)
          } else {
            bind(sortOrder, selectedSortType, true)
          }
          selectedSortOrder = sortOrder
        } else {
          bind(child.sortOrder, child.sortType, false)
        }
      }
    }
  }

  private fun onApplySortOrder() {
    if (selectedSortOrder != initialSortOrder || initialSortType != selectedSortType) {
      val result = bundleOf(
        ARG_SELECTED_SORT_ORDER to selectedSortOrder,
        ARG_SELECTED_SORT_TYPE to selectedSortType,
      )
      setFragmentResult(requestKey, result)
    }
    closeSheet()
  }

  private val itemClickListener: (SortOrder, SortType) -> Unit =
    { sortOrder, sortType -> onItemClicked(sortOrder, sortType) }
}
