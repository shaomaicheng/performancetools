package com.example.jvmtidmeo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.jvmtidmeo.ui.theme.JvmtidmeoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lei.cheng.performancetools.block.debug.BlockTraceActivity
import lei.cheng.performancetools.mainlock.MainLockManager
import kotlin.concurrent.thread

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
                        }.height(40.dp).wrapContentWidth())

                        Greeting("Android", modifier = Modifier.clickable {
                            startActivity(Intent(this@MainActivity,BlockTraceActivity::class.java))
                        }.height(40.dp).wrapContentWidth())

                        Greeting("ndk_dlopen", modifier = Modifier.clickable {
                            MainLockManager.test()
                        }.height(40.dp).wrapContentWidth())

                        Greeting("创造主线程锁等待", modifier = Modifier.clickable {
                            thread {
                                synchronized(this@MainActivity) {
                                    Log.e("chenglei","子线程获取锁")
                                    Thread.sleep(3*1000L)
                                    Log.e("chenglei","子线程用锁结束")
                                }
                            }
                            Thread.sleep(200)
                            synchronized(this@MainActivity) {
                                Log.e("chenglei","主线程获得锁")
                            }
                        }.height(40.dp).wrapContentWidth())
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