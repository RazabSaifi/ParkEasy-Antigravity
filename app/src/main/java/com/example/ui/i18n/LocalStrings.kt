package com.example.ui.i18n

import androidx.compose.runtime.staticCompositionLocalOf

fun getAppStrings(language: AppLanguage): AppStrings {
    return when (language) {
        AppLanguage.ENGLISH -> Translations.English
        AppLanguage.HINDI -> Translations.Hindi
        AppLanguage.KANNADA -> Translations.Kannada
        AppLanguage.TAMIL -> Translations.Tamil
        AppLanguage.TELUGU -> Translations.Telugu
        AppLanguage.SPANISH -> Translations.Spanish
    }
}

val LocalStrings = staticCompositionLocalOf<AppStrings> {
    Translations.English
}

val LocalAppLanguage = staticCompositionLocalOf<AppLanguage> {
    AppLanguage.ENGLISH
}
