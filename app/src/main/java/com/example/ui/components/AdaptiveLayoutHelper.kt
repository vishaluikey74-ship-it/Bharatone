package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Window Width Breakpoint Classes based on Material Design 3 and Android Adaptive Guidelines
 */
enum class WindowWidthClass {
    /** Mobile portrait & small foldables (< 600dp) */
    COMPACT,
    /** Large mobile, foldable unfolded, medium tablets portrait (600dp .. 839dp) */
    MEDIUM,
    /** Tablets landscape, foldables, small desktop/web (840dp .. 1199dp) */
    EXPANDED,
    /** Full desktop, ultra-wide web viewports (>= 1200dp) */
    ULTRA_WIDE
}

/**
 * Device layout archetype classification
 */
enum class ScreenType {
    MOBILE_PORTRAIT,
    MOBILE_LANDSCAPE,
    TABLET,
    DESKTOP_OR_WEB
}

/**
 * Rich adaptive window snapshot holding size calculations, breakpoints, and responsive presets
 */
@Immutable
data class AdaptiveWindowInfo(
    val widthDp: Dp,
    val heightDp: Dp,
    val widthClass: WindowWidthClass,
    val screenType: ScreenType,
    val isLandscape: Boolean,
    val optimalColumns: Int,
    val horizontalPadding: Dp,
    val verticalItemSpacing: Dp,
    val maxContentWidth: Dp = 1200.dp
) {
    val isCompact: Boolean get() = widthClass == WindowWidthClass.COMPACT
    val isMedium: Boolean get() = widthClass == WindowWidthClass.MEDIUM
    val isExpanded: Boolean get() = widthClass == WindowWidthClass.EXPANDED || widthClass == WindowWidthClass.ULTRA_WIDE
    val isUltraWide: Boolean get() = widthClass == WindowWidthClass.ULTRA_WIDE
    val isTabletOrLarger: Boolean get() = widthClass != WindowWidthClass.COMPACT

    /**
     * Pick a responsive value based on current width class
     */
    fun <T> select(
        compact: T,
        medium: T = compact,
        expanded: T = medium,
        ultraWide: T = expanded
    ): T {
        return when (widthClass) {
            WindowWidthClass.COMPACT -> compact
            WindowWidthClass.MEDIUM -> medium
            WindowWidthClass.EXPANDED -> expanded
            WindowWidthClass.ULTRA_WIDE -> ultraWide
        }
    }
}

/**
 * Local Composition provider for ambient AdaptiveWindowInfo access across the composable tree
 */
val LocalAdaptiveWindowInfo = staticCompositionLocalOf {
    AdaptiveWindowInfo(
        widthDp = 360.dp,
        heightDp = 800.dp,
        widthClass = WindowWidthClass.COMPACT,
        screenType = ScreenType.MOBILE_PORTRAIT,
        isLandscape = false,
        optimalColumns = 1,
        horizontalPadding = 12.dp,
        verticalItemSpacing = 8.dp
    )
}

/**
 * Calculates and remembers the current window's adaptive specifications dynamically
 */
@Composable
fun rememberAdaptiveWindowInfo(): AdaptiveWindowInfo {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = screenWidth > screenHeight

    return remember(screenWidth, screenHeight, isLandscape) {
        val widthClass = when {
            screenWidth < 600.dp -> WindowWidthClass.COMPACT
            screenWidth < 840.dp -> WindowWidthClass.MEDIUM
            screenWidth < 1200.dp -> WindowWidthClass.EXPANDED
            else -> WindowWidthClass.ULTRA_WIDE
        }

        val screenType = when {
            screenWidth >= 1200.dp -> ScreenType.DESKTOP_OR_WEB
            screenWidth >= 600.dp -> ScreenType.TABLET
            isLandscape -> ScreenType.MOBILE_LANDSCAPE
            else -> ScreenType.MOBILE_PORTRAIT
        }

        val optimalColumns = when (widthClass) {
            WindowWidthClass.COMPACT -> 1
            WindowWidthClass.MEDIUM -> 2
            WindowWidthClass.EXPANDED -> 3
            WindowWidthClass.ULTRA_WIDE -> 4
        }

        val horizontalPadding = when (widthClass) {
            WindowWidthClass.COMPACT -> 12.dp
            WindowWidthClass.MEDIUM -> 16.dp
            WindowWidthClass.EXPANDED -> 24.dp
            WindowWidthClass.ULTRA_WIDE -> 32.dp
        }

        val verticalSpacing = when (widthClass) {
            WindowWidthClass.COMPACT -> 8.dp
            WindowWidthClass.MEDIUM -> 10.dp
            WindowWidthClass.EXPANDED -> 12.dp
            WindowWidthClass.ULTRA_WIDE -> 16.dp
        }

        AdaptiveWindowInfo(
            widthDp = screenWidth,
            heightDp = screenHeight,
            widthClass = widthClass,
            screenType = screenType,
            isLandscape = isLandscape,
            optimalColumns = optimalColumns,
            horizontalPadding = horizontalPadding,
            verticalItemSpacing = verticalSpacing
        )
    }
}

