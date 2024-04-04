package com.nrr.ledger.presentation

import com.nrr.ledger.presentation.UpdateLedger
import android.content.SharedPreferences
import android.view.Menu
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nrr.ledger.realm.RealmApp
import com.nrr.ledger.util.THIS_MONTH_BALANCE
import com.nrr.ledger.util.THIS_MONTH_INCOME
import com.nrr.ledger.util.THIS_MONTH_EXPENSE
import com.nrr.ledger.util.USERNAME_PAGE
import com.nrr.ledger.util.USERNAME_SP
import com.nrr.ledger.util.formatBalance
import com.nrr.ledger.viewmodel.MainViewModel
import com.nrr.ledger.viewmodel.MenuEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    navController: NavController,
    vm: MainViewModel = viewModel<MainViewModel>(factory = MainViewModel.Factory)
) {
    if (RealmApp.username.value.isNotEmpty()) Scaffold(
        snackbarHost = { SnackbarHost(hostState = vm.snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { vm.onMenuClick() },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    ) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = "actions"
                        )
                    }
                    Menu(vm = vm)
                },
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = RealmApp.username.value,
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = null,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    indication = null
                                ) {
                                    navController.navigate(route = USERNAME_PAGE)
                                },
                            tint = if (isSystemInDarkTheme()) Color(red = 230, green = 230, blue = 230) else
                                Color(120, 120, 120),
                        )
                    }
                },
            )
        }
    ) {
        if (vm.showMenuAlertDialog.value) AlertDialog(
            onDismissRequest = { vm.dismissAlertDialog() },
            text = {
                Text(text = vm.alertDialogMessage.value)
            },
            confirmButton = {
                TextButton(
                    onClick = { vm.onConfirm.value() }
                ) {
                    Text(
                        text = "Yes",
                        color = Color(200, 0, 0)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { vm.dismissAlertDialog() },
                ) {
                    Text(
                        text = "Cancel",
                        color = if (isSystemInDarkTheme()) Color(100, 100, 100) else
                            Color(140, 140, 140)
                    )
                }
            }
        )
        LazyColumn(
            modifier = Modifier
                .padding(it),
            content = {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(199, 0, 57),
                                        Color(88, 24, 69)
                                    )
                                ),
                                shape = RoundedCornerShape(15.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(vertical = 18.dp, horizontal = 16.dp),
                            ) {
                                Text(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    text = "Balance"
                                )
                                Text(
                                    modifier = Modifier
                                        .padding(top = 6.dp),
                                    color = Color.White,
                                    text = "Rp.${formatBalance(vm.thisMonthBalance.intValue)}"
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .padding(end = 16.dp, top = 16.dp)
                            ) {
                                Text(
                                    text = "This month",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontSize = 11.sp)
                                )
                                SmallStat(
                                    icon = Icons.Rounded.KeyboardArrowUp,
                                    color = Color.Green,
                                    balance = "Rp.${formatBalance(vm.thisMonthIncome.intValue)}"
                                )
                                SmallStat(
                                    icon = Icons.Rounded.KeyboardArrowDown,
                                    color = Color.Red,
                                    balance = "Rp.${formatBalance(vm.thisMonthExpense.intValue)}"
                                )
                            }
                        }
                    }
                    UpdateLedger(vm = vm)
                }
            }
        )
    } else UsernamePage(navController = navController, editPage = false,)
}

@Composable
fun SmallStat(
    icon: ImageVector,
    color: Color,
    balance: String
) {
    Row {
        Icon(
            icon,
            modifier = Modifier
                .size(18.dp),
            tint = color,
            contentDescription = null,
        )
        Text(
            text = balance,
            color = color,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun Menu(
    vm: MainViewModel
) {
    DropdownMenu(
        expanded = vm.showMenu.value,
        onDismissRequest = { vm.onMenuClick() }
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = "Reset",
                    color = Color(200, 0, 0)
                )
            },
            onClick = { vm.onMenuEvent(MenuEvent.Reset) }
        )
    }
}