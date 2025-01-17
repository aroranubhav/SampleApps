package com.example.samplebluedotindoorlocation.initializer

import android.content.Context
import android.widget.Toast
import com.mist.android.IndoorLocationCallback
import com.mist.android.IndoorLocationManager
import com.mist.android.VirtualBeaconCallback
import java.lang.ref.WeakReference

class MistSdkmanager {

    private var indoorLocationManager : IndoorLocationManager? = null
    private var indoorLocationCallback : IndoorLocationCallback? = null
    private var virtualBeaconCallback :VirtualBeaconCallback? =null
    private var contextWeakReference :WeakReference<Context>? = null
    private var envType: String?=null
    private var orgSecret : String?=null
    private var mistSdkManager : MistSdkmanager? = null


    fun getInstance(context: Context): MistSdkmanager? {
        contextWeakReference = WeakReference<Context>(context)
        if (mistSdkManager == null) {
            mistSdkManager = MistSdkmanager()
        }
        return mistSdkManager
    }

    fun init(orgSecret: String?, indoorLocationCallback: IndoorLocationCallback?, virtualBeaconCallback: VirtualBeaconCallback?,context: Context) {
        if (orgSecret != null && !orgSecret.isEmpty()) {
            this.orgSecret = orgSecret
            this.envType = orgSecret[0].toString()
            this.indoorLocationCallback = indoorLocationCallback
            this.virtualBeaconCallback = virtualBeaconCallback
        } else {
            this.contextWeakReference = WeakReference<Context>(context)
            Toast.makeText(contextWeakReference?.get(), "Org Secret not present", Toast.LENGTH_SHORT).show()
        }
    }

    @Synchronized
    fun startMistSDK() {
        if (indoorLocationManager == null) {
            indoorLocationManager=IndoorLocationManager.getInstance(contextWeakReference?.get(), orgSecret)
            val node=indoorLocationManager
            node?.setVirtualBeaconCallback(virtualBeaconCallback)
            node?.start(indoorLocationCallback)
        } else {
            restartMistSDK()
        }
    }

    fun stopMistSDK() {
        if (indoorLocationManager != null) {
            indoorLocationManager!!.stop()
        }
    }

    fun destroy() {
        if (indoorLocationManager != null) {
            indoorLocationManager!!.stop()
            indoorLocationManager = null
        }
    }

    @Synchronized
    fun restartMistSDK() {
        if (indoorLocationManager != null) {
            stopMistSDK()
            indoorLocationManager!!.setVirtualBeaconCallback(virtualBeaconCallback)
            indoorLocationManager!!.start(indoorLocationCallback)
        }
    }

}