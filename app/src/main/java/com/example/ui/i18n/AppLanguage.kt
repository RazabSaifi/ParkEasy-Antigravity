package com.example.ui.i18n

enum class AppLanguage(
    val code: String,
    val englishName: String,
    val nativeName: String,
    val flag: String,
    val shortBadge: String
) {
    ENGLISH("en", "English", "English", "🇬🇧", "EN"),
    HINDI("hi", "Hindi", "हिन्दी", "🇮🇳", "हि"),
    KANNADA("kn", "Kannada", "ಕನ್ನಡ", "🇮🇳", "ಕ"),
    TAMIL("ta", "Tamil", "தமிழ்", "🇮🇳", "த"),
    TELUGU("te", "Telugu", "తెలుగు", "🇮🇳", "తె"),
    SPANISH("es", "Spanish", "Español", "🇪🇸", "ES");

    companion object {
        fun fromCode(code: String?): AppLanguage {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
        }
    }
}
