package com.michaldrabik.ui_base.viewmodel

import com.michaldrabik.ui_base.utilities.Event
import com.michaldrabik.ui_base.utilities.MessageEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow

interface ChannelsDelegate {

  val messageFlow: Flow<MessageEvent>
  val messageChannel: Channel<MessageEvent>

  val eventFlow: Flow<Event<*>>
  val eventChannel: Channel<Event<*>>
}
