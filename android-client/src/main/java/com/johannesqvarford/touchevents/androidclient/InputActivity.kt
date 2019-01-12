package com.johannesqvarford.touchevents.androidclient

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import kotlinx.coroutines.*
import java.lang.Math.floor
import java.net.InetAddress
import android.widget.ArrayAdapter
import com.johannesqvarford.touchevents.protocol.Client
import com.johannesqvarford.touchevents.protocol.InputStateMessage
import com.johannesqvarford.touchevents.protocol.MailBox


class InputActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)
        val position = findViewById<TextView>(R.id.position)
        val pressed = findViewById<TextView>(R.id.pressed)
        val touchArea = findViewById<View>(R.id.touchArea)
        val connect = findViewById<Button>(R.id.connect)
        val hostName = findViewById<TextView>(R.id.hostName)
        val self = this

        var x = 0.0
        var y = 0.0
        var pressedDown = false
        var connected = false
        var connectionJob: Job? = null

        connect.setOnClickListener {
            val ipAddressToUse = hostName.text.toString()

            connected = !connected
            connect.text = if (connected) "Disconnect" else "Connect"
            connectionJob?.cancel()

            if (connected) {
                connectionJob = runIO {
                    val client = Client(InetAddress.getByName(ipAddressToUse))
                    while (true) {
                        delay(10)
                        client.send(InputStateMessage(x = x.toInt(), y = y.toInt(), held = pressedDown))
                    }
                }
            }
        }

        fun updateDebugView() {
            position.text = String.format("Position: %03.0f:%03.0f", x, y)
            pressed.text = pressedDown.toString()
        }

        touchArea.setOnTouchListener{ view, event ->
            val positionScaling = 256.0 / touchArea.width
            x = floor(event.x * positionScaling)
            y = floor(event.y * positionScaling)
            pressedDown = when (event.action) {
                MotionEvent.ACTION_DOWN -> true
                MotionEvent.ACTION_UP -> false
                else -> pressedDown
            }
            updateDebugView()

            return@setOnTouchListener true;
        }
    }

    fun checkHosts(): Array<String> {
        val timeout = 1000
        val subnet = "192.168.10"
        val isReachable = InetAddress
            .getByName("192.168.10.100")
            .isReachable(100)

        val addresses = (1..254).asSequence()
            .map{ "$subnet.$it" }
            .map{ InetAddress.getByName(it) }
            .filter{ it.isReachable(timeout) }
            .toList()
        return addresses
            .map{ it.hostAddress }
            .toList()
            .toTypedArray()
    }

    private fun runUI(block: suspend CoroutineScope.() -> Unit): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            block()
        }
    }

    private fun runIO(block: suspend CoroutineScope.() -> Unit): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            block()
        }
    }
}
