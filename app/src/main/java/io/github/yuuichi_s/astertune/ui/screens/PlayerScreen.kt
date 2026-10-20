package dev.bitmystic.echopod.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import dev.bitmystic.echopod.constants.UI_DEBUG
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.bitmystic.echopod.LocalPlayerConnection
import dev.bitmystic.echopod.LocalTabMode
import dev.bitmystic.echopod.constants.DEFAULT_PLAYER_BACKGROUND
import dev.bitmystic.echopod.constants.MiniPlayerHeight
import dev.bitmystic.echopod.constants.PlayerBackgroundStyleKey
import dev.bitmystic.echopod.constants.ShowLyricsKey
import dev.bitmystic.echopod.extensions.usesLandscapePlayer
import dev.bitmystic.echopod.ui.component.expandedAnchor
import dev.bitmystic.echopod.ui.component.rememberBottomSheetState
import dev.bitmystic.echopod.ui.player.LandscapePlayer
import dev.bitmystic.echopod.ui.player.PlayerBackground
import dev.bitmystic.echopod.ui.player.PortraitPlayer
import dev.bitmystic.echopod.ui.theme.rememberUseDarkTheme
import dev.bitmystic.echopod.utils.rememberEnumPreference
import dev.bitmystic.echopod.utils.rememberPreference

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 0.dp,
) {
    val TAG = "PlayerScreen"

    val playerConnection = LocalPlayerConnection.current ?: return
    val queueBoard by playerConnection.queueBoard.collectAsState()

    val playerBackground by rememberEnumPreference(
        key = PlayerBackgroundStyleKey,
        defaultValue = DEFAULT_PLAYER_BACKGROUND
    )

    val useDarkTheme = rememberUseDarkTheme()

    val showLyrics by rememberPreference(ShowLyricsKey, defaultValue = false)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomPadding)
//            .background(MaterialTheme.colorScheme.surface)
    ) {
        PlayerBackground(
            playerConnection = playerConnection,
            playerBackground = playerBackground,
            showLyrics = showLyrics,
            useDarkTheme = useDarkTheme,
        )
        if (UI_DEBUG) Log.v(TAG, "PLR-3.0")

        val state = rememberBottomSheetState(
            dismissedBound = 0.dp,
            expandedBound = maxHeight,
            collapsedBound = MiniPlayerHeight,
            initialAnchor = expandedAnchor,
        )

        if (usesLandscapePlayer(LocalConfiguration.current, LocalTabMode.current)) {
            LandscapePlayer(state, navController, queueBoard)
        } else {
            PortraitPlayer(state, navController, queueBoard)
        }
    }
}
