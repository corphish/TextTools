package com.corphish.quicktools.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.corphish.quicktools.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val fontName = GoogleFont("Montserrat")

val BrandFontFamily = FontFamily(
    Font(googleFont = fontName, fontProvider = provider, weight = FontWeight.W200),
    Font(googleFont = fontName, fontProvider = provider, weight = FontWeight.W300),
    Font(googleFont = fontName, fontProvider = provider),
    Font(googleFont = fontName, fontProvider = provider, weight = FontWeight.W500),
    Font(googleFont = fontName, fontProvider = provider, weight = FontWeight.W600),
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

val TypographyV2 = Typography(
    headlineLarge = TextStyle(
        fontFamily = BrandFontFamily,
        fontWeight = FontWeight.W200,
        fontSize = 48.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = BrandFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 32.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = BrandFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 28.sp
    ),
    labelLarge = TextStyle(
        fontFamily = BrandFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 24.sp
    ),
    labelMedium = TextStyle(
        fontFamily = BrandFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = BrandFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 12.sp
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        letterSpacing = 0.5.sp
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        letterSpacing = 0.5.sp
    ),
    displayLarge = TextStyle(
        fontFamily = BrandFontFamily,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    displayMedium = TextStyle(
        fontFamily = BrandFontFamily,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp
    ),
    displaySmall = TextStyle(
        fontFamily = BrandFontFamily,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp
    )
)