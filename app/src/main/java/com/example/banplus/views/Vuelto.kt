package com.example.banplus.views

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.banplus.Activity.ListTypeDocument
import com.example.banplus.R
import com.example.banplus._interface.idropdown
import com.example.banplus.component.BtnNext
import com.example.banplus.component.DropdownDemo
import com.example.banplus.component.PostField
import com.example.banplus.component.header.HeaderInit
import com.example.banplus.navigation.PathRouter
import com.example.banplus.utils.mobileNumberFilter

@Composable
fun ViewVuelto(navController: NavController) {
    val context = LocalContext.current

    Scaffold() {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderInit(iconBoolean = false, menu = false)
            BodyContent { tipo, cedula, cell ->
               if (tipo != "" && cedula != "" && cell != "") {

                    if (cell.length == 11) {
                        navController.navigate(
                            PathRouter.VueltoNextRoute.withArgs(
                                tipo,
                                cedula,
                                cell
                            )
                        )
                    } else {
                        Toast.makeText(
                            context,
                            R.string.validTelefono,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Toast.makeText(
                        context,
                        R.string.campos_llenos,
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }
        }
    }
}

@Composable
private fun BodyContent(onClick: (tipo: String, celular: String, cell: String) -> Unit) {
    var tipo by remember { mutableStateOf<idropdown>(idropdown(key = "V", title = "V")) }
    var cedula by remember { mutableStateOf("") }
    var cell by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 44.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(modifier = Modifier.padding(top = 33.dp)) {
            Row() {
                DropdownDemo(
                    Modifier
                        .size(height = 65.dp, width = 120.dp)
                        .padding(end = 3.dp),
                    textColor = Color.Black,
                    selectedOptionText = tipo, onValueChange = { tipo = it },
                    modifierDropdownItem = Modifier.border(BorderStroke(0.5.dp, Color.Gray)),
                    label = stringResource(id = R.string.tipo),
                    options = ListTypeDocument

                )
                PostField(
                    text = cedula,
                    onValueChange = {
                        cedula = if (it.length > 9 || it.any { !it.isDigit() }) cedula else it
                    },
                    label = stringResource(id = R.string.cedula),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Next
                    )
                )
            }
            PostField(
                text = cell,
                onValueChange = {
                    cell = if (it.length > 11 || it.any { !it.isDigit() }) cell else it
                },
                label = stringResource(id = R.string.telefono),
                visualTransformation = {
                    mobileNumberFilter(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Next
                ),
            )
        }

        BtnNext(
            text = stringResource(id = R.string.siguiente),
            onClick = { onClick("${tipo.key}", cedula, cell) },
            ico = painterResource(id = R.drawable.ic_next),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(4.dp)
                .height(49.dp)
                .width(240.dp)
        )

    }
}
