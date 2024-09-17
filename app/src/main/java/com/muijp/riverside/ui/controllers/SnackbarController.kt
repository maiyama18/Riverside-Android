package com.muijp.riverside.ui.controllers

import androidx.compose.material3.SnackbarDuration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Singleton

data class SnackbarAction(
    val label: String,
    val action: () -> Unit,
)

data class SnackbarEvent(
    val message: String,
    val duration: SnackbarDuration,
    val action: SnackbarAction?,
)

object SnackbarController {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val _snackbarEvents = MutableSharedFlow<SnackbarEvent>(replay = 0)
    val snackbarEvents: SharedFlow<SnackbarEvent> = _snackbarEvents

    fun present(
        message: String,
        duration: SnackbarDuration = SnackbarDuration.Short,
        action: SnackbarAction? = null,
    ) {
        coroutineScope.launch {
            _snackbarEvents.emit(SnackbarEvent(message, duration, action))
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
object SnackbarControllerModule {
    @Provides
    @Singleton
    fun provideSnackbarController(): SnackbarController = SnackbarController
}