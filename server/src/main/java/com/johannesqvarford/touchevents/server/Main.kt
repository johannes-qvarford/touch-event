package com.johannesqvarford.touchevents.server

fun main(args: Array<String>) {
    val messageBroker = MessageExchanger()
    initiateServer(messageBroker)
    initiateConfigurationUI(messageBroker)
    initiateMouseControl(messageBroker)
}