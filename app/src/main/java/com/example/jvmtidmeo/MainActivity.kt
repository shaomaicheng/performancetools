package com.example.jvmtidmeo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.jvmtidmeo.ui.theme.JvmtidmeoTheme
import lei.cheng.performancetools.block.debug.BlockTraceActivity
import lei.cheng.performancetools.mainlock.MainLockManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JvmtidmeoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        Greeting("Android", modifier = Modifier.clickable {
                            Thread.sleep(8 * 1000L)
                            Toast.makeText(this@MainActivity,"test",Toast.LENGTH_SHORT).show()
                        })

                        Greeting("Android", modifier = Modifier.clickable {
                            startActivity(Intent(this@MainActivity,BlockTraceActivity::class.java))
                        })

                        Greeting("ndk_dlopen", modifier = Modifier.clickable {
                            MainLockManager.ndk_dlopen()
                        })
                    }
                }
            }
        }
    }


}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JvmtidmeoTheme {
        Greeting("Android")
    }
}