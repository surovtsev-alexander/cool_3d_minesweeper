package com.surovtsev.finitestatemachine

import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.config.LogLevel
import com.surovtsev.finitestatemachine.mock.TestEvent
import com.surovtsev.finitestatemachine.mock.TestFSM
import com.surovtsev.finitestatemachine.state.StateDescription
import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import logcat.LogcatLogger
import logcat.PrintLogger
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class FSMUnitTest {

    private val testLogConfig = LogConfig(
        logLevel = LogLevel.LOG_LEVEL_4
    )

    private var coroutineScope: CustomCoroutineScope? = null
    private var fsm: TestFSM? = null

    companion object {
        @JvmStatic
        @BeforeClass
        fun loggerSetup() {
            LogcatLogger.install(PrintLogger)
        }
    }

    @Before
    fun setup() {
        coroutineScope = CustomCoroutineScope(dispatcher = Dispatchers.IO).also {
            fsm = TestFSM(
                it,
                testLogConfig,
            )
        }
    }

    @Test
    fun passOneEvent() {
        // arrange
        val fsm = this.fsm!!

        // act
        fsm.receiveEvent(
            TestEvent.Init
        )

        runBlocking {
            fsm.queueHolder.waitForEmptyQueue()
        }

        // assert
        assert(fsm.stateHolder.state.value.description is StateDescription.Idle)
    }

    @Test
    fun passingEvents() {
        // arrange
        val fsm = this.fsm!!

        // act
        fsm.receiveEvent(
            TestEvent.Init
        )
        fsm.receiveEvent(
            TestEvent.CloseError
        )
        fsm.receiveEvent(
            TestEvent.EmptyEvent
        )

        runBlocking {
            fsm.queueHolder.waitForEmptyQueue()
        }

        // assert
        assert(fsm.stateHolder.state.value.description is StateDescription.Idle)
    }

    @Test
    fun pauseResume() {
        // arrange
        val fsm = this.fsm!!

        // act


        // assert
        assert(fsm.stateHolder.state.value.description is StateDescription.Idle)
    }
}