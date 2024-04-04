package com.nrr.ledger.util

fun formatBalance(balance: Int): String {
    var formatted = balance.toString()
    if (balance in 1000..9999) {
        formatted = "${formatted[0]}.${formatted.substring(1)}"
    }
    if (balance in 10000..99999) {
        formatted = "${formatted[0]}${formatted[1]}.${formatted.substring(2)}"
    }
    if (balance in 100000..999999) {
        formatted = "${formatted[0]}${formatted[1]}${formatted[2]}.${formatted.substring(3)}"
    }
    if (balance in 1000000..9999999) {
        formatted = "${formatted[0]}.${formatted[1]}${formatted[2]}${formatted[3]}.${formatted.substring(4)}"
    }
    if (balance in 10000000..99999999) {
        formatted = "${formatted[0]}${formatted[1]}.${formatted[2]}${formatted[3]}${formatted[4]}.${formatted.substring(5)}"
    }
    if (balance in 100000000..999999999) {
        formatted = "${formatted[0]}${formatted[1]}${formatted[2]}.${formatted[3]}${formatted[4]}${formatted[5]}.${formatted.substring(6)}"
    }
    if (balance in 1000000000..9999999999) {
        formatted = "${formatted[0]}.${formatted[1]}${formatted[2]}${formatted[3]}.${formatted[4]}${formatted[5]}${formatted[6]}.${formatted.substring(7)}"
    }
    return formatted
}