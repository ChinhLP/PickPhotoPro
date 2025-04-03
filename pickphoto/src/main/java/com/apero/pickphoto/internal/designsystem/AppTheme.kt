package com.apero.pickphoto.internal.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration

private val lightCustomColorScheme = CustomColorScheme(
    material = lightColorScheme(
        primary = AppColors.Black,
        surface = AppColors.White,
        background = AppColors.Background,
        secondary = AppColors.Secondary,
        error = AppColors.Light.Text.Error
    ),
    textPrimary = AppColors.Light.Text.Primary,
    textHighMediumEmp = AppColors.Light.Text.HighMediumEmp,
    textSecondary = AppColors.Light.Text.Secondary,
    textDisable = AppColors.Light.Text.Disable,
    border = AppColors.Light.Border,
    grey50 = AppColors.Light.Grey.Grey50,
    grey100 = AppColors.Light.Grey.Grey100,
    grey300 = AppColors.Light.Grey.Grey300,
    grey400 = AppColors.Light.Grey.Grey400,
    grey600 = AppColors.Light.Grey.Grey600,
    grey700 = AppColors.Light.Grey.Grey700,
    grey900 = AppColors.Light.Grey.Grey900,
    main = AppColors.Main,
    main2 = AppColors.Secondary,
    profileLine = AppColors.Light.Line.ProfileLine,
    logoutTextColor = AppColors.Light.LogoutColor,
    statusBarColor = AppColors.Light.StatusBarColor,
    new = AppColors.New,
    unselectedBottomBar = AppColors.Light.BottomBarUnSelected,
    reminderLightingColor = AppColors.Reminder.LightingColor,
    reminderPrunColor = AppColors.Reminder.PrunColor,
    reminderWaterColor = AppColors.Reminder.WaterColor,
    reminderRepotColor = AppColors.Reminder.RepotColor,
    backGroundReminder = AppColors.Reminder.Background,

    //TODO testcolor
    btnShareStart = AppColors.Share.StartColor,
    btnShareEnd = AppColors.Share.EndColor
)

private val darkCustomColorScheme = CustomColorScheme(
    material = darkColorScheme(
        primary = AppColors.Black,
        surface = AppColors.White,
        background = AppColors.Background,
        secondary = AppColors.Secondary,
        error = AppColors.Dark.Text.Error
    ),
    textPrimary = AppColors.Dark.Text.Primary,
    textHighMediumEmp = AppColors.Dark.Text.HighMediumEmp,
    textSecondary = AppColors.Dark.Text.Secondary,
    textDisable = AppColors.Dark.Text.Disable,
    border = AppColors.Dark.Border,
    grey50 = AppColors.Dark.Grey.Grey50,
    grey100 = AppColors.Dark.Grey.Grey100,
    grey300 = AppColors.Dark.Grey.Grey300,
    grey400 = AppColors.Dark.Grey.Grey400,
    grey600 = AppColors.Dark.Grey.Grey600,
    grey900 = AppColors.Dark.Grey.Grey900,
    grey700 = AppColors.Dark.Grey.Grey600,
    main = AppColors.Main,
    main2 = AppColors.Secondary,
    profileLine = AppColors.Dark.Line.ProfileLine,
    logoutTextColor = AppColors.Dark.LogoutColor,
    statusBarColor = AppColors.Dark.StatusBarColor,
    new = AppColors.New,
    unselectedBottomBar = AppColors.Dark.BottomBarUnSelected,
    reminderLightingColor = AppColors.Reminder.LightingColor,
    reminderPrunColor = AppColors.Reminder.PrunColor,
    reminderWaterColor = AppColors.Reminder.WaterColor,
    reminderRepotColor = AppColors.Reminder.RepotColor,
    backGroundReminder = AppColors.Reminder.Background,
    //TODO testcolor
    btnShareStart = AppColors.Share.StartColor,
    btnShareEnd = AppColors.Share.EndColor
)

internal val LocalCustomColors = staticCompositionLocalOf { lightCustomColorScheme }
internal var cachedDensityScale: Float = 1f

@Composable
internal fun AppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val customColorScheme = if (darkTheme) darkCustomColorScheme else lightCustomColorScheme
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp


    val screenScale = remember(screenWidthDp) {
        val baseWidth = 360f  // Base width for scaling
        (screenWidthDp / baseWidth).also {
            cachedDensityScale = it
        }
    }
    val typography = rememberCustomTypography(screenScale)


    CompositionLocalProvider(
        LocalCustomColors provides customColorScheme,
        LocalCustomTypography provides typography,
        LocalScreenScale provides screenScale
    ) {

        MaterialTheme(
            colorScheme = customColorScheme.material,
            content = content
        )
    }
}