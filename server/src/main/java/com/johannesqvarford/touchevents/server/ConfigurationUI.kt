package com.johannesqvarford.touchevents.server

import java.awt.GraphicsEnvironment
import javax.swing.*

@Suppress("UNUSED_PARAMETER")
fun initiateConfigurationUI(exchanger: MessageExchanger) {
    val configFrame = JFrame()
    configFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

    val screenCount = GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices.size

    val contentPane = configFrame.contentPane
    val screenLabel = JLabel("Screen: ")
    val screenField = JComboBox((1..screenCount).map { it.toString() }.toTypedArray())
    val emulatorLayoutLabel = JLabel("Layout: ")
    val emulatorLayoutField = JComboBox(arrayOf("DS top/bottom", "DS bottom/top", "DS left/right", "DS right/left", "DS bottom only", "DS hybrid/top"))
    val showTargetAreaButton = JButton("Show target area")
    val saveButton = JButton("Save")
    val toggleServerButton = JButton("Start Server")

    val layout = GroupLayout(contentPane)
    contentPane.layout = layout
    layout.autoCreateContainerGaps = true
    layout.autoCreateGaps = true
    layout.setHorizontalGroup(
        layout.createParallelGroup(GroupLayout.Alignment.CENTER, false)
            .addGroup(
                layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(screenLabel)
                        .addComponent(emulatorLayoutLabel))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(screenField)
                        .addComponent(emulatorLayoutField)))
            .addComponent(showTargetAreaButton)
            .addComponent(saveButton)
            .addComponent(toggleServerButton))

    layout.setVerticalGroup(
        layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(screenLabel)
                .addComponent(screenField))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(emulatorLayoutLabel)
                .addComponent(emulatorLayoutField))
            .addComponent(showTargetAreaButton)
            .addComponent(saveButton)
            .addComponent(toggleServerButton))

    layout.linkSize(showTargetAreaButton, saveButton, toggleServerButton)

    configFrame.pack()
    configFrame.minimumSize = configFrame.size
    configFrame.isVisible = true
}