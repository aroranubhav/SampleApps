package com.example.sampleappwakeup.service

import android.content.Context
import android.widget.Toast
import com.mist.android.IndoorLocationCallback
import com.mist.android.IndoorLocationManager
import com.mist.android.VirtualBeaconCallback
import java.lang.ref.WeakReference

/**
 * MistSdkInitializer
 * This class provides utility function to start the Mist SDK.
 */
class MistSdkManager {
    /* Required by Mist SDK for initialization
      * IndoorLocationManager
      * IndoorLocationCallback
      * VirtualBeaconCallback
      */
    private var indoorLocationManager : IndoorLocationManager? = null
    private var indoorLocationCallback : IndoorLocationCallback? = null
    private var virtualBeaconCallback : VirtualBeaconCallback? =null
    private var contextWeakReference : WeakReference<Context>? = null
    private var envType: String?=null
    private var orgSecret : String?=null
    private var mistSdkManager : MistSdkManager? = null

    open fun getInstance(context: Context): MistSdkManager? {
        contextWeakReference = WeakReference<Context>(context)
        if (mistSdkManager == null) {
            mistSdkManager = MistSdkManager()
        }
        return mistSdkManager
    }

    open fun init(orgSecret: String?, indoorLocationCallback: IndoorLocationCallback?, virtualBeaconCallback: VirtualBeaconCallback?,context: Context) {
        if (orgSecret != null && !orgSecret.isEmpty()) {
            this.orgSecret = orgSecret
            envType = orgSecret[0].toString()
            this.indoorLocationCallback = indoorLocationCallback
            this.virtualBeaconCallback = virtualBeaconCallback
        } else {
            this.contextWeakReference=WeakReference<Context>(context)
            Toast.makeText(contextWeakReference?.get(), "Org Secret not present", Toast.LENGTH_SHORT).show()
        }
    }

    @Synchronized
    open fun startMistSDK() {
        if (indoorLocationManager == null) {
            indoorLocationManager=IndoorLocationManager.getInstance(contextWeakReference?.get(), orgSecret)
            val node=indoorLocationManager
            node?.setVirtualBeaconCallback(virtualBeaconCallback)
            node?.start(indoorLocationCallback)
        } else {
            restartMistSDK()
        }
    }

    open fun stopMistSDK() {
        if (indoorLocationManager != null) {
            indoorLocationManager!!.stop()
        }
    }

    open fun destroy() {
        if (indoorLocationManager != null) {
            indoorLocationManager!!.stop()
            indoorLocationManager = null
        }
    }

    @Synchronized
    open fun restartMistSDK() {
        if (indoorLocationManager != null) {
            stopMistSDK()
            indoorLocationManager!!.setVirtualBeaconCallback(virtualBeaconCallback)
            indoorLocationManager!!.start(indoorLocationCallback)
        }
    }
}