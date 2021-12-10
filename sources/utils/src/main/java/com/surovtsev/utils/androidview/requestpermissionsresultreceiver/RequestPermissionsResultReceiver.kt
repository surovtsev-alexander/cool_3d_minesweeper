package com.surovtsev.utils.androidview.requestpermissionsresultreceiver


data class RequestPermissionsResult(
    val requestCode: Int,
    val permissions: Array<out String>,
    val grantResults: IntArray
)


interface RequestPermissionsResultReceiver {
    fun handleRequestPermissionsResult(requestPermissionsResult: RequestPermissionsResult)
}
