@file:OptIn(ExperimentalMaterial3Api::class)

package com.insece.usbserialreader

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandscapeLayout(status: String, tempValue:Float, moisValue:Float, nitValue:Float, phValue:Float, fosValue:Float, conValue:Float, potValue:Float) {
    val context = LocalContext.current
    val activity = context as? Activity
    val window = activity?.window
    var imgname by remember { mutableStateOf("")}
    var modal by remember { mutableStateOf(false)}
    var screen by remember { mutableStateOf(false)}
    var saveAsImage by remember {mutableStateOf(false)}
    var saveAsCSV by remember {mutableStateOf(false)}


    Box {
        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {

            VerticalPairDivider(
                status = status,
                firstPos = true,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                topBox = MeasureBoxStruct(
                    title = "Insece",
                    value = 0f,
                    unit = "",
                    backGround = Color(0xFF000000),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ),
                bottomBox = MeasureBoxStruct(
                    title = "Temperatura",
                    value = tempValue,
                    unit = "°C",
                    backGround = Color(0xFFEC0708),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            )
            VerticalPairDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                topBox = MeasureBoxStruct(
                    title = "Humedad",
                    value = moisValue,
                    unit = " %",
                    backGround = Color(0xFF06bfc0),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ),
                bottomBox = MeasureBoxStruct(
                    title = "Nitrogeno",
                    value = nitValue,
                    unit = " mg/Kg",
                    backGround = Color(0XFF457caa),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ),
            )

            VerticalPairDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                topBox = MeasureBoxStruct(
                    title = "PH",
                    value = phValue,
                    unit = " ",
                    backGround = Color(0xFF5cdd5e),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ),
                bottomBox = MeasureBoxStruct(
                    title = "Fosforo",
                    value = fosValue,
                    unit = "  mg/Kg",
                    backGround = Color(0XFFe9d3c1),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ),
            )

            VerticalPairDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                topBox = MeasureBoxStruct(
                    title = "Conductividad",
                    value = conValue,
                    unit = " μS/cm",
                    backGround = Color(0XFFec9b08),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ),
                bottomBox = MeasureBoxStruct(
                    title = "Potasio",
                    value = potValue,
                    unit = " mg/Kg",
                    backGround = Color(0xFFecd237),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ),
            )
        }

        FloatingActionButton(
            containerColor = Color(0xFF2196F3),
            modifier = Modifier
                .size(75.dp)
                .align(Alignment.BottomEnd)
                .padding(10.dp),
            shape = CircleShape,
            onClick = {
                Log.e("BOTON", "Floating action button clicked.")
                modal = !modal
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.saveicon),
                modifier = Modifier.size(35.dp).align(Alignment.Center),
                contentDescription  = "Guardar"
            )
        }


        if (modal) {
            Box(
                Modifier
                    .height(220.dp)
                    .width(350.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xAAFFFFFF))
                    .align(Alignment.Center)
                    .border(
                        width = 2.dp,
                        color = Color(0XAA000000),
                        shape = RoundedCornerShape(16.dp)
                    ),
            ) {
                Column (
                    modifier = Modifier.align(Alignment.Center)
                ){
                    TextField(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        value = imgname,
                        onValueChange = { imgname = it },
                        label = { Text("Nombre de la captura", color = Color(0xFF000000)) },
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor  = Color(0x33FFFFFF),
                            focusedIndicatorColor = Color(0xFF0000F8),
                            cursorColor = Color.Black
                        )
                    )
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .align(Alignment.End),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFAF0000),
                            ),
                            onClick = {
                                modal = false
                                imgname = ""
                            }) {
                            Text("Cancelar")

                        }

                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Blue,
                            ),
                            onClick = {
                                window?.let {
                                    modal = false

                                    if (saveAsImage) {
                                        val name = generateImageName()
                                        screen = true
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            saveScreenshot(context, it, "$imgname$name")
                                            imgname = ""
                                            println("Acción retrasada 10 ms")
                                            screen = false
                                        }, 10)
                                    }

                                    if (saveAsCSV) {
                                        // Save as CSV
                                        shareCsvFile(context, imgname, tempValue, moisValue, nitValue, phValue, fosValue, conValue, potValue)
                                        if (!saveAsImage) {
                                            imgname = ""
                                        }
                                    }

                                    if(!saveAsImage && !saveAsCSV){
                                        imgname = ""
                                    }
                                }
                            }) {
                            Text("Guardar")
                        }

                    }

                    Column(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ){
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Checkbox(checked = saveAsImage, onCheckedChange = {saveAsImage = it })
                            Text("Guardar como imagen")
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Checkbox(checked = saveAsCSV, onCheckedChange = { saveAsCSV = it } )
                            Text("Enviar como CSV")
                        }

                    }
                }
            }
        }



        if (screen) {
            val timestamp = SimpleDateFormat("dd-MM-yyyy || HH:mm:ss", Locale.getDefault()).format(Date())


            Box(
                modifier = Modifier
                    .height(70.dp)
                    .width(350.dp)
                    .align(Alignment.Center)
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(Color(0xAA000000)),
            ) {

                Text(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(5.dp),
                    text = imgname,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(5.dp),
                    text = timestamp,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }



        }




    }
}


