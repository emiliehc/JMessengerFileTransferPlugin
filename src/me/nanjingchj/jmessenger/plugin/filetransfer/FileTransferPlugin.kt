package me.nanjingchj.jmessenger.plugin.filetransfer

import jmessenger.client.AbstractPlugin
import jmessenger.client.Messenger
import jmessenger.client.PluginButton
import jmessenger.shared.Message
import jmessenger.shared.PluginMessage
import org.apache.commons.lang3.SerializationUtils
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.lang.Exception
import java.nio.file.Files
import java.util.*
import javax.swing.JFileChooser
import javax.swing.JLabel
import javax.swing.JOptionPane


class FileTransferPlugin : AbstractPlugin() {
    override fun onMessageReceived(p0: Message) {}
    override fun onMessageSent(p0: Message) {}
    override fun onClose() {}
    override fun onStart() {}

    override fun getCustomJButton(): PluginButton? {
        val btn = PluginButton {
            // show file dialog and get the file
            val fileChooser = JFileChooser()
            fileChooser.currentDirectory = File(System.getProperty("user.home"))
            val result = fileChooser.showOpenDialog(null)
            if (result == JFileChooser.APPROVE_OPTION) {
                val selectedFile = fileChooser.selectedFile
                val msg = PluginMessage(it.conversation.recipient, Files.readAllBytes(selectedFile.toPath()), "FILE")
                // send the message
                msg.isMyMessage = true
                it.conversation.addMessage(msg)
                Messenger.getInstance().send(msg)
            }
        }
        btn.text = "FILE"
        return btn
    }

    override fun renderCustomMessage(pm: PluginMessage): JLabel? {
        if (pm.type == "FILE") {
            val data = pm.data
            val lbl = JLabel(if (pm.isMyMessage) "[File]" else "[Click here to download file]")
            lbl.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    // download only if it is sent by the other person
                    if (!pm.isMyMessage) {
                        saveFile(data)
                        pm.isMyMessage = true // prevent further downloads
                        lbl.text = "[File]"
                    }
                }
            })
            return lbl
        }
        // if not a file
        return null
    }

    private fun saveFile(data: ByteArray) {
        val fileChooser = JFileChooser()
        fileChooser.currentDirectory = File(System.getProperty("user.home"))
        val result = fileChooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedFile = fileChooser.selectedFile
            FileOutputStream(selectedFile).use { fos ->
                fos.write(data)
            }
        }
    }
}