package com.johannesqvarford.touchevents.server

import com.johannesqvarford.touchevents.protocol.InputStateMessage
import com.johannesqvarford.touchevents.protocol.Server
import java.io.IOException

fun initiateServer(exchanger: MessageExchanger) {
    val lock = Object()
    var server: Server? = null

    exchanger.addCallback<ServerStopRequested> {
        synchronized(lock) {
            server?.close()
            server = null
        }
    }
    exchanger.addCallback<ServerStartRequested> {
        synchronized(lock) {
            server?.close()
            server = Server()
            lock.notify()
        }
    }

    val thread = Thread {
        try {
            while(true) {
                var currentServer: Server?
                synchronized(lock) {
                    currentServer = server
                    if (currentServer == null) {
                        lock.wait()
                    }
                }

                currentServer?.let {
                    try {
                        val response = it.receive()
                        val message = response.message
                        when (message) {
                            is InputStateMessage -> exchanger.add(StateChanged(x = message.x, y = message.y, held = message.held))
                        }
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                }
            }
        } catch (ex: InterruptedException) {
            return@Thread
        }
        finally {
            synchronized(lock) {
                server?.close()
                server = null
            }
        }
    }

    exchanger.addCallback<ApplicationKillRequested> {
        synchronized(lock) {
            thread.interrupt()
        }
    }

    thread.start()
}