package com.example.barcodescanner.usecase

import android.content.Context
import android.net.wifi.WifiConfiguration
import com.example.barcodescanner.extension.wifiManager
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.schedulers.Schedulers

object WifiConnector {

    fun connect(context: Context, authType: String, name: String, password: String): Completable {
        return Completable
            .create { emitter ->
                connect(context, authType, name, password, emitter)
            }
            .subscribeOn(Schedulers.newThread())
    }

    private fun connect(context: Context, authType: String, name: String, password: String, emitter: CompletableEmitter) {
        try {
            tryToConnect(context, authType, name, password)
            emitter.onComplete()
        } catch (ex: Exception) {
            emitter.onError(ex)
        }
    }

    private fun tryToConnect(context: Context, authType: String, name: String, password: String) {
        enableWifiIfNeeded(context)

        when (authType) {
            "nopass" -> connectToOpenNetwork(context, name)
            "WPA" -> connectToWpaNetwork(context, name, password)
            "WEP" -> connectToWepNetwork(context, name, password)
        }
    }

    private fun enableWifiIfNeeded(context: Context) {
        context.wifiManager?.apply {
            if (isWifiEnabled.not()) {
                isWifiEnabled = true
            }
        }
    }

    private fun connectToOpenNetwork(context: Context, name: String) {
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
        connect(context, wifiConfiguration)
    }

    private fun connectToWpaNetwork(context: Context, name: String, password: String) {
        val wifiConfiguration = WifiConfiguration().apply {
            SSID = "\"" + name + "\""
            preSharedKey = "\"" + password + "\""
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
        connect(context, wifiConfiguration)
    }

    private fun connectToWepNetwork(context: Context, name: String, password: String) {
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
        connect(context, wifiConfiguration)
    }

    private fun connect(context: Context, wifiConfiguration: WifiConfiguration) {
        context.wifiManager?.apply {
            val id = addNetwork(wifiConfiguration)
            disconnect()
            enableNetwork(id, true)
            reconnect()
        }
    }
}