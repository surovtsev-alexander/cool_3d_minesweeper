/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.helpscreen.presentation

import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerLibraryInfo
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.surovtsev.core.ui.theme.MinesweeperTheme
import com.surovtsev.helpscreen.viewmodel.HelpScreenViewModel

@Composable
fun HelpScreen(
    viewModel: HelpScreenViewModel
) {
    HelpScreenControls(viewModel)
}

@Composable
fun HelpScreenControls(
    viewModel: HelpScreenViewModel
) {
    MinesweeperTheme {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            TutorialVideoPlayer(
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun TutorialVideoPlayer(
    viewModel: HelpScreenViewModel
) {
    val context = LocalContext.current

    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build()
    }

    val dataSourceFactory = DefaultDataSource
        .Factory(context)
    val source = ProgressiveMediaSource
        .Factory(dataSourceFactory)
        .createMediaSource(
            MediaItem.fromUri(
                Uri.parse(
                    HelpScreenViewModel
                        .TutorialVideoFile
                        .calculateUrl(context)
                )
            )
        )
    exoPlayer.setMediaSource(source)
    exoPlayer.prepare()

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            StyledPlayerView(context).apply {
                player = exoPlayer
                resizeMode = RESIZE_MODE_FIT
                exoPlayer.playWhenReady = true
            }
        }
    )
}
