package com.dhkim.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingCommand
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest

fun String.profileImage(): Int {
    return when (this) {
        "0" -> R.drawable.ic_smile_blue
        "1" -> R.drawable.ic_smile_violet
        "2" -> R.drawable.ic_smile_green
        "3" -> R.drawable.ic_smile_orange
        else -> R.drawable.ic_smile_red
    }
}

fun Int.profileImage(): String {
    return when (this) {
        R.drawable.ic_smile_blue -> "0"
        R.drawable.ic_smile_violet -> "1"
        R.drawable.ic_smile_green -> "2"
        R.drawable.ic_smile_orange -> "3"
        else -> "0"
    }
}

interface RestartableSharingStarted : SharingStarted {
    fun restart()
}

interface RestartableStateFlow<out T> : StateFlow<T> {
    fun restart()
}

fun <T> Flow<T>.onetimeRestartableStateFlow(
    scope: CoroutineScope,
    initialValue: T,
    stopTimeOut: Long = 5_000,
    isOnetime: Boolean = true
): RestartableStateFlow<T> {
    val restarter = OneTimeSharingStarted(stopTimeOut = stopTimeOut, restartable = isOnetime)
    val stateFlow = stateIn(
        scope = scope,
        started = restarter,
        initialValue = initialValue
    )

    return object : RestartableStateFlow<T>, StateFlow<T> by stateFlow {
        override fun restart() {
            restarter.restart()
        }
    }
}

class OneTimeSharingStarted(
    private val stopTimeOut: Long,
    private val replayExpiration: Long = Long.MAX_VALUE,
    private val restartable: Boolean
) : RestartableSharingStarted {

    private val hasCollected: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val restartFlow: MutableSharedFlow<SharingCommand> =
        MutableSharedFlow(extraBufferCapacity = 2)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun command(subscriptionCount: StateFlow<Int>): Flow<SharingCommand> {
        return merge(
            restartFlow,
            subscriptionCount.transformLatest { count ->
                if (count > 0 && !hasCollected.value) {
                    emit(SharingCommand.START)
                    if (restartable) {
                        hasCollected.value = true
                    }
                } else {
                    delay(stopTimeOut)
                    emit(SharingCommand.STOP)
                    if (replayExpiration > 0) {
                        delay(replayExpiration)
                        emit(SharingCommand.STOP_AND_RESET_REPLAY_CACHE)
                    }
                }
            }.dropWhile {
                it != SharingCommand.START
            }.distinctUntilChanged()
        )
    }

    override fun restart() {
        restartFlow.tryEmit(SharingCommand.STOP_AND_RESET_REPLAY_CACHE)
        restartFlow.tryEmit(SharingCommand.START)
    }
}


