/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


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

    fun addMessageUI(
        @Suppress("UNUSED_PARAMETER") message: String
    ) {
//        doAsync {
//            uiThread {
//                addMessage(message)
//            }
//        }
    }
}