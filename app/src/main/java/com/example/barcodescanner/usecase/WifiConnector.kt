package com.example.barcodescanner.usecase

import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager

class WifiConnector(context: Context) {
    private val wifiManager by lazy {
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    fun connect(authType: String, name: String, password: String) {
        enableWifiIfNeeded()

        when (authType) {
            "nopass" -> connectToOpenNetwork(name)
            "WPA" -> connectToWpaNetwork(name, password)
            "WEP" -> connectToWepNetwork(name, password)
        }
    }

    private fun enableWifiIfNeeded() {
        if (wifiManager.isWifiEnabled.not()) {
            wifiManager.isWifiEnabled = true
        }
    }

    private fun connectToOpenNetwork(name: String) {
        val wifiConfiguration = WifiConfiguration().apply {
            SSID = "\"" + name + "\""
            allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            allowedProtocols.set(WifiConfiguration.Protocol.RSN)
            allowedProtocols.set(WifiConfiguration.Protocol.WPA)
            allowedAuthAlgorithms.clear()
            allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
            allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
            allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
        }
        connect(wifiConfiguration)
    }

    private fun connectToWpaNetwork(name: String, password: String) {
        val wifiConfiguration = WifiConfiguration().apply {
            SSID = "\"" + name + "\""
            preSharedKey = "\""+ password +"\""
            allowedProtocols.set(WifiConfiguration.Protocol.RSN)
            allowedProtocols.set(WifiConfiguration.Protocol.WPA)
            allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
            allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
            allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
        }
        connect(wifiConfiguration)
    }

    private fun connectToWepNetwork(name: String, password: String) {
        val wifiConfiguration = WifiConfiguration().apply {
            SSID = "\"" + name + "\""
            wepKeys[0] = "\"" + password + "\""
            wepTxKeyIndex = 0
            allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
            allowedProtocols.set(WifiConfiguration.Protocol.RSN)
            allowedProtocols.set(WifiConfiguration.Protocol.WPA)
            allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
            allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
            allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
            allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
        }
        connect(wifiConfiguration)
    }

    private fun connect(wifiConfiguration: WifiConfiguration) {
        val id = wifiManager.addNetwork(wifiConfiguration)
        wifiManager.apply {
            disconnect()
            enableNetwork(id, true)
            reconnect()
        }
    }
}