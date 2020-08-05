package com.example.barcodescanner.usecase

import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.barcodescanner.extension.toCaps
import com.example.barcodescanner.extension.wifiManager
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

object WifiConnector {

    fun connect(context: Context, authType: String, name: String, password: String): Completable {
        return Completable
            .create { emitter ->
                try {
                    tryToConnect(context, authType, name, password)
                    emitter.onComplete()
                } catch (ex: Exception) {
                    emitter.onError(ex)
                }
            }
            .subscribeOn(Schedulers.newThread())
    }

    private fun tryToConnect(context: Context, authType: String, name: String, password: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tryToConnectNewApi(context, authType, name, password)
        } else {
            tryToConnectOldApi(context, authType, name, password)
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun tryToConnectNewApi(context: Context, authType: String, name: String, password: String) {
        when (authType.toCaps()) {
            "", "NOPASS" -> connectToOpenNetworkNewApi(context, name)
            "WPA", "WPA2" -> connectToWpa2NetworkNewApi(context, name, password)
            "WPA3" -> connectToWpa3NetworkNewApi(context, name, password)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectToOpenNetworkNewApi(context: Context, name: String) {
        val builder = WifiNetworkSuggestion.Builder()
            .setSsid(name)

        connect(context, builder)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectToWpa2NetworkNewApi(context: Context, name: String, password: String) {
        val builder = WifiNetworkSuggestion.Builder()
            .setSsid(name)
            .setWpa2Passphrase(password)

        connect(context, builder)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectToWpa3NetworkNewApi(context: Context, name: String, password: String) {
        val builder = WifiNetworkSuggestion.Builder()
            .setSsid(name)
            .setWpa3Passphrase(password)

        connect(context, builder)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connect(context: Context, builder: WifiNetworkSuggestion.Builder) {
        val suggestions = listOf(builder.build())

        context.wifiManager?.apply {
            removeNetworkSuggestions(suggestions)
            addNetworkSuggestions(suggestions)
        }
    }


    private fun tryToConnectOldApi(context: Context, authType: String, name: String, password: String) {
        enableWifiIfNeeded(context)

        when (authType.toCaps()) {
            "", "NOPASS" -> connectToOpenNetworkOldApi(context, name)
            "WPA", "WPA2" -> connectToWpaNetworkOldApi(context, name, password)
            "WEP" -> connectToWepNetworkOldApi(context, name, password)
        }
    }

    private fun enableWifiIfNeeded(context: Context) {
        context.wifiManager?.apply {
            if (isWifiEnabled.not()) {
                isWifiEnabled = true
            }
        }
    }

    private fun connectToOpenNetworkOldApi(context: Context, name: String) {
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

    private fun connectToWpaNetworkOldApi(context: Context, name: String, password: String) {
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

    private fun connectToWepNetworkOldApi(context: Context, name: String, password: String) {
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