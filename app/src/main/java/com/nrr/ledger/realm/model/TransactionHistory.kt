package com.nrr.ledger.realm.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class TransactionHistory : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var type: String = ""
    var total: Int = 0
}