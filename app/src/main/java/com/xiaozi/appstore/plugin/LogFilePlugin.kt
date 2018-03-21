package com.xiaozi.appstore.plugin

import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.xiaozi.appstore.component.Framework
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.RandomAccessFile
import java.nio.charset.Charset

/**
 * Created by fish on 17-10-12.
 */
class LogFilePlugin {
    companion object {
        var OPEN = false

        private val ABSOLUTE_CRASH_LOG_PATH = "${Environment.getExternalStorageDirectory().absolutePath}/log/alltest.log"
        private val lock: Any = Any()
        private val fileThreadHandler: Handler by lazy { Handler(HandlerThread("log_file_thread").apply { start() }.looper) }

        init {
            if (!File(ABSOLUTE_CRASH_LOG_PATH).parentFile.exists()) File(ABSOLUTE_CRASH_LOG_PATH).parentFile.mkdirs()
        }

        private fun appendLog(content: String) {
            synchronized(lock) {
                val file = RandomAccessFile(ABSOLUTE_CRASH_LOG_PATH, "rw")
                try {
                    file.seek(file.length())
                    file.write("$content\n".toByteArray(Charset.defaultCharset()))
                    file.close()
                    Log.e("LOG R APPEND-ok", content)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }

        fun AppendLog(content: String) {
            if (!OPEN) return
            fileThreadHandler.post { appendLog("${content}") }
        }

        fun AppendLog(key: String, content: String) {
            if (!OPEN) return
            fileThreadHandler.post { appendLog("$key:$content") }
        }

        fun AppendLogBlock(key: String, content: String) {
            appendLog("$key:$content")
        }

        fun RLog(tag: String, msg: String) {
            if (!OPEN) return
            fileThreadHandler.post { appendLog("Log/R$tag:$msg") }
        }

        fun asyncAllText(succ: (content: String) -> Unit) {
            fileThreadHandler.post {
                synchronized(lock) {
                    try {
                        val file = RandomAccessFile(ABSOLUTE_CRASH_LOG_PATH, "rw")
                        val bs: ByteArray = kotlin.ByteArray(file.length().toInt())
                        if (file.length() > 0) {
                            file.readFully(bs)
                            Framework._H.post { succ("{${String(bs, Charset.defaultCharset())}}") }
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        }

        fun clear() {
            fileThreadHandler.post {
                val file = File(ABSOLUTE_CRASH_LOG_PATH)
                synchronized(lock) {
                    if (file.exists()) file.delete()
                }
            }
        }

        fun AppendCrashLogBlock(ex: Throwable) {
            synchronized(lock) {
                try {
                    val file = File(ABSOLUTE_CRASH_LOG_PATH).apply { if (!exists()) createNewFile() }
                    val fw = FileWriter(file, true)
                    val pw = PrintWriter(fw)
                    fw.append('{').append('\n')
                    ex.printStackTrace(pw)
                    var cause: Throwable? = ex.cause
                    while (cause != null) {
                        cause.printStackTrace(pw)
                        cause = cause.cause
                    }
                    pw.close()
                    fw.append('}')
                    fw.close()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

}
