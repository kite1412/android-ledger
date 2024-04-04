package com.nrr.ledger.realm.dao

import android.util.Log
import com.nrr.ledger.realm.model.TransactionHistory
import com.nrr.ledger.realm.realm
import com.nrr.ledger.realm.util.HistoryType
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

private const val TAG = "TransactionHistoryDAO"

class TransactionHistoryDAO : RealmDAO<TransactionHistory> {
    override suspend fun items(): RealmResults<TransactionHistory> = realm.query<TransactionHistory>().find()

    suspend fun itemsByType(isIncome: Boolean): List<TransactionHistory> = items().filter {
        val type = if (isIncome) HistoryType.INCOME.name else HistoryType.EXPENSE.name
        it.type == type
    }

    override suspend fun writeItem(
        type: String,
        total: Int
    ) {
        coroutineScope {
            var needUpdate = true
            val items = async { items() }.await()
            items.forEach {
                if (it.total == total && it.type == type) {
                    needUpdate = false
                }
            }
            if (needUpdate) {
                realm.write {
                    copyToRealm(TransactionHistory().apply {
                        this.type = type
                        this.total = total
                    })
                }
            }
        }
        Log.i(TAG, "new transaction inserted: ${items().size}")
    }

    override suspend fun deleteItem(
        data: TransactionHistory
    ) {
        coroutineScope {
            val items = async { items() }.await()
            realm.write {
                val needDeletion = items.first {
                    it.type == data.type && it.total == data.total
                }
                val liveObject = findLatest(needDeletion)
                delete(liveObject!!)
                Log.i(TAG, "deleted: ${items.size - 1} remaining")
            }
        }
    }
}