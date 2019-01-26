package com.johannesqvarford.touchevents.protocol

import java.io.Closeable
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

private const val SERVER_PORT = 9145

class ServerResponse(val message: Message, val mailBox: MailBox)
class MailBox internal constructor(internal val address: InetAddress, internal val port: Int)

class Server private constructor(private val socket: DatagramSocket) : Closeable {
    constructor(): this(DatagramSocket(SERVER_PORT))

    fun receive(): ServerResponse {
        val inputBuffer = createMessageByteArray()
        val inputPacket = DatagramPacket(inputBuffer, inputBuffer.size)
        this.socket.receive(inputPacket)
        val message = unserializeMessage(inputBuffer)
        val senderAddress = MailBox(inputPacket.address, inputPacket.port)
        return ServerResponse(message, senderAddress)
    }

    override fun close() {
        this.socket.close()
    }
}

class Client private constructor(private val socket: DatagramSocket, private val mailBox: MailBox) {

    constructor(address: InetAddress): this(DatagramSocket(), MailBox(address, SERVER_PORT))

    constructor(mailBox: MailBox): this(DatagramSocket(), mailBox)

    fun send(message: Message) {
        val outputBuffer = createMessageByteArray()
        serializeMessage(message, outputBuffer)
        val outputPacket = DatagramPacket(outputBuffer, outputBuffer.size, this.mailBox.address, this.mailBox.port)
        this.socket.send(outputPacket)
    }

    fun receive(): Message {
        val inputBuffer = createMessageByteArray()
        val inputPacket = DatagramPacket(inputBuffer, inputBuffer.size)
        this.socket.receive(inputPacket)
        return unserializeMessage(inputBuffer)
    }
}