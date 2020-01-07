package me.nanjingchj.jmessenger

import jmessenger.client.AbstractPlugin
import jmessenger.client.PluginButton
import jmessenger.shared.Message
import jmessenger.shared.PluginMessage
import javax.swing.JLabel

class FileTransferPlugin : AbstractPlugin() {
    override fun onMessageReceived(p0: Message) {
    }

    override fun onMessageSent(p0: Message) {
    }

    override fun onClose() {
    }

    override fun onStart() {
    }

    override fun getCustomJButton(): PluginButton? {
        
    }

    override fun renderCustomMessage(pm: PluginMessage?): JLabel? {

    }
}