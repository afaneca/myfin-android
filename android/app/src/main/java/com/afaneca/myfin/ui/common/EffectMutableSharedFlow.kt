package com.afaneca.myfin.ui.common

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

fun<T> EffectMutableSharedFlow(
    replay: Int = 0,
    extraBufferCapacity: Int = 32,
    onBufferOverflow: BufferOverflow = BufferOverflow.DROP_OLDEST,
): MutableSharedFlow<T> {
    return MutableSharedFlow(replay, extraBufferCapacity, onBufferOverflow)
}
