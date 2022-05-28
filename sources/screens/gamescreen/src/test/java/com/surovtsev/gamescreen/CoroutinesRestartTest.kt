package com.surovtsev.gamescreen

import com.surovtsev.restartablecoroutinescope.dagger.DaggerRestartableCoroutineScopeComponent
import com.surovtsev.restartablecoroutinescope.dagger.RestartableCoroutineScopeComponent
import com.surovtsev.subscriptionsholder.helpers.factory.SubscriptionsHolderComponentFactoryHolderImp
import com.surovtsev.utils.coroutines.restartablecoroutinescope.RestartableCoroutineScope
import com.surovtsev.utils.coroutines.restartablecoroutinescope.subscription.Subscription
import com.surovtsev.utils.coroutines.restartablecoroutinescope.subscriptionsholder.SubscriptionsHolder
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