/**
 * Composition provider to wrap top-level screens or root composables
 */
@Composable
fun ProvideAdaptiveWindowInfo(
    content: @Composable () -> Unit
) {
    val adaptiveInfo = rememberAdaptiveWindowInfo()
    CompositionLocalProvider(LocalAdaptiveWindowInfo provides adaptiveInfo) {
        content()
    }
}

/**
 * Responsive utility function to pick values based on current viewport
 */
@Composable
fun <T> adaptiveValue(
    compact: T,
    medium: T = compact,
    expanded: T = medium,
    ultraWide: T = expanded
): T {
    val info = rememberAdaptiveWindowInfo()
    return info.select(
        compact = compact,
        medium = medium,
        expanded = expanded,
        ultraWide = ultraWide
    )
}

/**
 * Responsive Dp dimension selector
 */
@Composable
fun adaptiveDp(
    compact: Dp,
    medium: Dp = compact,
    expanded: Dp = medium,
    ultraWide: Dp = expanded
): Dp = adaptiveValue(compact, medium, expanded, ultraWide)

/**
 * Responsive Sp typography size selector
 */
@Composable
fun adaptiveSp(
    compact: TextUnit,
    medium: TextUnit = compact,
    expanded: TextUnit = medium,
    ultraWide: TextUnit = expanded
): TextUnit = adaptiveValue(compact, medium, expanded, ultraWide)

/**
 * Responsive Column count selector
 */
@Composable
fun adaptiveColumns(
    compact: Int = 1,
    medium: Int = 2,
    expanded: Int = 3,
    ultraWide: Int = 4
): Int = adaptiveValue(compact, medium, expanded, ultraWide)

/**
 * Center-constrained responsive container to prevent UI from stretching on wide tablets / web
 */
@Composable
fun AdaptiveContainer(
    modifier: Modifier = Modifier,
    maxWidth: Dp = 1200.dp,
    content: @Composable BoxScope.(AdaptiveWindowInfo) -> Unit
) {
    val info = rememberAdaptiveWindowInfo()
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = maxWidth)
                .fillMaxWidth(),
            content = { content(info) }
        )
    }
}

/**
 * Adaptive 2-pane layout helper: Shows primary and secondary panes side-by-side on wide screens/web,
 * or switches to vertical/stacked on compact mobile screens.
 */
@Composable
fun AdaptiveTwoPane(
    modifier: Modifier = Modifier,
    primaryPaneRatio: Float = 0.6f,
    spacing: Dp = 16.dp,
    primaryPane: @Composable BoxScope.() -> Unit,
    secondaryPane: @Composable BoxScope.() -> Unit
) {
    val info = rememberAdaptiveWindowInfo()

    if (info.isTabletOrLarger) {
        Row(
            modifier = modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            Box(
                modifier = Modifier
                    .weight(primaryPaneRatio)
                    .fillMaxHeight(),
                content = primaryPane
            )
            Box(
                modifier = Modifier
                    .weight(1f - primaryPaneRatio)
                    .fillMaxHeight(),
                content = secondaryPane
            )
        }
    } else {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                content = primaryPane
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                content = secondaryPane
            )
        }
    }
}

/**
 * Modifier extension to apply responsive horizontal padding
 */
fun Modifier.adaptivePadding(info: AdaptiveWindowInfo): Modifier {
    return this.padding(horizontal = info.horizontalPadding, vertical = info.verticalItemSpacing)
}
