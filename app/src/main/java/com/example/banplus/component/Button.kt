package com.example.banplus.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.banplus.R
import com.example.banplus.ui.theme.color_black
import com.example.banplus.ui.theme.color_white

@Composable
fun BtnIni(ico: Painter, text: String, background: Color = color_black, colorText: Color = color_white, onClick: () -> Unit?) {
    Box() {
        Button(
            modifier = Modifier
                .padding(bottom = 25.dp, start = 3.dp, end = 3.dp)
                .height(150.dp)
                .width(150.dp),
            shape = RoundedCornerShape(15),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = background,
                contentColor =colorText
            ),
            onClick = { onClick()},
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp),
            ) {
                Icon(modifier= Modifier
                    .fillMaxSize(5F)
                    .padding(0.dp), painter = ico, contentDescription = stringResource(id = R.string.defualt_message_img))
            }
        }

        Text(text = text, modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(top = 104.dp), color = background)
    }

}

@Preview(showBackground = true)
@Composable
fun previewBtn() {
    val ico = painterResource(id = R.drawable.ic_time)
    BtnIni(text= stringResource(id = R.string.app_name),onClick = {/*  TODO: */}, ico = ico)
}