package com.ubuntu.vpn

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream

class MyVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var thread: Thread? = null
    private var running = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (running) return START_STICKY

        val builder = Builder()
        builder.setSession("UbuntuVpn-Pro")
            .addAddress("10.0.0.2", 32)
            .addRoute("0.0.0.0", 0)
            .addDnsServer("8.8.8.8")
            .addDnsServer("1.1.1.1")

        try {
            vpnInterface = builder.establish()
        } catch (e: Exception) {
            e.printStackTrace()
            stopSelf()
            return START_NOT_STICKY
        }

        running = true
        thread = Thread {
            val fd = vpnInterface?.fileDescriptor ?: return@Thread
            val input = FileInputStream(fd)
            val output = FileOutputStream(fd)
            val packet = ByteArray(32767)
            try {
                while (running) {
                    val length = input.read(packet)
                    if (length > 0) {
                        // Kwa sasa tunarudisha packet kama ilivyo - tunnel ya msingi
                        output.write(packet, 0, length)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread?.start()
        return START_STICKY
    }

    override fun onDestroy() {
        running = false
        try {
            thread?.interrupt()
            vpnInterface?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }
}
