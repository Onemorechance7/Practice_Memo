package com.example.practice_memo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = MoleskineAccent,
    onPrimary = Color.White,
    secondary = MoleskineGray,
    onSecondary = Color.White,
    background = MoleskineWhite,
    onBackground = MoleskineBlack,
    surface = Color.White,
    onSurface = MoleskineBlack
)

private val DarkColorScheme = darkColorScheme(
    primary = MoleskineAccent,
    onPrimary = MoleskineBlack,
    secondary = MoleskineGray,
    onSecondary = Color.White,
    background = MoleskineBlack,
    onBackground = Color.White,
    surface = Color(0xFF2A2A2A),
    onSurface = Color.White
)

@Composable
fun PracticeMemoTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PracticeMemoTypography,
        content = content
    )
}
