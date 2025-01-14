package com.michaldrabik.ui_base

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.activity.addCallback
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import com.michaldrabik.common.Mode
import com.michaldrabik.ui_base.utilities.MessageEvent
import com.michaldrabik.ui_base.utilities.MessageEvent.Type.ERROR
import com.michaldrabik.ui_base.utilities.MessageEvent.Type.INFO
import com.michaldrabik.ui_base.utilities.NavigationHost
import com.michaldrabik.ui_base.utilities.SnackbarHost
import com.michaldrabik.ui_base.utilities.TipsHost
import com.michaldrabik.ui_base.utilities.extensions.showErrorSnackbar
import com.michaldrabik.ui_base.utilities.extensions.showInfoSnackbar
import com.michaldrabik.ui_model.Tip

abstract class BaseFragment<T : ViewModel>(@LayoutRes contentLayoutId: Int) :
  Fragment(contentLayoutId),
  TipsHost {

  protected abstract val viewModel: T
  protected var viewBinding: ViewBinding? = null
  open val navigationId: Int = 0

  protected var isInitialized = false

  protected val animations = mutableListOf<ViewPropertyAnimator?>()
  protected val animators = mutableListOf<Animator?>()
  protected val snackbars = mutableListOf<Snackbar?>()

  protected var mode: Mode
    get() = (requireActivity() as NavigationHost).getMode()
    set(value) = (requireActivity() as NavigationHost).setMode(value)

  protected val moviesEnabled: Boolean
    get() = (requireActivity() as NavigationHost).moviesEnabled()

  protected open fun createViewBinding(binding: ViewBinding): View? {
    viewBinding = binding
    return binding.root
  }

  override fun onResume() {
    super.onResume()
    setupBackPressed()
  }

  protected fun findNavControl() =
    (requireActivity() as NavigationHost).findNavControl()

  protected fun hideNavigation(animate: Boolean = true) =
    (requireActivity() as NavigationHost).hideNavigation(animate)

  protected fun showNavigation(animate: Boolean = true) =
    (requireActivity() as NavigationHost).showNavigation(animate)

  protected fun showSnack(message: MessageEvent) {
    message.consume()?.let {
      val host = (requireActivity() as SnackbarHost).provideSnackbarLayout()
      when (message.type) {
        INFO -> {
          val length = if (message.indefinite) LENGTH_INDEFINITE else LENGTH_SHORT
          val action = if (message.indefinite) ({}) else null
          host.showInfoSnackbar(getString(it), length = length, action = action)
        }
        ERROR -> host.showErrorSnackbar(getString(it))
      }
    }
  }

  protected open fun setupBackPressed() {
    val dispatcher = requireActivity().onBackPressedDispatcher
    dispatcher.addCallback(viewLifecycleOwner) {
      isEnabled = false
      findNavControl()?.popBackStack()
    }
  }

  protected fun navigateTo(@IdRes destination: Int, bundle: Bundle? = null) {
    findNavControl()?.navigate(destination, bundle)
  }

  override fun isTipShown(tip: Tip) = (requireActivity() as TipsHost).isTipShown(tip)

  override fun showTip(tip: Tip) = (requireActivity() as TipsHost).showTip(tip)

  override fun setTipShow(tip: Tip) = (requireActivity() as TipsHost).showTip(tip)

  private fun clearAnimations() {
    animations.forEach { it?.cancel() }
    animators.forEach { it?.cancel() }
    animations.clear()
    animators.clear()
  }

  override fun onDestroyView() {
    viewBinding = null
    snackbars.forEach { it?.dismiss() }
    clearAnimations()
    super.onDestroyView()
  }

  fun Fragment.requireAppContext(): Context = requireContext().applicationContext
}
