package com.johannesqvarford.touchevents.server

import kotlin.reflect.KClass

sealed class ServerMessage
data class StateChanged(val x: Int, val y: Int, val held: Boolean) : ServerMessage()
object ServerStartRequested : ServerMessage()
object ServerStopRequested : ServerMessage()
object ApplicationKillRequested : ServerMessage()

private typealias ServerMessageCallback = (m: ServerMessage) -> Unit
private typealias TypedServerMessageCallback<T> = (m: T) -> Unit

class MessageExchanger {
    val inputStateCallbacks = mutableMapOf<KClass<out ServerMessage>, MutableList<ServerMessageCallback>>()

    inline fun <reified T : ServerMessage> addCallback(crossinline messageCallback: TypedServerMessageCallback<T>) {
        inputStateCallbacks.getOrPut(T::class) { ArrayList() }
        val callbacks: MutableList<ServerMessageCallback> = inputStateCallbacks[T::class]!!
        callbacks.add {
            messageCallback(it as T)
        }
    }

    inline fun <reified T : ServerMessage> add(message: T) {
        inputStateCallbacks.getOrPut(T::class) { ArrayList() }
        val callbacks: MutableList<ServerMessageCallback> = inputStateCallbacks[T::class]!!
        callbacks.forEach { it(message) }
    }
}