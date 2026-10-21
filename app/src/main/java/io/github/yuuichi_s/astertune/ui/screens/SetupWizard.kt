/*
 * Copyright (C) 2025 O​u​t​er​Tu​ne Project
 * Copyright (C) 2026 AsterTune Project
 *
 * SPDX-License-Identifier: GPL-3.0
 *
 * For any other attributions, refer to the git commit history
 */

package io.github.yuuichi_s.astertune.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.NavigateBefore
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.SdCard
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavController
import io.github.yuuichi_s.astertune.BuildConfig
import io.github.yuuichi_s.astertune.LocalDownloadUtil
import io.github.yuuichi_s.astertune.R
import io.github.yuuichi_s.astertune.constants.AutomaticScannerKey
import io.github.yuuichi_s.astertune.constants.DEFAULT_ENABLED_FILTERS
import io.github.yuuichi_s.astertune.constants.DEFAULT_ENABLED_TABS
import io.github.yuuichi_s.astertune.constants.DownloadPathKey
import io.github.yuuichi_s.astertune.constants.EnabledFiltersKey
import io.github.yuuichi_s.astertune.constants.EnabledTabsKey
import io.github.yuuichi_s.astertune.constants.InnerTubeCookieKey
import io.github.yuuichi_s.astertune.constants.LibraryFilterKey
import io.github.yuuichi_s.astertune.constants.LocalLibraryEnableKey
import io.github.yuuichi_s.astertune.constants.LyricTrimKey
import io.github.yuuichi_s.astertune.constants.MaxSongCacheSizeKey
import io.github.yuuichi_s.astertune.constants.NavigationBarHeight
import io.github.yuuichi_s.astertune.constants.OOBE_VERSION
import io.github.yuuichi_s.astertune.constants.OobeStatusKey
import io.github.yuuichi_s.astertune.constants.ScanPathsKey
import io.github.yuuichi_s.astertune.constants.ThumbnailCornerRadius
import io.github.yuuichi_s.astertune.ui.component.ListPreference
import io.github.yuuichi_s.astertune.ui.component.PreferenceEntry
import io.github.yuuichi_s.astertune.ui.component.PreferenceGroupTitle
import io.github.yuuichi_s.astertune.ui.component.SwitchPreference
import io.github.yuuichi_s.astertune.ui.component.button.IconLabelButton
import io.github.yuuichi_s.astertune.ui.dialog.ActionPromptDialog
import io.github.yuuichi_s.astertune.ui.dialog.InfoLabel
import io.github.yuuichi_s.astertune.ui.screens.Screens.LibraryFilter
import io.github.yuuichi_s.astertune.ui.screens.settings.fragments.AccountFrag
import io.github.yuuichi_s.astertune.ui.screens.settings.fragments.LocalScannerFrag
import io.github.yuuichi_s.astertune.ui.screens.settings.fragments.LocalizationFrag
import io.github.yuuichi_s.astertune.ui.screens.settings.fragments.ThemeAppFrag
import io.github.yuuichi_s.astertune.utils.dlCoroutine
import io.github.yuuichi_s.astertune.utils.formatFileSize
import io.github.yuuichi_s.astertune.utils.rememberEnumPreference
import io.github.yuuichi_s.astertune.utils.rememberPreference
import io.github.yuuichi_s.astertune.utils.scanners.stringFromUriList
import io.github.yuuichi_s.astertune.utils.scanners.uriListFromString
import com.zionhuang.innertube.utils.parseCookieString
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val OobeColorScheme = darkColorScheme(
    primary = Color(0xFFE8E8EC),
    onPrimary = Color(0xFF17181B),
    secondary = Color(0xFFBFC0C7),
    onSecondary = Color(0xFF1B1C20),
    tertiary = Color(0xFFD5D5DA),
    onTertiary = Color(0xFF1B1C20),
    background = Color(0xFF0A0B0D),
    onBackground = Color(0xFFF5F5F7),
    surface = Color(0xFF141519),
    onSurface = Color(0xFFF5F5F7),
    surfaceVariant = Color(0xFF202126),
    onSurfaceVariant = Color(0xFFB8B9C0),
    outline = Color(0xFF55565E),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupWizard(
    navController: NavController,
) {
    val typography = MaterialTheme.typography
    MaterialTheme(
        colorScheme = OobeColorScheme,
        typography = typography,
    ) {
        SetupWizardContent(navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetupWizardContent(
    navController: NavController,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val layoutDirection = LocalLayoutDirection.current
    val uriHandler = LocalUriHandler.current

    var oobeStatus by rememberPreference(OobeStatusKey, defaultValue = 0)

    // content prefs
    var filter by rememberEnumPreference(LibraryFilterKey, LibraryFilter.ALL)


    val innerTubeCookie by rememberPreference(InnerTubeCookieKey, "")
    val isLoggedIn = remember(innerTubeCookie) {
        "SAPISID" in parseCookieString(innerTubeCookie)
    }
    val (ytmSync, onYtmSyncChange) = rememberPreference(LyricTrimKey, defaultValue = true)

    // local media prefs
    val (localLibEnable, onLocalLibEnableChange) = rememberPreference(LocalLibraryEnableKey, defaultValue = true)
    val (autoScan, onAutoScanChange) = rememberPreference(AutomaticScannerKey, defaultValue = true)
    val (enabledTabs, onEnabledTabsChange) = rememberPreference(EnabledTabsKey, defaultValue = DEFAULT_ENABLED_TABS)
    val (enabledFilters, onEnabledFiltersChange) = rememberPreference(EnabledFiltersKey, defaultValue = DEFAULT_ENABLED_FILTERS)

    LaunchedEffect(localLibEnable) {
        var containsFolders = enabledTabs.contains('F')
        if (localLibEnable && !containsFolders) {
            onEnabledTabsChange(enabledTabs + "F")
        } else if (!localLibEnable && containsFolders) {
            onEnabledTabsChange(enabledTabs.filterNot { it == 'F' })
        }

        containsFolders = enabledFilters.contains('F')
        if (!localLibEnable && containsFolders) {
            onEnabledFiltersChange(enabledFilters.filterNot { it == 'F' })
        }
    }

    BackHandler {
        if (oobeStatus > 0) {
            oobeStatus -= 1
        } else {
            // user may not dismiss via back
        }
    }

    val finishSetup: () -> Unit = {
        oobeStatus = OOBE_VERSION
        navController.navigateUp()
    }

    val navBar = @Composable {
        // nav bar
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.09f), Color.White.copy(alpha = 0.035f))
                    )
                )
                .border(
                    1.dp,
                    Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.17f), Color.White.copy(alpha = 0.045f))
                    ),
                    RoundedCornerShape(28.dp)
                )
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    if (oobeStatus > 0) {
                        oobeStatus -= 1
                    }
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                }
            ) {
                Text(
                    text = stringResource(R.string.action_back),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.NavigateBefore,
                    contentDescription = null
                )
            }

            LinearProgressIndicator(
                progress = { oobeStatus.toFloat() / (OOBE_VERSION - 1) },
//                color = ProgressIndicatorDefaults.linearColor,
//                trackColor = MaterialTheme.colorScheme.primary,
                strokeCap = StrokeCap.Butt,
                drawStopIndicator = {},
                modifier = Modifier
                    .weight(1f, false)
                    .height(8.dp)  // Height of the progress bar
                    .padding(2.dp),  // Add some padding at the top
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    if (oobeStatus == 1) {
                        filter = LibraryFilter.ALL // hax
                    }

                    if (oobeStatus < OOBE_VERSION) {
                        oobeStatus += 1
                    }

                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.NavigateNext,
                    contentDescription = null
                )
                Text(
                    text = stringResource(R.string.action_next),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                )
            }
        }
    }

    Scaffold(
        bottomBar = {
            when (oobeStatus) {
                0 -> OobeWelcomeFooter(
                    onBackup = { navController.navigate("settings/backup_restore") },
                    onSkip = finishSetup,
                    onContinue = {
                        oobeStatus = 1
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    },
                )
                OOBE_VERSION - 1 -> OobeFinishFooter(onStartListening = finishSetup)
                else -> Box(
                    Modifier
                        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
                        .fillMaxWidth()
                ) {
                    navBar()
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(
                    PaddingValues(
                        start = paddingValues.calculateStartPadding(layoutDirection),
                        top = 0.dp,
                        end = paddingValues.calculateEndPadding(layoutDirection),
                        bottom = paddingValues.calculateBottomPadding()
                    )
                )
                .fillMaxSize()
        ) {
            OobeBackdrop(Modifier.matchParentSize())
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 12.dp))

                when (oobeStatus) {
                    0 -> { // landing page
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .size(112.dp)
                                .shadow(24.dp, CircleShape, spotColor = Color.White.copy(alpha = 0.12f))
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(Color.White.copy(alpha = 0.11f), Color.White.copy(alpha = 0.025f))
                                    )
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.16f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.launcher_monochrome),
                                contentDescription = stringResource(R.string.app_name),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground, BlendMode.SrcIn),
                                modifier = Modifier.size(78.dp),
                            )
                        }

                        Text(
                            text = stringResource(R.string.oobe_welcome_message),
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            textAlign = TextAlign.Center,
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            OobeInfoPill("v${BuildConfig.VERSION_NAME}")
                            OobeInfoPill(BuildConfig.FLAVOR.replaceFirstChar { it.uppercase() })
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 4.dp, top = 28.dp, end = 4.dp, bottom = 8.dp)
                        ) {
                            OobeFeatureRow(
                                title = stringResource(R.string.oobe_ytm_integration),
                                description = stringResource(R.string.oobe_ytm_integration_description),
                                icon = Icons.Rounded.MusicNote,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            OobeFeatureRow(
                                title = stringResource(R.string.oobe_ad_free_exp),
                                description = stringResource(R.string.oobe_ad_free_exp_description),
                                icon = Icons.Rounded.Block,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            OobeFeatureRow(
                                title = stringResource(R.string.oobe_cross_platform_sync),
                                description = stringResource(R.string.oobe_cross_platform_sync_description),
                                icon = Icons.Rounded.Sync,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            OobeFeatureRow(
                                title = stringResource(R.string.oobe_local_music_support),
                                description = stringResource(R.string.oobe_local_music_support_description),
                                icon = Icons.Rounded.SdCard,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        OobeGlassCard(modifier = Modifier.padding(horizontal = 4.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(end = 12.dp),
                                )
                                Text(
                                    text = stringResource(R.string.oobe_welcome_tip),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    // appearance
                    1 -> {
                        OobePageHeader(
                            icon = Icons.Rounded.DarkMode,
                            title = stringResource(R.string.grp_interface),
                            subtitle = stringResource(R.string.oobe_interface_subtitle),
                        )

                        OobeGlassCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ThemeAppFrag()
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OobeGlassCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            LocalizationFrag()
                        }
                    }

                    // account
                    2 -> {
                        OobePageHeader(
                            icon = Icons.Rounded.AccountCircle,
                            title = stringResource(R.string.oobe_ytm_logon_title),
                            subtitle = stringResource(R.string.oobe_ytm_logon_subtitle),
                        )

                        OobeGlassCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AccountFrag(navController)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OobeGlassCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SwitchPreference(
                                title = { Text(stringResource(R.string.ytm_sync)) },
                                icon = { Icon(Icons.Rounded.Lyrics, null) },
                                checked = ytmSync,
                                onCheckedChange = onYtmSyncChange,
                                isEnabled = isLoggedIn
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = finishSetup,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 54.dp),
                            shape = RoundedCornerShape(20.dp),
                        ) {
                            Text(
                                text = if (isLoggedIn) {
                                    stringResource(R.string.oobe_start_listening)
                                } else {
                                    stringResource(R.string.oobe_skip_to_listening)
                                },
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    // local media
                    3 -> {
                        OobePageHeader(
                            icon = Icons.Rounded.LibraryMusic,
                            title = stringResource(R.string.oobe_local_media_title),
                            subtitle = stringResource(R.string.oobe_local_media_subtitle),
                        )

                        OobeGlassCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SwitchPreference(
                                title = { Text(stringResource(R.string.local_library_enable_title)) },
                                description = stringResource(R.string.local_library_enable_description),
                                icon = { Icon(Icons.Rounded.SdCard, null) },
                                checked = localLibEnable,
                                onCheckedChange = onLocalLibEnableChange
                            )
                        }

                        AnimatedVisibility(localLibEnable) {
                            Column {
                                Spacer(modifier = Modifier.height(16.dp))
                                OobeGlassCard(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    SwitchPreference(
                                        title = { Text(stringResource(R.string.auto_scanner_title)) },
                                        description = stringResource(R.string.auto_scanner_description),
                                        icon = { Icon(Icons.Rounded.Autorenew, null) },
                                        checked = autoScan,
                                        onCheckedChange = onAutoScanChange
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                OobeGlassCard(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    PreferenceGroupTitle(
                                        title = stringResource(R.string.grp_manual_scanner)
                                    )


                                    LocalScannerFrag()
                                }
                            }

                        }
                    }

                    // downloads
                    4 -> {
                        val downloadUtil = LocalDownloadUtil.current
                        val (downloadPath, onDownloadPathChange) = rememberPreference(DownloadPathKey, "")
                        val (maxSongCacheSize, onMaxSongCacheSizeChange) = rememberPreference(
                            key = MaxSongCacheSizeKey,
                            defaultValue = 0
                        )
                        val (scanPaths, onScanPathsChange) = rememberPreference(ScanPathsKey, defaultValue = "")

                        var showDlPathDialog: Boolean by remember {
                            mutableStateOf(false)
                        }


                        OobePageHeader(
                            icon = Icons.Rounded.Download,
                            title = stringResource(R.string.oobe_downloads_title),
                            subtitle = stringResource(R.string.oobe_downloads_subtitle),
                        )

                        OobeGlassCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            PreferenceEntry(
                                title = { Text(stringResource(R.string.dl_main_path_title)) },
                                onClick = {
                                    showDlPathDialog = true
                                },
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        InfoLabel(stringResource(R.string.dl_oobe_tooltip))

                        Spacer(Modifier.height(24.dp))
                        OobePageHeader(
                            icon = Icons.Rounded.Cached,
                            title = stringResource(R.string.oobe_cache_title),
                            subtitle = stringResource(R.string.oobe_cache_subtitle),
                        )

                        OobeGlassCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ListPreference(
                                title = { Text(stringResource(R.string.max_cache_size)) },
                                selectedValue = maxSongCacheSize,
                                values = listOf(0, 128, 256, 512, 1024, 2048, 4096, 8192, -1),
                                valueText = {
                                    when (it) {
                                        0 -> stringResource(R.string.off)
                                        -1 -> stringResource(R.string.unlimited)
                                        else -> formatFileSize(it * 1024 * 1024L)
                                    }
                                },
                                onValueSelected = onMaxSongCacheSizeChange
                            )
                            InfoLabel(stringResource(R.string.restart_to_apply_changes))
                            Spacer(Modifier.height(12.dp))
                        }

                        if (showDlPathDialog) {
                            var tempFilePath by remember {
                                mutableStateOf<Uri?>(null)
                            }
                            LaunchedEffect(downloadPath) {
                                tempFilePath = uriListFromString(downloadPath).firstOrNull()
                            }

                            ActionPromptDialog(
                                titleBar = {
                                    Text(
                                        text = stringResource(R.string.dl_main_path_title),
                                        style = MaterialTheme.typography.titleLarge,
                                    )
                                },
                                onDismiss = {
                                    showDlPathDialog = false
                                    tempFilePath = null
                                },
                                onConfirm = {
                                    tempFilePath?.let { f ->
                                        val uris = stringFromUriList(listOfNotNull(f))
                                        onDownloadPathChange(uris)
                                    }

                                    showDlPathDialog = false
                                    tempFilePath = null

                                    coroutineScope.launch(dlCoroutine) {
                                        delay(1000)
                                        downloadUtil.cd()
                                        downloadUtil.scanDownloads()
                                    }
                                },
                                onReset = {
                                    tempFilePath = null
                                },
                                onCancel = {
                                    showDlPathDialog = false
                                    tempFilePath = null
                                },
                                isInputValid = uriListFromString(scanPaths).none {
                                    // download path cannot a scan path, or a subdir of a scan path
                                    tempFilePath.toString().length <= it.toString().length && tempFilePath.toString()
                                        .contains(it.toString())
                                },
                                modifier = Modifier
                                    .verticalScroll(rememberScrollState()),
                            ) {

                                val dirPickerLauncher = rememberLauncherForActivityResult(
                                    ActivityResultContracts.OpenDocumentTree()
                                ) { uri ->
                                    if (tempFilePath.toString() == uri.toString()) return@rememberLauncherForActivityResult
                                    if (uri?.path != null) {
                                        // Take persistable URI permission
                                        val contentResolver = context.contentResolver
                                        val takeFlags: Int =
                                            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                        contentResolver.takePersistableUriPermission(uri, takeFlags)

                                        tempFilePath = uri
                                    }
                                }

                                val valid = uriListFromString(scanPaths).none {
                                    // download path cannot a scan path, or a subdir of a scan path
                                    tempFilePath.toString().length <= it.toString().length && tempFilePath.toString()
                                        .contains(it.toString())
                                }

                                Text(
                                    text = stringResource(R.string.dl_main_path_description),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                                Spacer(Modifier.padding(vertical = 8.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .border(
                                            2.dp,
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                            RoundedCornerShape(ThumbnailCornerRadius)
                                        )
                                        .background(if (valid) Color.Transparent else MaterialTheme.colorScheme.errorContainer)
                                ) {
                                    tempFilePath?.let {
                                        Text(
                                            text = it.toString(),
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }

                                // add folder button
                                Column {
                                    Button(onClick = { dirPickerLauncher.launch(null) }) {
                                        Text(stringResource(R.string.scan_paths_add_folder))
                                    }

                                    InfoLabel(
                                        text = stringResource(R.string.scan_paths_tooltip),
                                        modifier = Modifier.padding(vertical = 16.dp)
                                    )

                                    if (!valid) {
                                        InfoLabel(
                                            text = stringResource(R.string.scanner_rejected_dir),
                                            isError = true,
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // exit page
                    5 -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(92.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .border(1.dp, Color.White.copy(alpha = 0.14f), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(44.dp),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                            Text(
                                text = stringResource(R.string.oobe_complete_title),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                            )
                            Text(
                                text = stringResource(R.string.oobe_complete),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                            )
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 16.dp)
                            ) {
                                IconLabelButton(
                                    text = "GitHub",
                                    icon = Icons.Rounded.Code,
                                    onClick = { uriHandler.openUri("https://github.com/bitmystic-dev/Echopod-Music") },
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                            Text(
                                text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) · ${BuildConfig.FLAVOR.replaceFirstChar { it.uppercase() }}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(NavigationBarHeight))
            }

        }
    }
}


@Composable
private fun OobeFeatureRow(title: String, description: String?, icon: ImageVector, tint: Color) {
    OobeGlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(27.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun OobePageHeader(icon: ImageVector, title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.075f))
                .border(1.dp, Color.White.copy(alpha = 0.13f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(31.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
    }
}

@Composable
private fun OobeGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(26.dp)
    Column(
        modifier = modifier
            .shadow(18.dp, shape, ambientColor = Color.Black.copy(alpha = 0.28f))
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.085f),
                        Color.White.copy(alpha = 0.035f),
                        Color.White.copy(alpha = 0.055f),
                    )
                )
            )
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(Color.White.copy(alpha = 0.18f), Color.White.copy(alpha = 0.045f))
                ),
                shape,
            ),
        content = content,
    )
}

@Composable
private fun OobeInfoPill(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.055f))
            .border(1.dp, Color.White.copy(alpha = 0.10f), CircleShape)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}

@Composable
private fun OobeBackdrop(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF17181B), Color(0xFF0C0D10), Color(0xFF090A0C))
                    )
                )
        )
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .offset(x = 96.dp, y = (-74).dp)
                .size(250.dp)
                .blur(44.dp)
                .background(
                    Brush.radialGradient(listOf(Color.White.copy(alpha = 0.12f), Color.Transparent)),
                    CircleShape,
                )
        )
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-108).dp, y = 80.dp)
                .size(260.dp)
                .blur(48.dp)
                .background(
                    Brush.radialGradient(listOf(Color(0xFFBFC0C7).copy(alpha = 0.085f), Color.Transparent)),
                    CircleShape,
                )
        )
    }
}

