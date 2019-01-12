package com.johannesqvarford.touchevents.server

import com.johannesqvarford.touchevents.protocol.InputStateMessage
import com.johannesqvarford.touchevents.protocol.Server
import java.awt.Robot
import java.awt.event.InputEvent

data class State(val x: Int, val y: Int, val held: Boolean)

fun main(args: Array<String>) {

    var previousState = State(0, 0, false)

    val server = Server()
    val robot = Robot()
    while (true) {

        var currentState: State? = null
        try {
            val response = server.receive()
            val message = response.message
            when (message) {
                is InputStateMessage -> currentState = State(message.x, message.y, message.held)
            }
        } catch (ex: Exception) {
            println("Exception in server loop: $ex")
        }

        currentState?.let {
            robot.mouseMove(it.x, it.y)
            val pressed = !previousState.held && it.held
            val released = previousState.held && !it.held

            if (pressed) {
                robot.mousePress(InputEvent.BUTTON1_MASK)
            }
            if (released) {
                robot.mouseRelease(InputEvent.BUTTON1_MASK)
            }
            previousState = it
        }

        println("state: $currentState")
    }
}