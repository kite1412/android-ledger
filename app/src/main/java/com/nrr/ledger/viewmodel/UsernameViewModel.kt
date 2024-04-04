package com.nrr.ledger.viewmodel

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nrr.ledger.realm.RealmApp
import com.nrr.ledger.util.MAIN_PAGE
import com.nrr.ledger.util.USERNAME_SP

class UsernameViewModel(
    private val sp: SharedPreferences
) : ViewModel() {
    private var _username = mutableStateOf("")
    val username = _username

    object Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UsernameViewModel(
                sp = RealmApp.sp
            ) as T
        }
    }

    fun setUsername(newValue: String) {
        _username.value = newValue
    }

    fun onSave(onComplete: () -> Unit) {
        if (_username.value.isNotEmpty()) {
            sp.edit(commit = true) {
                putString(USERNAME_SP, _username.value)
                RealmApp.username.value = _username.value
            }
            onComplete()
        }
    }
}