@Composable
private fun OobeWelcomeFooter(
    onBackup: () -> Unit,
    onSkip: () -> Unit,
    onContinue: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onBackup) {
                Text(stringResource(R.string.oobe_use_backup))
            }
            TextButton(onClick = onSkip) {
                Text(stringResource(R.string.action_skip))
            }
        }
        OobeSlideToContinue(onContinue)
    }
}

@Composable
private fun OobeSlideToContinue(onContinue: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    var dragOffsetPx by remember { mutableStateOf(0f) }
    val arrowTransition = rememberInfiniteTransition(label = "onboarding-slide-arrows")
    val arrowProgress by arrowTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "onboarding-slide-arrow-motion",
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(Color.White.copy(alpha = 0.10f), Color.White.copy(alpha = 0.04f))
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.17f), CircleShape),
    ) {
        val handleSize = 56.dp
        val maxDragPx = with(density) { (maxWidth - handleSize - 12.dp).toPx().coerceAtLeast(0f) }
        val safeDragOffset = dragOffsetPx.coerceIn(0f, maxDragPx)

        Text(
            text = stringResource(R.string.oobe_slide_to_continue),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.Center),
        )
        Text(
            text = "›  ›  ›",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White.copy(alpha = 0.2f + (1f - arrowProgress) * 0.35f),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 54.dp)
                .offset(x = (arrowProgress * 8).dp),
        )
        Icon(
            imageVector = Icons.Rounded.Headphones,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 18.dp)
                .size(21.dp),
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset { IntOffset(safeDragOffset.roundToInt(), 0) }
                .padding(6.dp)
                .size(handleSize - 12.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .pointerInput(maxDragPx) {
                    var dragAmount = 0f
                    detectHorizontalDragGestures(
                        onDragStart = { dragAmount = dragOffsetPx },
                        onDragEnd = {
                            if (dragAmount >= maxDragPx * 0.82f) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onContinue()
                            }
                            dragOffsetPx = 0f
                        },
                        onHorizontalDrag = { change, dragDelta ->
                            change.consume()
                            dragAmount = (dragAmount + dragDelta).coerceIn(0f, maxDragPx)
                            dragOffsetPx = dragAmount
                        },
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = stringResource(R.string.oobe_slide_to_continue),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(25.dp),
            )
        }
    }
}

@Composable
private fun OobeFinishFooter(onStartListening: () -> Unit) {
    Button(
        onClick = onStartListening,
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .heightIn(min = 56.dp),
        shape = RoundedCornerShape(22.dp),
    ) {
        Text(
            text = stringResource(R.string.oobe_start_listening),
            fontWeight = FontWeight.SemiBold,
        )
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = null,
            modifier = Modifier.padding(start = 10.dp),
        )
    }
}
