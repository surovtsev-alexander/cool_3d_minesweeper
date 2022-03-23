package com.surovtsev.finitestatemachine

import java.lang.reflect.Field

data class WW(
    val x: Int = 10
)

object XX {
    val x = WW(10)
    val y = WW(12)
}

class YY {
    val a = WW(1)
    val b = WW(2)
}

fun main() {
    println("starting")

    val fields: Array<Field> = XX::class.java.declaredFields

    for (field in fields) {
        println("field: $field")
        field.isAccessible = true
        val value = field.get(XX.javaClass)

        if (value is XX) {
            continue
        }

        val ww = value as WW
        println("value: $value; ${value.javaClass.name}")
        println("${ww}")
    }
    
    println("-----------")

    val yy = YY()
    
    val yyFields = yy::class.java.declaredFields
    
    yyFields.map { field ->
        println("field: $field")
        field.isAccessible = true
        val value = field.get(yy)
        println("value: $value; ${value.javaClass.name}")
        println("${value is WW}")
    }

    println("finishing")
}