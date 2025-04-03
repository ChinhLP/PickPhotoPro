package com.apero.pickphoto.internal.designsystem

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

internal object AppColors {
    val Main = Color(0xFF15B392)
    val Main2 = Color(0xFFA1BA9B)
    val Secondary = Color(0xFFF2F3F5)
    val Black = Color(0xFF000000)
    val White = Color(0xFFFFFFFF)
    val Background = Color(0xFFFFFEFB)
    val New = Color(0XFFE7C70B)

    object Share {
        val StartColor: Color = Color(0xFFB66DCF)
        val EndColor: Color = Color(0xFFF796A3)
    }


    object Reminder {
        val WaterColor: Color = Color(0xFF2997C5)
        val LightingColor: Color = Color(0xFFF9C123)
        val PrunColor: Color = Color(0xFF15B392)
        val RepotColor: Color = Color(0xFFAD6340)
        val Background: Color = Color(0xFFF7F7F7)
    }

    object Light {
        val Background = Color(0xFFFAFAFA)
        val Surface = Color(0xFFFFFFFF)
        val OnPrimary = Color(0xFFFFFFFF)
        val OnSecondary = Color(0xFF000000)
        val OnBackground = Color(0xFF000000)
        val OnSurface = Color(0xFF000000)
        val OnError = Color(0xFFFFFFFF)
        val BottomBarUnSelected = Color(0xFF9DB2CE)

        val Border = Color(0xFFEAEAEA)
        val LogoutColor = Color(0xFFF75555)
        val StatusBarColor = Color(0xFFF2F0CE)

        object Text {
            val Primary = Grey.Grey900
            val HighMediumEmp = Grey.Grey300
            val Secondary = Grey.Grey600
            val Disable = Grey.Grey400
            val Error = Color(0xFFF53F3F)
        }

        object Grey {
            val Grey50 = Color(0xFFF1F1F1)
            val Grey100 = Color(0xFFF7F7F7)
            val Grey300 = Color(0xFFEAEAEA)
            val Grey400 = Color(0xFFB0B1B2)
            val Grey600 = Color(0xFF797C80)
            val Grey700 = Color(0xFF1D1D21)
            val Grey900 = Color(0xFF131318)
        }

        object Line {
            val ProfileLine = Color(0xFF9E9E9E)
        }
    }

    object Dark {

        val Border = Color(0xFFEAEAEA)
        val LogoutColor = Color(0xFFF75555)
        val StatusBarColor = Color(0xFFF2F0CE)
        val BottomBarUnSelected = Color(0xFF9DB2CE)

        object Text {
            val Primary = Grey.Grey900
            val HighMediumEmp = Grey.Grey300
            val Secondary = Grey.Grey600
            val Disable = Grey.Grey400
            val Error = Color(0xFFF53F3F)
        }

        object Grey {
            val Grey900 = Color(0xFF141414)
            val Grey600 = Color(0xFF202020)
            val Grey400 = Color(0xFF303030)
            val Grey300 = Color(0xFF434343)
            val Grey200 = Color(0xFF2E2E2E)
            val Grey100 = Color(0xFF848890)
            val Grey50 = Color(0xFFEDEAEA)
        }

        object Line {
            val ProfileLine = Color(0xFF9E9E9E)
        }
    }
}

internal data class CustomColorScheme(
    val material: ColorScheme,
    val textPrimary: Color,
    val textHighMediumEmp: Color,
    val textSecondary: Color,
    val textDisable: Color,
    val border: Color,
    val grey50: Color,
    val grey100: Color,
    val grey300: Color,
    val grey400: Color,
    val grey600: Color,
    val grey700: Color,
    val grey900: Color,
    val main: Color,
    val main2: Color,
    val profileLine: Color,
    val logoutTextColor: Color,
    val statusBarColor: Color,
    val new: Color,
    val backGroundReminder: Color,
    val unselectedBottomBar : Color,
    val reminderWaterColor: Color,
    val reminderLightingColor: Color,
    val reminderPrunColor: Color,
    val reminderRepotColor: Color,
    val btnShareStart : Color,
    val btnShareEnd : Color
)