package com.johannesqvarford.touchevents.protocol

import java.io.IOException
import java.nio.ByteBuffer

private const val MAX_MESSAGE_SIZE = 16

private fun Any?.discard() = Unit

private enum class MessageType(val id: Int) {
    INPUT_STATE(1);

    companion object {
        fun from(id: Int) = when (id) {
            INPUT_STATE.id -> INPUT_STATE
            else -> throw UnrecognizedMessageException("Unrecognized message. ID: ${id}")
        }
    }
}


sealed class Message
data class InputStateMessage(val x: Int, val y: Int, val held: Boolean) : Message()
class UnrecognizedMessageException(message: String): IOException(message)

internal fun createMessageByteArray() = ByteArray(MAX_MESSAGE_SIZE)

internal fun unserializeMessage(source: ByteArray): Message {
    val buffer = ByteBuffer.wrap(source)
    val id = buffer.int
    return when (MessageType.from(id)) {
        MessageType.INPUT_STATE ->
            InputStateMessage(x = buffer.int, y = buffer.int, held = buffer.int == 1)
    }
}

internal fun serializeMessage(message: Message, destination: ByteArray) {
    val buffer = ByteBuffer.wrap(destination)
    return when (message) {
        is InputStateMessage -> {
            buffer.putInt(MessageType.INPUT_STATE.id)
            buffer.putInt(message.x)
            buffer.putInt(message.y)
            buffer.putInt(if (message.held) 1 else 0).discard()
        }
    }
}