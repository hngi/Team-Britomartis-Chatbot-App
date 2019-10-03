package com.britomartis.android.britobudget.utils

import android.content.Context
import com.britomartis.android.britobudget.MainActivity
import com.britomartis.android.britobudget.R
import com.google.protobuf.Value


private fun doTextReplacement(context: Context, text: String, template: String): String {
    when (template) {
        "\$LOCAL_USERNAME" -> {
            // Replace local username
            val username = getUsernameOrNull(context)
            // Replace with default if there's no username
            username?.let {
                return text.replace(template, username, true)
            } ?: return text.replace(
                template,
                context.getString(R.string.chatbot_defaultreplacement_no_local_username),
                true
            )
        }
    }

    return text
}


private fun doValueSave(context: Context, key: String, value: String) {

    when (key) {
        "\$LOCAL_USERNAME" -> {
            // Save the user's name
            saveUsername(context, value)
        }
    }
}


fun parseBotReply(context: Context, replyPair: Pair<String?, Map<String, Value>?>?): String? {
    if (replyPair == null) return null

    var replyText = replyPair.first
    val payload = replyPair.second

    payload ?: return replyText

    // PARSE THE PAYLOAD
    // Ensure it has a type
    if (!payload.keys.contains("type")) return replyText
    when (payload.getValue("type").stringValue) {
        "REPLACE" -> {
            // Asking for replacement
            // Get the template
            val template = payload.getValue("template").stringValue
            // replyText should not be null
            if (replyText == null) return null
            replyText = doTextReplacement(context, replyText, template)
        }
        "SAVE" -> {
            // Asking for a value to be saved locally
            // Get the key and value
            val key = payload.getValue("key").stringValue
            val value = payload.getValue("value").stringValue

            if (key == null || value == null) return replyText
            doValueSave(context, key, value)
        }
    }

    return replyText
}

fun userNameAlreadySaved(context: Context): Boolean {
    val sharedPref = (context as MainActivity).getPreferences(Context.MODE_PRIVATE)
    sharedPref?.let { sp ->
        val username = sp.getString(context.getString(R.string.username_pref_key), null)
        if (!username.isNullOrEmpty()) return true
    }
    return false
}

fun getUsernameOrNull(context: Context): String? {
    val sharedPref = (context as MainActivity).getPreferences(Context.MODE_PRIVATE)
    sharedPref?.let { sp ->
        val username = sp.getString(context.getString(R.string.username_pref_key), null)
        if (!username.isNullOrEmpty()) return username
    }
    return null
}

fun saveUsername(context: Context, username: String) {
    val sharedPref = (context as MainActivity).getPreferences(Context.MODE_PRIVATE)
    sharedPref?.let { sp ->
        with(sharedPref.edit()) {
            putString(context.getString(R.string.username_pref_key), username)
            apply()
        }
    }
}