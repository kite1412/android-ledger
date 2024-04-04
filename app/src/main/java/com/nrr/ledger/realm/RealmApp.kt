package com.nrr.ledger.realm

import android.app.Application
import android.content.SharedPreferences
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.nrr.ledger.realm.dao.TransactionHistoryDAO
import com.nrr.ledger.realm.model.TransactionHistory
import com.nrr.ledger.util.APP_SHARED_PREFERENCES
import com.nrr.ledger.util.USERNAME_SP
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

lateinit var realm: Realm

class RealmApp : Application() {

    companion object {
        lateinit var transactionHistoryDAO: TransactionHistoryDAO
        lateinit var sp: SharedPreferences
        var username: MutableState<String> = mutableStateOf("")
    }

    override fun onCreate() {
        super.onCreate()
        realm = Realm.open(RealmConfiguration.create(
            schema = setOf(TransactionHistory::class)
        ))
        transactionHistoryDAO = TransactionHistoryDAO()
        sp = getSharedPreferences(APP_SHARED_PREFERENCES, MODE_PRIVATE)
        username.value = sp.getString(USERNAME_SP, "")!!
    }
}