@Composable
fun PortraitLayout(status: String, tempValue:Float, moisValue:Float, nitValue:Float, phValue:Float, fosValue:Float, conValue:Float, potValue:Float) {
    val context = LocalContext.current
    val activity = context as? Activity
    val window = activity?.window
    var imgname by remember { mutableStateOf("") }
    var modal by remember { mutableStateOf(false) }
    var screen by remember { mutableStateOf(false) }
    var saveAsImage by remember {mutableStateOf(false)}
    var saveAsCSV by remember {mutableStateOf(false)}

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.Black)
        ) {
            PairDivider(
                status = status,
                firstPos = true,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                leftBox = MeasureBoxStruct(
                    title = "INSECE",
                    value = 0f,
                    unit = " ",
                    backGround = Color(0xFFEC0708),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ),
                rightBox = MeasureBoxStruct(
                    title = "Temperatura",
                    value = tempValue,
                    unit = "ºC ",
                    backGround = Color(0xFFEC0708),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            )

            PairDivider(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                leftBox = MeasureBoxStruct(
                    title = "Humedad",
                    value = moisValue,
                    unit = " %",
                    backGround = Color(0xFF06bfc0),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ),
                rightBox = MeasureBoxStruct(
                    title = "Nitrogeno",
                    value = nitValue,
                    unit = " mg/Kg",
                    backGround = Color(0XFF457caa),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            )

            PairDivider(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                leftBox = MeasureBoxStruct(
                    title = "PH",
                    value = phValue,
                    unit = "",
                    backGround = Color(0xFF5cdd5e),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ),
                rightBox = MeasureBoxStruct(
                    title = "Fosforo",
                    value = fosValue,
                    unit = " mg/Kg",
                    backGround = Color(0XFFe9d3c1),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            )

            PairDivider(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),

                leftBox = MeasureBoxStruct(
                    title = "Conductividad",
                    value = conValue,
                    unit = " μS/cm",
                    backGround = Color(0XFFec9b08),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ),
                rightBox = MeasureBoxStruct(
                    title = "Potasio",
                    value = potValue,
                    unit = " mg/Kg",
                    backGround = Color(0xFFecd237),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            )
        }

        FloatingActionButton(
            containerColor = Color(0xFF2196F3),
            modifier = Modifier
                .size(75.dp)
                .align(Alignment.BottomEnd)
                .padding(10.dp),
            shape = CircleShape,
            onClick = {
                Log.e("BOTON", "Floating action button clicked.")
                modal = true
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.saveicon),
                modifier = Modifier.size(35.dp).align(Alignment.Center),
                contentDescription  = "Guardar"
            )
        }


        if (modal) {
            Box(
                Modifier
                    .height(220.dp)
                    .width(350.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xAAFFFFFF))
                    .align(Alignment.Center)
                    .border(
                        width = 2.dp,
                        color = Color(0XAA000000),
                        shape = RoundedCornerShape(16.dp)
                    ),
            ) {
                Column (
                    modifier = Modifier.align(Alignment.Center)
                ){
                    TextField(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        value = imgname,
                        onValueChange = { imgname = it },
                        label = { Text("Nombre de la captura", color = Color(0xFF000000)) },
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor  = Color(0x33FFFFFF),
                            focusedIndicatorColor = Color(0xFF0000F8),
                            cursorColor = Color.Black
                        )
                    )
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .align(Alignment.End),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {

                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFAF0000),
                            ),
                            onClick = {
                                modal = false
                                imgname = ""
                            }) {
                            Text("Cancelar")

                        }

                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Blue,
                            ),
                            onClick = {
                            window?.let {
                                modal = false

                                if (saveAsImage) {
                                    val name = generateImageName()
                                    screen = true
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        saveScreenshot(context, it, "$imgname$name")
                                        imgname = ""
                                        screen = false
                                    }, 10)
                                }

                                if (saveAsCSV) {
                                    // Save as CSV
                                    shareCsvFile(context, imgname, tempValue, moisValue, nitValue, phValue, fosValue, conValue, potValue)
                                    if (!saveAsImage){
                                        imgname = ""
                                    }
                                }

                                if(!saveAsImage && !saveAsCSV){
                                    imgname = ""
                                }
                            }
                        }) {
                            Text("Guardar")
                        }



                    }

                    Column(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ){
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Checkbox(checked = saveAsImage, onCheckedChange = {saveAsImage = it })
                            Text("Guardar como imagen")
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Checkbox(checked = saveAsCSV, onCheckedChange = { saveAsCSV = it } )
                            Text("Enviar como CSV")
                        }

                    }
                }
            }
        }



        if (screen) {
            val timestamp = SimpleDateFormat("dd-MM-yyyy || HH:mm:ss", Locale.getDefault()).format(Date())


            Box(
                modifier = Modifier
                    .height(70.dp)
                    .width(350.dp)
                    .align(Alignment.Center)
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(Color(0xAA000000)),
            ) {

                Text(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(5.dp),
                    text = imgname,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(5.dp),
                    text = timestamp,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }



        }
    }
}
