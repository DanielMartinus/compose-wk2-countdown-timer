/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androiddevchallenge.ui.theme.MyTheme
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class CountdownViewModel : ViewModel() {

    private var countDownTimer: CountDownTimer? = null

    // LiveData holds state which is observed by the UI
    // (state flows down from ViewModel)
    private val _state = MutableLiveData<State>(State.Idle())
    private var startTime: Long = 0L
    val state: LiveData<State> = _state

    fun start() {
        startTime = System.currentTimeMillis()
        countDownTimer = object : CountDownTimer(30000, 50) {
            override fun onTick(millisUntilFinished: Long) {
                val format: DateFormat = SimpleDateFormat("m:ss.SS")
                val timePassed =
                    TimeUnit.SECONDS.toMillis(30) - (System.currentTimeMillis() - startTime)
                val displayTime: String = format.format(timePassed)
                _state.value = State.Started(displayTime)
            }

            override fun onFinish() {
            }
        }.start()
    }

    fun reset() {
        _state.value = State.Idle()
        countDownTimer?.cancel()
    }

    fun stop() {
        countDownTimer?.cancel()
    }

    sealed class State {
        open val time: String = ""

        class Started(override val time: String) : State()
        class Idle(override val time: String = "0:30:00") : State()
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    Surface(color = MaterialTheme.colors.primary) {
        CountdownScreen()
    }
}

@Composable
fun CountdownScreen(viewModel: CountdownViewModel = CountdownViewModel()) {
    val state: CountdownViewModel.State by viewModel.state.observeAsState(CountdownViewModel.State.Idle())
    val countdownTime = state.time
    val ctaButtonText = when (state) {
        is CountdownViewModel.State.Started -> "Pause"
        is CountdownViewModel.State.Idle -> "Start"
    }
    CountdownContent(
        countdown = countdownTime, ctaButtonText = ctaButtonText,
        ctaClick = {
            when (state) {
                is CountdownViewModel.State.Started -> viewModel.stop()
                is CountdownViewModel.State.Idle -> viewModel.start()
            }
        },
        reset = { viewModel.reset() }
    )
}

@Composable
fun CountdownContent(
    countdown: String,
    ctaButtonText: String,
    ctaClick: () -> Unit,
    reset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$countdown",
            modifier = Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.h4,
            color = MaterialTheme.colors.secondary
        )
        Row(
            modifier = Modifier.padding(top = 120.dp),
        ) {
            Button(
                onClick = { reset() }
            ) {
                Text(text = "Reset", color = MaterialTheme.colors.secondary)
            }
            Button(
                modifier = Modifier.padding(start = 16.dp),
                onClick = {
                    ctaClick()
                },
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = MaterialTheme.colors.primaryVariant
                ),
            ) {
                Text(
                    text = "$ctaButtonText",
                    color = MaterialTheme.colors.secondary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
