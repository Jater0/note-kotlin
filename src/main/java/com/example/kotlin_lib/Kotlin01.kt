package com.example.kotlin_lib

// Inline Function
inline fun doSomeFunc(a: Int) = a * a

fun main(args: Array<String>) {
    // Lambda Function
    val sum = {
        x: Int, y: Int -> x + y
    }
    println("Sum ${sum(12, 24)}")

    for (i in 1..5) {
        println("$i Output ${doSomeFunc(i)}")
    }
}