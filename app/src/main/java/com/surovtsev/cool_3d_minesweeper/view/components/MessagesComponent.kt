package com.surovtsev.cool_3d_minesweeper.view.components

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat
import com.surovtsev.cool_3d_minesweeper.R
import kotlinx.android.synthetic.main.messages_component.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MessagesComponent: LinearLayoutCompat {
    private var mFirstLine = true


    constructor(context: Context): super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
        initView()
    }

    fun initView() {
        inflate(context, R.layout.messages_component, this)
    }

    fun addMessage(message: String) {
        val messageToAdd = if (mFirstLine) {
            mFirstLine = false
            message
        } else {
            "\n" + message
        }
        tv_messages.append(messageToAdd)
    }

    fun addMessageUI(message: String) {
        doAsync {
            uiThread {
                addMessage(message)
            }
        }
    }
}