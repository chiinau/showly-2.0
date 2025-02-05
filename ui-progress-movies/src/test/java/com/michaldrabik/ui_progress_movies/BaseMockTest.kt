package com.michaldrabik.ui_progress_movies

import io.mockk.MockKAnnotations
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before

@Suppress("EXPERIMENTAL_API_USAGE")
abstract class BaseMockTest {

  protected val testDispatcher = TestCoroutineDispatcher()

  @Before
  open fun setUp() {
    Dispatchers.setMain(testDispatcher)
    MockKAnnotations.init(this)
    mockkStatic("androidx.room.RoomDatabaseKt")
  }
}
