package com.lucasxvirtual.composescreencapture

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lucasxvirtual.composescreencapture.ui.theme.ComposeScreenCaptureTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeScreenCaptureTheme {
                // A surface container using the 'background' color from the theme
                val takeScreenCapture = remember {
                    mutableStateOf(false)
                }
                var error by remember {
                    mutableStateOf("")
                }
                var bitmap by remember {
                    mutableStateOf<Bitmap?>(null)
                }
                val options = listOf("Simple screen", "List screen")
                var expanded by remember { mutableStateOf(false) }
                var selectedOptionText by remember { mutableStateOf(options[0]) }
                if (takeScreenCapture.value) {
                    ScreenCapture(
                        onResult = {
                            takeScreenCapture.value = false
                            when (it) {
                                is ScreenShotResult.Success -> {
                                    bitmap = it.bitmap
                                }

                                is ScreenShotResult.Error -> error = it.throwable.message.orEmpty()
                            }
                        },
                        takeScreenCapture = takeScreenCapture,
                        options = ScreenCaptureOptions(height = LocalView.current.measuredHeight * 4)
                    ) {
                        if (selectedOptionText == "Simple screen")
                            SimpleScreen()
                        else
                            ListScreen()
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = {expanded = !expanded}
                        ) {
                            TextField(
                                readOnly = true,
                                value = selectedOptionText,
                                onValueChange = { },
                                label = { Text("Screen type") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expanded
                                    )
                                },
                                modifier = Modifier.menuAnchor(),
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                options.forEach { selectionOption ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(text = selectionOption)
                                        },
                                        onClick = {
                                            selectedOptionText = selectionOption
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Column(
                            modifier = Modifier
                                .height(300.dp)
                                .padding(top = 20.dp)
                        ) {
                            if (selectedOptionText == "Simple screen")
                                SimpleScreen()
                            else
                                ListScreen()
                        }
                        Button(
                            onClick = {
                                takeScreenCapture.value = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .height(56.dp)
                        ) {
                            Text(text = "Take screen capture")
                        }
                        bitmap?.let {
                            Text(text = "Screen Capture: ", modifier = Modifier.align(Alignment.Start))
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Red)
                                    .border(1.dp, color = Color.Black)
                            )
                        }
                        if (error.isNotEmpty()) {
                            Text(text = error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
    ) {
        Text(
            text = "This is a simple screen",
        )
        Button(
            onClick = {},
            modifier = Modifier.height(56.dp)
        ) {
            Text(text = "Simple button")
        }
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null
        )
    }
}

@Composable
private fun ListScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
    ) {
        items(30) {
            Text(
                text = "list item $it",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
