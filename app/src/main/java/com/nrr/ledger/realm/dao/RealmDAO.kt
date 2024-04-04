package com.nrr.ledger.realm.dao

import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmObject

interface RealmDAO<T : RealmObject> {
    suspend fun items(): RealmResults<T>
    suspend fun writeItem(type: String, total: Int)
    suspend fun deleteItem(data: T)
}