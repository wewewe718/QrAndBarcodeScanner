package com.example.barcodescanner.usecase

import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.barcodescanner.extension.toCaps
import com.example.barcodescanner.extension.wifiManager
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

object WifiConnector {    
    private val hexRegex = """^[0-9a-f]+$""".toRegex(RegexOption.IGNORE_CASE)

    fun connect(
            context: Context,
            authType: String,
            name: String,
            password: String,
            isHidden: Boolean,
            anonymousIdentity: String,
            identity: String,
            eapMethod: String,
            phase2Method: String
    ): Completable {
        return Completable
            .create { emitter ->
                try {
                    tryToConnect(
                            context,
                            authType,
                            name,
                            password,
                            isHidden,
                            anonymousIdentity,
                            identity,
                            eapMethod.toEapMethod(),
                            phase2Method.toPhase2Method()
                    )
                    emitter.onComplete()
                } catch (ex: Exception) {
                    Logger.log(ex)
                    emitter.onError(ex)
                }
            }
            .subscribeOn(Schedulers.newThread())
    }

    private fun tryToConnect(
            context: Context,
            authType: String,
            name: String,
            password: String,
            isHidden: Boolean,
            anonymousIdentity: String,
            identity: String,
            eapMethod: Int?,
            phase2Method: Int?
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tryToConnectNewApi(context, authType, name, password, anonymousIdentity, identity, eapMethod, phase2Method)
        } else {
            tryToConnectOldApi(context, authType, name, password, isHidden, anonymousIdentity, identity, eapMethod, phase2Method)
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun tryToConnectNewApi(
            context: Context,
            authType: String,
            name: String,
            password: String,
            anonymousIdentity: String,
            identity: String,
            eapMethod: Int?,
            phase2Method: Int?
    ) {
        when (authType.toCaps()) {
            "", "NOPASS" -> connectToOpenNetworkNewApi(context, name)
            "WPA", "WPA2" -> connectToWpa2NetworkNewApi(context, name, password)
            "WPA2-EAP" -> connectToWpa2EapNetworkNewApi(context, name, password, anonymousIdentity, identity, eapMethod, phase2Method)
            "WPA3" -> connectToWpa3NetworkNewApi(context, name, password)
            "WPA3-EAP" -> connectToWpa3EapNetworkNewApi(context, name, password, anonymousIdentity, identity, eapMethod, phase2Method)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectToOpenNetworkNewApi(context: Context, name: String) {
        val builder = WifiNetworkSuggestion.Builder().setSsid(name)
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
    private fun connectToWpa2EapNetworkNewApi(
            context: Context,
            name: String,
            password: String,
            anonymousIdentity: String,
            identity: String,
            eapMethod: Int?,
            phase2Method: Int?
    ) {
        val config = WifiEnterpriseConfig().also { config ->
            config.anonymousIdentity = anonymousIdentity
            config.identity = identity
            config.password = password

            eapMethod?.apply {
                config.eapMethod = this
            }

            phase2Method?.apply {
                config.phase2Method = this
            }
        }

        val builder = WifiNetworkSuggestion.Builder()
                .setSsid(name)
                .setWpa2Passphrase(password)
                .setWpa2EnterpriseConfig(config)

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
    private fun connectToWpa3EapNetworkNewApi(
            context: Context,
            name: String,
            password: String,
            anonymousIdentity: String,
            identity: String,
            eapMethod: Int?,
            phase2Method: Int?
    ) {
        val config = WifiEnterpriseConfig().also { config ->
            config.anonymousIdentity = anonymousIdentity
            config.identity = identity
            config.password = password

            eapMethod?.apply {
                config.eapMethod = this
            }

            phase2Method?.apply {
                config.phase2Method = this
            }
        }

        val builder = WifiNetworkSuggestion.Builder()
                .setSsid(name)
                .setWpa3Passphrase(password)
                .setWpa3EnterpriseConfig(config)

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


    private fun tryToConnectOldApi(
            context: Context,
            authType: String,
            name: String,
            password: String,
            isHidden: Boolean,
            anonymousIdentity: String,
            identity: String,
            eapMethod: Int?,
            phase2Method: Int?
    ) {
        enableWifiIfNeeded(context)
        when (authType.toCaps()) {
            "", "NOPASS" -> connectToOpenNetworkOldApi(context, name, isHidden)
            "WPA", "WPA2" -> connectToWpaNetworkOldApi(context, name, password, isHidden)
            "WPA2-EAP" -> connectToWpa2EapNetworkOldApi(context, name, password, isHidden, anonymousIdentity, identity, eapMethod, phase2Method)
            "WEP" -> connectToWepNetworkOldApi(context, name, password, isHidden)
        }
    }

    @Suppress("DEPRECATION")
    private fun enableWifiIfNeeded(context: Context) {
        context.wifiManager?.apply {
            if (isWifiEnabled.not()) {
                isWifiEnabled = true
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun connectToOpenNetworkOldApi(context: Context, name: String, isHidden: Boolean) {
        val wifiConfiguration = WifiConfiguration().apply {
            SSID = name.quote()
            hiddenSSID = isHidden
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

    @Suppress("DEPRECATION")
    private fun connectToWpaNetworkOldApi(context: Context, name: String, password: String, isHidden: Boolean) {
        val wifiConfiguration = WifiConfiguration().apply {
            SSID = name.quote()
            preSharedKey = password.quoteIfNotHex()
            hiddenSSID = isHidden
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

    @Suppress("DEPRECATION")
    private fun connectToWpa2EapNetworkOldApi(
            context: Context,
            name: String,
            password: String,
            isHidden: Boolean,
            anonymousIdentity: String,
            identity: String,
            eapMethod: Int?,
            phase2Method: Int?
    ) {
        val wifiConfiguration = WifiConfiguration().apply {
            SSID = name.quote()
            preSharedKey = password.quoteIfNotHex()
            hiddenSSID = isHidden

            allowedProtocols.set(WifiConfiguration.Protocol.RSN)
            allowedProtocols.set(WifiConfiguration.Protocol.WPA)
            allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
            allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
            allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)

            enterpriseConfig.anonymousIdentity = anonymousIdentity
            enterpriseConfig.identity = identity
            enterpriseConfig.password = password

            eapMethod?.apply {
                enterpriseConfig.eapMethod = this
            }

            phase2Method?.apply {
                enterpriseConfig.phase2Method = this
            }
        }
        connect(context, wifiConfiguration)
    }

    @Suppress("DEPRECATION")
    private fun connectToWepNetworkOldApi(context: Context, name: String, password: String, isHidden: Boolean) {
        val wifiConfiguration = WifiConfiguration().apply {
            SSID = name.quote()
            wepKeys[0] = password.quoteIfNotHex()
            hiddenSSID = isHidden
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

    @Suppress("DEPRECATION")
    private fun connect(context: Context, wifiConfiguration: WifiConfiguration) {
        context.wifiManager?.apply {
            val id = addNetwork(wifiConfiguration)
            disconnect()
            enableNetwork(id, true)
            reconnect()
        }
    }

    
    private fun String.quote(): String {
        return if (startsWith("\"") && endsWith("\"")) this else "\"$this\""
    }

    private fun String.quoteIfNotHex(): String {
        return if (isHex()) {
            this
        } else {
            quote()
        }
    }

    private fun String.isHex(): Boolean {
        return length == 64 && matches(hexRegex)
    }

    private fun String.toEapMethod(): Int? {
        return when (this) {
            "AKA" -> WifiEnterpriseConfig.Eap.AKA
            "AKA_PRIME" -> requireApiLevel(Build.VERSION_CODES.M) { WifiEnterpriseConfig.Eap.AKA_PRIME }
            "NONE" ->  WifiEnterpriseConfig.Eap.NONE
            "PEAP" -> WifiEnterpriseConfig.Eap.PEAP
            "PWD" -> WifiEnterpriseConfig.Eap.PWD
            "SIM" -> WifiEnterpriseConfig.Eap.SIM
            "TLS" -> WifiEnterpriseConfig.Eap.TLS
            "TTLS" -> WifiEnterpriseConfig.Eap.TTLS
            "UNAUTH_TLS" -> requireApiLevel(Build.VERSION_CODES.N) { WifiEnterpriseConfig.Eap.UNAUTH_TLS }
            else -> null
        }
    }

    private fun String.toPhase2Method(): Int? {
        return when (this) {
            "AKA" -> requireApiLevel(Build.VERSION_CODES.O) { WifiEnterpriseConfig.Phase2.AKA }
            "AKA_PRIME" -> requireApiLevel(Build.VERSION_CODES.O) { WifiEnterpriseConfig.Phase2.AKA_PRIME }
            "GTC" -> WifiEnterpriseConfig.Phase2.GTC
            "MSCHAP" -> WifiEnterpriseConfig.Phase2.MSCHAP
            "MSCHAPV2" -> WifiEnterpriseConfig.Phase2.MSCHAPV2
            "NONE" -> WifiEnterpriseConfig.Phase2.NONE
            "PAP" -> WifiEnterpriseConfig.Phase2.PAP
            "SIM" -> requireApiLevel(Build.VERSION_CODES.O) { WifiEnterpriseConfig.Phase2.SIM }
            else -> WifiEnterpriseConfig.Phase2.NONE
        }        
    }

    private inline fun <T> requireApiLevel(version: Int, block: () -> T): T? {
        return if (Build.VERSION.SDK_INT >= version) return block() else null
    }
}