@file:Suppress("MemberVisibilityCanBePrivate")

package com.surovtsev.gamelogic.utils.utils.unused.componentsunused

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import android.widget.TextView
import com.surovtsev.gamelogic.R
//import org.jetbrains.anko.doAsync
//import org.jetbrains.anko.uiThread

@Suppress("unused")
class MessagesComponent: ScrollView {
    private var lineCount = 0


    constructor(context: Context): super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
        initView()
    }

    var tvMessages: TextView? = null

    fun initView() {
        inflate(context, R.layout.messages_component, this)
        tvMessages = findViewById(R.id.tvMessages)
    }

    fun addMessage(message: String) {
        if (tvMessages == null) {
            return
        }
        val lineSeparator = if (lineCount == 0) {
            ""
        } else {
            "\n"
        }
        lineCount++
        tvMessages!!.append("$lineSeparator\t$lineCount: $message")
        fullScroll(FOCUS_DOWN)
    }

    fun addMessageUI(message: String) {
//        doAsync {
//            uiThread {
//                addMessage(message)
//            }
//        }
    }
}