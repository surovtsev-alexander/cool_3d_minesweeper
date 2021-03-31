package com.surovtsev.cool_3d_minesweeper.view.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import com.surovtsev.cool_3d_minesweeper.R
import kotlinx.android.synthetic.main.messages_component.view.*

class MessagesComponent: LinearLayoutCompat {

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

    fun add_message(message: String) {
        tv_messages.append(message + "\n")
    }
}