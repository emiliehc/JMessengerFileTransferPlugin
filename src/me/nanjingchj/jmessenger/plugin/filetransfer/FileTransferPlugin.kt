package me.nanjingchj.jmessenger.plugin.filetransfer

import jmessenger.client.AbstractPlugin
import jmessenger.client.Messenger
import jmessenger.client.PluginButton
import jmessenger.shared.Message
import jmessenger.shared.PluginMessage
import org.apache.commons.lang3.SerializationUtils
import java.io.File
import java.nio.file.Files
import javax.swing.JFileChooser
import javax.swing.JLabel
import java.io.FileOutputStream
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
                // wrap the file inside the MFile object so that it could be tracked whether or not the file has been
                // saved to the client computer. This is important because if the user has already saved the file to
                // their computer, the program would then clear the file so that it no longer exists in memory, which
                // lowers the memory footprint of the program if the user transfers file very frequently.
                val f = MFile(Files.readAllBytes(selectedFile.toPath()))
                // serialize the file and wrap it around inside a plugin message object
                val data = SerializationUtils.serialize(f)
                val msg = PluginMessage(it.conversation.recipient, data, "FILE")
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
            // deserialize then render
            val mFile = SerializationUtils.deserialize<MFile>(pm.data)
            // if the message is my own message, i won't have to downloaded. therefore, delete the file and mark it
            // as downloaded.
            if (pm.isMyMessage) {
                mFile.file = null
                mFile.isDownloaded = true
                // write the data back to the PluginMessage
                pm.data = SerializationUtils.serialize(mFile)
            } else if (!mFile.isDownloaded) {
                // download the file and prompt the user where to save it
                JOptionPane.showMessageDialog(null, "New file received. Click ok to save it locally.", "File", JOptionPane.INFORMATION_MESSAGE)
                val file = mFile.file
                val fileChooser = JFileChooser()
                fileChooser.currentDirectory = File(System.getProperty("user.home"))
                val result = fileChooser.showOpenDialog(null)
                if (result == JFileChooser.APPROVE_OPTION) {
                    val selectedFile = fileChooser.selectedFile
                    FileOutputStream("pathname").use { fos ->
                        fos.write(mFile.file) // TODO RESOLVE THIS ISSUE
                    }
                }
                mFile.isDownloaded = true
            }
            // render as [File]
            return JLabel("[File]")
        }
        // if not a file
        return null
    }
}