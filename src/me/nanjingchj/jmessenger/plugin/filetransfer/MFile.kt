package me.nanjingchj.jmessenger.plugin.filetransfer

import java.io.File
import java.io.Serializable

class MFile(f: File) : Serializable {
    var file: File? = f
    var isDownloaded = false
}