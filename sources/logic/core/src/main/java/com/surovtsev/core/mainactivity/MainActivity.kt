package com.surovtsev.core.mainactivity

import androidx.appcompat.app.AppCompatActivity
import com.surovtsev.core.mainactivity.requestpermissionsresultreceiver.RequestPermissionsResultReceiver

abstract class MainActivity: AppCompatActivity() {
    var requestPermissionsResultReceiver: RequestPermissionsResultReceiver? = null

}