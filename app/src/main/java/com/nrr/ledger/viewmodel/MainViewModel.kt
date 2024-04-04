package com.nrr.ledger.viewmodel

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.nrr.ledger.realm.RealmApp
import com.nrr.ledger.realm.dao.TransactionHistoryDAO
import com.nrr.ledger.realm.model.TransactionHistory
import com.nrr.ledger.realm.util.HistoryType
import com.nrr.ledger.util.THIS_MONTH_BALANCE
import com.nrr.ledger.util.THIS_MONTH_EXPENSE
import com.nrr.ledger.util.THIS_MONTH_INCOME
import com.nrr.ledger.util.USERNAME_SP
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "MainViewModel"

class MainViewModel(
    private var transactionHistoryDAO: TransactionHistoryDAO,
    private var sp: SharedPreferences
) : ViewModel() {

    object Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, ): T {
            return MainViewModel(
                transactionHistoryDAO = RealmApp.transactionHistoryDAO,
                sp = RealmApp.sp
            ) as T
        }
    }

    private var _isIncome = mutableStateOf(true)
    val isIncome = _isIncome

    private var _balanceInput = mutableStateOf("")
    val balanceInput = _balanceInput

    private var _thisMonthBalance = mutableIntStateOf(0)
    val thisMonthBalance = _thisMonthBalance

    private var _thisMonthIncome = mutableIntStateOf(0)
    val thisMonthIncome = _thisMonthIncome

    private var _thisMonthExpense = mutableIntStateOf(0)
    val thisMonthExpense = _thisMonthExpense

    private var _showMenu = mutableStateOf(false)
    val showMenu = _showMenu

    private var _showMenuAlertDialog = mutableStateOf(false)
    val showMenuAlertDialog = _showMenuAlertDialog

    private var _alertDialogMessage = mutableStateOf("")
    val alertDialogMessage = _alertDialogMessage

    private var _onConfirm = mutableStateOf({})
    val onConfirm = _onConfirm

    private var job: Job

    val snackbarHostState = SnackbarHostState()

    // make read-only
    var transactionHistories = mutableStateOf(listOf<TransactionHistory>())

    init {
        _thisMonthBalance.intValue = sp.getInt(THIS_MONTH_BALANCE, 0)
        _thisMonthIncome.intValue = sp.getInt(THIS_MONTH_INCOME, 0)
        _thisMonthExpense.intValue = sp.getInt(THIS_MONTH_EXPENSE, 0)
        viewModelScope.launch {
            transactionHistories.value = transactionHistoryDAO.itemsByType(true)
        }
        job = CoroutineScope(Dispatchers.Default).launch {
            transactionHistoryDAO.items().asFlow().collect {
                when (it) {
                    is UpdatedResults -> {
                        transactionHistories.value = it.list.filter { th ->
                            val type = if (_isIncome.value) HistoryType.INCOME.name else HistoryType.EXPENSE.name
                            th.type == type
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun setIsIncome(isIncome: Boolean) {
        _isIncome.value = isIncome
        viewModelScope.launch {
            transactionHistories.value = transactionHistoryDAO.itemsByType(isIncome)
        }
    }

    fun setBalanceInput(input: String) {
        try {
            // try to check if the inputted input is able to converted into integer
            if (input.toInt() <= Int.MAX_VALUE) {
                if (!input.startsWith("0")) {
                    _balanceInput.value = input
                } else {
                    _balanceInput.value = ""
                }
            }
        } catch (_: Throwable) {
            if (input.isEmpty()) {
                _balanceInput.value = ""
            }
        }
        Log.i(TAG, "_balanceInput = ${_balanceInput.value}")
    }

    private fun resetBalanceInput() {
        _balanceInput.value = ""
    }

    fun writeNewTransaction() {
        val type = if (_isIncome.value) HistoryType.INCOME.name else HistoryType.EXPENSE.name
        viewModelScope.launch {
            val transactionType = if (_isIncome.value) THIS_MONTH_INCOME else THIS_MONTH_EXPENSE
            val currentBalance = sp.getInt(THIS_MONTH_BALANCE, 0)
            val transaction = sp.getInt(transactionType, 0)
            sp.edit(true) {
                if (_isIncome.value) {
                    putInt(THIS_MONTH_BALANCE, currentBalance + _balanceInput.value.toInt())
                    _thisMonthBalance.intValue = currentBalance + _balanceInput.value.toInt()
                    _thisMonthIncome.intValue = transaction + _balanceInput.value.toInt()
                    transactionHistoryDAO.writeItem(type = type, total = _balanceInput.value.toInt())
                    putInt(transactionType, transaction + _balanceInput.value.toInt())
                } else {
                    if (currentBalance >= _balanceInput.value.toInt()) {
                        putInt(THIS_MONTH_BALANCE, currentBalance - _balanceInput.value.toInt())
                        _thisMonthBalance.intValue = currentBalance - _balanceInput.value.toInt()
                        _thisMonthExpense.intValue = transaction + _balanceInput.value.toInt()
                        transactionHistoryDAO.writeItem(type = type, total = _balanceInput.value.toInt())
                        putInt(transactionType, transaction + _balanceInput.value.toInt())
                    }
                    else snackbarHostState.showSnackbar("Expense can't exceeds the balance")
                }
                resetBalanceInput()
            }
        }
    }

    fun deleteTransaction(
        transactionHistory: TransactionHistory
    ) {
        viewModelScope.launch {
            transactionHistoryDAO.deleteItem(transactionHistory)
        }
    }

    fun onMenuClick() {
        _showMenu.value = !_showMenu.value
    }

    private fun setupAlertDialog(message: String, action: () -> Unit) {
        _showMenuAlertDialog.value = true
        _alertDialogMessage.value = message
        _onConfirm.value = action
    }

    fun dismissAlertDialog() {
        _showMenuAlertDialog.value = false
        _alertDialogMessage.value = ""
        _onConfirm.value = {}
    }

    private fun onReset() {
        sp.edit(true) {
            putInt(THIS_MONTH_BALANCE, 0)
            putInt(THIS_MONTH_INCOME, 0)
            putInt(THIS_MONTH_EXPENSE, 0)
        }
        _thisMonthBalance.intValue = 0
        _thisMonthIncome.intValue = 0
        _thisMonthExpense.intValue = 0
    }

    fun onMenuEvent(menuEvent: MenuEvent) {
        when (menuEvent) {
            is MenuEvent.Reset -> {
                setupAlertDialog(
                    message = "Reset this month activities?",
                    action = {
                        onReset()
                        dismissAlertDialog()
                        _showMenu.value = false
                    }
                )
            }
        }
    }
}

open class MenuEvent {
    object Reset : MenuEvent()
}