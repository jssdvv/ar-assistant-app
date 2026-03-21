package com.jssdvv.ara.core.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Brown
private val Primary0 = Color(0xFF000000)
private val Primary10 = Color(0xFF261a0d)
private val Primary20 = Color(0xFF4d3319)
private val Primary30 = Color(0xFF734d26)
private val Primary40 = Color(0xFF996633)
private val Primary50 = Color(0xFFbf8040)
private val Primary60 = Color(0xFFcc9966)
private val Primary70 = Color(0xFFd9b38c)
private val Primary80 = Color(0xFFe6ccb3)
private val Primary90 = Color(0xFFf2e6d9)
private val Primary100 = Color(0xFFFFFFFF)

// Orange
private val Secondary0 = Color(0xFF000000)
private val Secondary10 = Color(0xFF2d1a06)
private val Secondary20 = Color(0xFF59330d)
private val Secondary30 = Color(0xFF864d13)
private val Secondary40 = Color(0xFFb36619)
private val Secondary50 = Color(0xFFdf8020)
private val Secondary60 = Color(0xFFe6994d)
private val Secondary70 = Color(0xFFecb379)
private val Secondary80 = Color(0xFFf2cca6)
private val Secondary90 = Color(0xFFf9e6d2)
private val Secondary100 = Color(0xFFFFFFFF)

// Blue
private val Tertiary0 = Color(0xFF000000)
private val Tertiary10 = Color(0xFF0E1A25)
private val Tertiary20 = Color(0xFF1C334A)
private val Tertiary30 = Color(0xFF2A4D6F)
private val Tertiary40 = Color(0xFF386694)
private val Tertiary50 = Color(0xFF4680B9)
private val Tertiary60 = Color(0xFF6B99C7)
private val Tertiary70 = Color(0xFF90B3D5)
private val Tertiary80 = Color(0xFFB5CCE3)
private val Tertiary90 = Color(0xFFDAE6F1)
private val Tertiary100 = Color(0xFFFFFFFF)

// Gray
private val Neutral0 = Color(0xFF000000)
private val Neutral5 = Color(0xFF0D0D0D)
private val Neutral10 = Color(0xFF1A1A1A)
private val Neutral15 = Color(0xFF262626)
private val Neutral20 = Color(0xFF333333)
private val Neutral25 = Color(0xFF404040)
private val Neutral30 = Color(0xFF4D4D4D)
private val Neutral40 = Color(0xFF666666)
private val Neutral50 = Color(0xFF808080)
private val Neutral60 = Color(0xFF999999)
private val Neutral70 = Color(0xFFB3B3B3)
private val Neutral80 = Color(0xFFCCCCCC)
private val Neutral85 = Color(0xFFD9D9D9)
private val Neutral90 = Color(0xFFE6E6E6)
private val Neutral95 = Color(0xFFF2F2F2)
private val Neutral100 = Color(0xFFFFFFFF)

// Gray Green
private val NeutralVariant0 = Color(0xFF000000)
private val NeutralVariant10 = Color(0xFF1F1F14)
private val NeutralVariant20 = Color(0xFF3D3D29)
private val NeutralVariant30 = Color(0xFF5C5C3D)
private val NeutralVariant40 = Color(0xFF7A7A52)
private val NeutralVariant50 = Color(0xFF999966)
private val NeutralVariant60 = Color(0xFFADAD85)
private val NeutralVariant70 = Color(0xFFC2C2A3)
private val NeutralVariant80 = Color(0xFFD6D6C2)
private val NeutralVariant90 = Color(0xFFEBEBE0)
private val NeutralVariant100 = Color(0xFFFFFFFF)

// Red
private val Error0 = Color(0xFF000000)
private val Error10 = Color(0xFF330500)
private val Error20 = Color(0xFF660A00)
private val Error30 = Color(0xFF990F00)
private val Error40 = Color(0xFFCC1400)
private val Error50 = Color(0xFFFF1A00)
private val Error60 = Color(0xFFFF4733)
private val Error70 = Color(0xFFFF7566)
private val Error80 = Color(0xFFFFA399)
private val Error90 = Color(0xFFFFD1CC)
private val Error100 = Color(0xFFFFFFFF)

internal val DarkColorScheme = darkColorScheme(
    primary = Primary80,
    onPrimary = Primary20,
    primaryContainer = Primary30,
    onPrimaryContainer = Primary90,
    inversePrimary = Primary40,
    secondary = Secondary80,
    onSecondary = Secondary20,
    secondaryContainer = Secondary30,
    onSecondaryContainer = Secondary90,
    tertiary = Tertiary80,
    onTertiary = Tertiary20,
    tertiaryContainer = Tertiary30,
    onTertiaryContainer = Tertiary90,
    background = Neutral5,
    onBackground = Neutral90,
    surface = Neutral15,
    onSurface = Neutral90,
    surfaceVariant = NeutralVariant30,
    onSurfaceVariant = NeutralVariant80,
    surfaceTint = Primary80,
    inverseSurface = Neutral90,
    inverseOnSurface = Neutral20,
    error = Error80,
    onError = Error20,
    errorContainer = Error30,
    onErrorContainer = Error90,
    outline = NeutralVariant60,
    outlineVariant = NeutralVariant30,
    scrim = Neutral0,
    surfaceBright = Neutral25,
    surfaceContainer = Neutral10,
    surfaceContainerHigh = Neutral15,
    surfaceContainerHighest = Neutral20,
    surfaceContainerLow = Neutral10,
    surfaceContainerLowest = Neutral5,
    surfaceDim = Neutral5
)

internal val LightColorScheme = lightColorScheme(
    primary = Primary40,
    onPrimary = Primary100,
    primaryContainer = Primary90,
    onPrimaryContainer = Primary30,
    inversePrimary = Primary80,
    secondary = Secondary40,
    onSecondary = Secondary100,
    secondaryContainer = Secondary90,
    onSecondaryContainer = Secondary30,
    tertiary = Tertiary40,
    onTertiary = Tertiary100,
    tertiaryContainer = Tertiary90,
    onTertiaryContainer = Tertiary30,
    background = Neutral100,
    onBackground = Neutral10,
    surface = Neutral100,
    onSurface = Neutral15,
    surfaceVariant = NeutralVariant90,
    onSurfaceVariant = NeutralVariant30,
    surfaceTint = Primary40,
    inverseSurface = Neutral20,
    inverseOnSurface = Neutral95,
    error = Error40,
    onError = Error100,
    errorContainer = Error90,
    onErrorContainer = Error30,
    outline = NeutralVariant50,
    outlineVariant = NeutralVariant80,
    scrim = Neutral0,
    surfaceBright = Neutral100,
    surfaceContainer = Neutral95,
    surfaceContainerHigh = Neutral90,
    surfaceContainerHighest = Neutral90,
    surfaceContainerLow = Neutral95,
    surfaceContainerLowest = Neutral100,
    surfaceDim = Neutral85
)