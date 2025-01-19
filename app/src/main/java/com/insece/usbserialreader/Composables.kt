package com.insece.usbserialreader


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun VerticalPairDivider(
    topBox: MeasureBoxStruct,
    bottomBox: MeasureBoxStruct,
    modifier: Modifier = Modifier,
    status: String = "Esperando conexion",
    firstPos: Boolean = false,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        if(firstPos) {
            InseceBox(
                status = status,
                modifier = modifier
                    .weight(1f)
                    .fillMaxHeight(),
                )
        } else {
            MeasureBox(
                topBox,
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
        MeasureBox(bottomBox,
            Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}

@Composable
fun InseceBox(modifier:Modifier, status : String ) {
    Box(
        modifier = modifier.background(Color(0xFFFFFFFF))
    ) {
        Image(
            painter = painterResource(id = R.drawable.insecelogo),
            contentDescription = "Insece Logo",
            modifier = Modifier.fillMaxSize()
        )
        Image(
            painter = painterResource(id = R.drawable.insece),
            contentDescription = "Insece Text",
            modifier = Modifier.fillMaxSize(),
        )

//        Text(
//            text = "SondApp\n\nInsece",
//            textAlign = TextAlign.Center,
//            fontSize = 40.sp,
//            fontWeight = FontWeight.Black,
//            modifier = Modifier.align(Alignment.Center)
//        )
        Text(
            text = status ,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}


@Composable
fun PairDivider(
    leftBox: MeasureBoxStruct,
    rightBox: MeasureBoxStruct,
    modifier: Modifier = Modifier,
    status : String = "Esperando conexion",
    firstPos: Boolean = false,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        if(firstPos){
            InseceBox(
                status = status,
                modifier = modifier
                .weight(1f)
                .fillMaxSize())
        }
        else{
            MeasureBox(
                leftBox,
                Modifier
                    .weight(1f)
                    .fillMaxSize()
            )
        }
        MeasureBox(rightBox,
            Modifier
                .weight(1f)
                .fillMaxSize()
        )
    }
}

@Composable
fun MeasureBox(
    box : MeasureBoxStruct,
    modifier: Modifier = Modifier
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = box.modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(box.backGround)
    ){
        Column(
            verticalArrangement = Arrangement.SpaceEvenly ,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(vertical = 40.dp)
        ) {
            Text(
                text = box.title,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                ){
                Text(
                    text = box.value.toString(),
                    textAlign = TextAlign.End,
                    fontSize = 18.sp,
                )
                Text(
                    text = box.unit,
                    textAlign = TextAlign.Start,
                    fontSize = 18.sp,
                )
            }
        }
    }
}

// Clase para el composable MeasureBox
class MeasureBoxStruct(
    val title : String,
    val value : Float,

    val unit : String,
    val backGround : Color,
    val modifier: Modifier = Modifier
)