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


package com.surovtsev.gamescreen

import com.surovtsev.restartablecoroutinescope.dagger.DaggerRestartableCoroutineScopeComponent
import com.surovtsev.restartablecoroutinescope.dagger.RestartableCoroutineScopeComponent
import com.surovtsev.subscriptionsholder.helpers.factory.SubscriptionsHolderComponentFactoryHolderImp
import com.surovtsev.utils.coroutines.restartablescope.RestartableCoroutineScope
import com.surovtsev.utils.coroutines.restartablescope.subscribing.subscription.Subscription
import com.surovtsev.utils.coroutines.restartablescope.subscribing.subscriptionsholder.SubscriptionsHolder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random


fun main() {
    CoroutinesRestartTest.test()
}

object CoroutinesRestartTest {
    fun test() {
        println("starting")

        val restartableCoroutineScopeComponent = DaggerRestartableCoroutineScopeComponent
            .create()

        val subscriptionsHolderComponent = SubscriptionsHolderComponentFactoryHolderImp
            .createAndSubscribe(
                restartableCoroutineScopeComponent,
                "GameScreen:CoroutineStopTest"
            )

        val testClass = TestClass(
            subscriptionsHolderComponent.subscriptionsHolder
        )

        runBlocking {
            val j1 = launch {
                testClass.printLoop(500L)
            }
            val j2 = launch {
                testClass.restartLoop(2000L, restartableCoroutineScopeComponent)
            }

            delay(10000L)
            j1.cancel()
            j2.cancel()
        }

        restartableCoroutineScopeComponent.subscriberImp.stop()

        println("done")
    }
}

class TestClass(
    subscriptionsHolder: SubscriptionsHolder,
): Subscription {
    var restartsCount = 0

    init {
        subscriptionsHolder.addSubscription(this)
    }

    var testVariable = 0

    override fun initSubscription(restartableCoroutineScope: RestartableCoroutineScope) {
        restartableCoroutineScope.launch {
            restartsCount++
            updateLoop()
        }
    }

    private suspend fun updateLoop() {
        do {
            nextTestValue()
        } while (true)
    }

    private suspend fun nextTestValue() {
        testVariable = Random.nextInt(10000) * 100 + restartsCount
    }

    private fun printValues() {
        println("restartCount: $restartsCount\ttestVariable: $testVariable")
    }

    suspend fun printLoop(
        delayDuration: Long
    ) {
        do {
            printValues()
            delay(delayDuration)
        } while (true)
    }

    suspend fun restartLoop(
        delayDuration: Long,
        restartableCoroutineScopeComponent: RestartableCoroutineScopeComponent
    ) {
        do {
            delay(delayDuration)
            restartableCoroutineScopeComponent
                .subscriberImp
                .restart()
        } while (true)
    }
}