package com.surovtsev.finitestatemachine.config

enum class LogLevel {
    LOG_LEVEL_0, // minimal level, no logs
    LOG_LEVEL_1,
    LOG_LEVEL_2,
    LOG_LEVEL_3,
    LOG_LEVEL_4;

    fun isGreaterThan0(): Boolean {
        return isGreaterThan(
            LOG_LEVEL_0
        )
    }

    fun isGreaterThan1(): Boolean {
        return isGreaterThan(
            LOG_LEVEL_1
        )
    }

    fun isGreaterThan2(): Boolean {
        return isGreaterThan(
            LOG_LEVEL_2
        )
    }

    fun isGreaterThan3(): Boolean {
        return isGreaterThan(
            LOG_LEVEL_3
        )
    }

    fun isGreaterThan(
        logLevel: LogLevel
    ): Boolean {
        return this > logLevel
    }
}