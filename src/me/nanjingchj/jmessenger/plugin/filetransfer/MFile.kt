package me.nanjingchj.jmessenger.plugin.filetransfer

import java.io.File
import java.io.Serializable

class MFile(f: ByteArray) : Serializable {
    var file: ByteArray? = f
    var isDownloaded = false
}