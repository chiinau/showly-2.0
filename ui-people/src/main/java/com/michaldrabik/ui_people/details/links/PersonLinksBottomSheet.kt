package com.michaldrabik.ui_people.details.links

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.michaldrabik.ui_base.BaseBottomSheetFragment
import com.michaldrabik.ui_base.utilities.extensions.onClick
import com.michaldrabik.ui_base.utilities.extensions.openWebUrl
import com.michaldrabik.ui_base.utilities.extensions.requireParcelable
import com.michaldrabik.ui_model.Ids
import com.michaldrabik.ui_model.Person
import com.michaldrabik.ui_navigation.java.NavigationArgs
import com.michaldrabik.ui_people.R
import com.michaldrabik.ui_people.databinding.ViewPersonLinksBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize

@AndroidEntryPoint
class PersonLinksBottomSheet : BaseBottomSheetFragment<PersonLinksViewModel>() {

  @Parcelize
  data class Options(
    val ids: Ids,
    val name: String,
    val website: String?,
  ) : Parcelable

  companion object {
    fun createBundle(person: Person): Bundle {
      val options = Options(person.ids, person.name, person.homepage)
      return bundleOf(NavigationArgs.ARG_OPTIONS to options)
    }
  }

  override val layoutResId = R.layout.view_person_links
  private val view by lazy { viewBinding as ViewPersonLinksBinding }

  private val options by lazy { requireParcelable<Options>(NavigationArgs.ARG_OPTIONS) }
  private val ids by lazy { options.ids }
  private val name by lazy { options.name }
  private val website by lazy { options.website }

  override fun getTheme(): Int = R.style.CustomBottomSheetDialog

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val contextThemeWrapper = ContextThemeWrapper(activity, R.style.AppTheme)
    val view = inflater.cloneInContext(contextThemeWrapper).inflate(layoutResId, container, false)
    return createViewBinding(ViewPersonLinksBinding.bind(view))
  }

  override fun createViewModel() = ViewModelProvider(this)[PersonLinksViewModel::class.java]

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupView()
  }

  private fun setupView() {
    with(view) {
      viewPersonLinksYouTube.onClick {
        openWebUrl("https://www.youtube.com/results?search_query=$name")
      }
      viewPersonLinksWiki.onClick {
        openWebUrl("https://en.wikipedia.org/w/index.php?search=$name")
      }
      viewPersonLinksGoogle.onClick {
        openWebUrl("https://www.google.com/search?q=$name")
      }
      viewPersonLinksDuckDuck.onClick {
        openWebUrl("https://duckduckgo.com/?q=$name")
      }
      viewPersonLinksTwitter.onClick {
        openWebUrl("https://twitter.com/search?q=$name&src=typed_query&f=user")
      }
      viewPersonLinksButtonClose.onClick { closeSheet() }
    }
    setWebLink()
    setTmdbLink()
    setImdbLink()
  }

  private fun setWebLink() {
    view.viewPersonLinksWebsite.run {
      if (website.isNullOrBlank()) {
        alpha = 0.5F
        isEnabled = false
      } else {
        onClick { openWebUrl(website ?: "") }
      }
    }
  }

  private fun setTmdbLink() {
    view.viewPersonLinksTmdb.run {
      if (ids.tmdb.id == -1L) {
        alpha = 0.5F
        isEnabled = false
      } else {
        onClick {
          openWebUrl("https://www.themoviedb.org/person/${ids.tmdb.id}")
        }
      }
    }
  }

  private fun setImdbLink() {
    view.viewPersonLinksImdb.run {
      if (ids.imdb.id.isBlank()) {
        alpha = 0.5F
        isEnabled = false
      } else {
        onClick {
          val i = Intent(Intent.ACTION_VIEW)
          i.data = Uri.parse("imdb:///name/${ids.imdb.id}")
          try {
            startActivity(i)
          } catch (e: ActivityNotFoundException) {
            // IMDb App not installed. Start in web browser
            openWebUrl("https://www.imdb.com/name/${ids.imdb.id}")
          }
        }
      }
    }
  }
}
