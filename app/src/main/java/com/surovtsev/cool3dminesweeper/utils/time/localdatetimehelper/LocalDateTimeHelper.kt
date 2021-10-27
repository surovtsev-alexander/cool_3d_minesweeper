package com.surovtsev.cool3dminesweeper.utils.time.localdatetimehelper

import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset

object LocalDateTimeHelper {
    val localDateTime: LocalDateTime
        get() = LocalDateTime.now()

    val timeZone: ZoneId
        get() = ZoneId.systemDefault()

    val zoneOffset: ZoneOffset
        get() = timeZone.rules.getOffset(Instant.now())

    val epochMilli: Long
        get() = localDateTimeToEpochMilli()

    fun localDateTimeToEpochMilli(
        localDateTime: LocalDateTime = this.localDateTime,
        zoneOffset: ZoneOffset = this.zoneOffset
    ) = localDateTime.atOffset(zoneOffset).toInstant().toEpochMilli()

    fun restoreLocalDateTimeFromEpochMilli(
        epochMilli: Long
    ): LocalDateTime {
        val instant = Instant.ofEpochMilli(epochMilli)
        return LocalDateTime.ofInstant(instant, zoneOffset)
    }
}