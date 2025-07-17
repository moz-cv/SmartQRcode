package com.szr.co.smart.qr.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import androidx.core.content.edit
import androidx.core.os.ConfigurationCompat
import java.util.Locale
import kotlin.collections.filter
import kotlin.let

object AppLangUtils {

    sealed class Language(val displayName: String, val locale: Locale) {
        object Auto : Language("AUTO", getSystemLocale() ?: Locale.ENGLISH)
        object English : Language("English", Locale.ENGLISH)
        object French : Language("Français", Locale("fr"))
        object Hindi : Language("हिन्दी", Locale("hi"))
        object Portuguese : Language("Português", Locale("pt"))
        object Vietnamese : Language("Tiếng Việt", Locale("vi"))
        object Spanish : Language("Español", Locale("es"))
        object Korean : Language("한국어", Locale("ko"))
        object Japanese : Language("日本語", Locale("ja"))
        object Arabic : Language("عربي", Locale("ar"))


        companion object {
            fun fromDisplayName(name: String): Language {
                return when (name) {
                    English.displayName -> English
                    Portuguese.displayName -> Portuguese
                    French.displayName -> French
                    Hindi.displayName -> Hindi
                    Vietnamese.displayName -> Vietnamese
                    Spanish.displayName -> Spanish
                    Korean.displayName -> Korean
                    Arabic.displayName -> Arabic
                    Japanese.displayName -> Japanese
                    else -> Auto
                }
            }
        }
    }

    private val allSupportedLanguages = listOf(
        Language.Auto,
        Language.English,
        Language.Portuguese,
        Language.French,
        Language.Hindi,
        Language.Vietnamese,
        Language.Spanish,
        Language.Arabic,
        Language.Japanese,
        Language.Korean
    )

    private val supportedLanguagesWithoutAuto = allSupportedLanguages.filter { it != Language.Auto }

    fun getSupportedLanguages(includeAuto: Boolean = false): List<Language> {
        return if (includeAuto) allSupportedLanguages else supportedLanguagesWithoutAuto
    }

    fun switchLanguage(context: Context, language: Language) {
        LanguagePreference.saveLanguage(context, language.displayName)
        updateAppLanguage(context, language.locale)
    }

    fun getSavedLanguage(context: Context?): String {
        if (context == null) return Language.Auto.displayName
        return LanguagePreference.getLanguage(context) ?: Language.Auto.displayName

    }

    fun getSavedLanguageOrNull(context: Context?): String? {
        return context?.let { LanguagePreference.getLanguage(it) }
    }

    @SuppressLint("ObsoleteSdkInt")
    fun attachBaseContext(context: Context?): Context? {
        context ?: return null
        val language = getSavedLanguage(context)
        updateAppLanguage(context, Language.Companion.fromDisplayName(language).locale)
        return context
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun updateAppLanguage(context: Context, locale: Locale?) {
        val resources = context.resources
        val configuration = resources.configuration
        val metrics = resources.displayMetrics

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                configuration.setLocale(locale)
                configuration.setLocales(LocaleList(locale))
                context.createConfigurationContext(configuration)
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> {
                configuration.setLocale(locale)
            }

            else -> {
                configuration.locale = locale
            }
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            resources.updateConfiguration(configuration, metrics)
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun getSystemLocale(): Locale? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
        } else {
            Locale.getDefault()
        }
    }


    private object LanguagePreference {
        private const val PREF_NAME = "app_lang"
        private const val KEY_LANGUAGE = "lang"

        fun getLanguage(context: Context): String? {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return prefs.getString(KEY_LANGUAGE, "")
        }

        fun saveLanguage(context: Context, language: String?) {
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit {
                putString(KEY_LANGUAGE, language)
            }
        }
    }
}