package com.ubuntu.vpn

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var isConnected = false
    private lateinit var btnConnect: Button
    private lateinit var txtStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnConnect = findViewById(R.id.btnConnect)
        txtStatus = findViewById(R.id.txtStatus)

        btnConnect.setOnClickListener {
            if (!isConnected) {
                val intent = VpnService.prepare(this)
                if (intent != null) {
                    startActivityForResult(intent, 100)
                } else {
                    startVpn()
                }
            } else {
                stopVpn()
            }
        }
    }

    private fun startVpn() {
        val intent = Intent(this, MyVpnService::class.java)
        startService(intent)
        isConnected = true
        btnConnect.text = "DISCONNECT"
        txtStatus.text = "Status: Connected - UbuntuVpn-Pro"
        Toast.makeText(this, "VPN Connected", Toast.LENGTH_SHORT).show()
    }

    private fun stopVpn() {
        val intent = Intent(this, MyVpnService::class.java)
        stopService(intent)
        isConnected = false
        btnConnect.text = "CONNECT"
        txtStatus.text = "Status: Disconnected"
        Toast.makeText(this, "VPN Disconnected", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            startVpn()
        }
    }
}
