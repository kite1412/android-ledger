package com.nrr.ledger.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.AddCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrr.ledger.other.BalanceVisualTransformation
import com.nrr.ledger.realm.model.TransactionHistory
import com.nrr.ledger.util.formatBalance
import com.nrr.ledger.viewmodel.MainViewModel

private val formColor = Color(119, 158, 203)

@Composable
fun UpdateLedger(
    vm: MainViewModel
) {
    val focusManager = LocalFocusManager.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = formColor,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "Update Ledger",
                style = MaterialTheme.typography.headlineSmall.copy(color = Color.White)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 8.dp, top = 16.dp)
                ) {
                    Row {
                        TypeSelection(
                            isSelected = vm.isIncome.value,
                            onClick = {
                                vm.setIsIncome(it)
                            },
                            isIncome = true
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        TypeSelection(
                            isSelected = !vm.isIncome.value,
                            onClick = {
                                vm.setIsIncome(it)
                            },
                            isIncome = false
                        )
                    }
                    LazyRow(
                        modifier = Modifier
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(vm.transactionHistories.value) {
                            TransactionHistoryBar(
                                history = it,
                                onClick = { history ->
                                    vm.setBalanceInput(history.total.toString())
                                },
                                onDelete = { history -> vm.deleteTransaction(history) }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            modifier = Modifier.padding(bottom = 8.dp),
                            text = "Rp.",
                            color = Color.White
                        )
                        TextField(
                            value = vm.balanceInput.value,
                            onValueChange = {
                                vm.setBalanceInput(it)
                            },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = formColor,
                                focusedContainerColor = formColor,
                                cursorColor = Color.White,
                                unfocusedIndicatorColor = Color.White,
                                focusedIndicatorColor = Color.White,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            label = {
                                Text(
                                    text = "Balance",
                                    color = Color.White
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            visualTransformation = BalanceVisualTransformation(),
                            singleLine = true,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        TextButton(
                            enabled = vm.balanceInput.value.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                disabledContainerColor = Color(90, 130, 170),
                                containerColor = Color(255, 190, 0),
                                contentColor = Color.White,
                                disabledContentColor = Color(190, 190, 190)
                            ),
                            onClick = {
                                vm.writeNewTransaction()
                                focusManager.clearFocus()
                            }
                        ) {
                            Text(text = "Submit")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TypeSelection(
    isSelected: Boolean,
    onClick: (Boolean) -> Unit,
    isIncome: Boolean = true,
) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) if (isIncome) Color(0, 130, 0)
                else Color(160, 0, 0) else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 1.dp,
                color = if (!isSelected) if (isIncome) Color(0, 100, 0)
                else Color(160, 0, 0) else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(indication = null, interactionSource = MutableInteractionSource()) {
                onClick(isIncome)
            }
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp),
            text = if (isIncome) "Income" else "Expense",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (isSelected) Color.White else if (isIncome) Color(0, 130, 0)
                    else Color(160, 0, 0)
            )
        )
    }
}

@Composable
private fun TransactionHistoryBar(
    modifier: Modifier = Modifier,
    history: TransactionHistory,
    onClick: (TransactionHistory) -> Unit,
    onDelete: (TransactionHistory) -> Unit
) {
    Row(
        modifier = modifier
            .border(1.dp, color = Color.White, shape = RoundedCornerShape(100.dp))
            .clip(shape = RoundedCornerShape(100.dp))
            .clickable {
                onClick(history)
            }
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formatBalance(history.total),
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.White,
                fontSize = 13.sp
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        IconButton(
            modifier = Modifier
                .size(14.dp),
            onClick = {
                onDelete(history)
            }
        ) {
            Icon(
                Icons.Sharp.AddCircle,
                modifier = Modifier
                    .rotate(45.0f),
                tint = Color.White,
                contentDescription = "delete"
            )
        }
    }
}