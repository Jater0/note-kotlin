package com.example.kotlin_lib

import io.reactivex.rxjava3.core.Observable

inline fun Any.toIntOrError(): Int = toString().toInt()

fun main(args: Array<String>) {
    // Without Error Handling Operators
    Observable.just(1, 2, "Err", 3)
            .map {
                it.toIntOrError()
            }.subscribe(observer)
    println()

    // onErrorReturn
    Observable.just(1, 2, "Err", 3)
            .map {
                it.toIntOrError()
            }
            .onErrorReturn { -1 }
            .subscribe(observer)
    println()

    // onErrorResumeNext
    Observable.just(1, 2, "Err", 3)
            .map { it.toIntOrError() }
            .onErrorResumeNext { Observable.range(10, 2) }
            .subscribe(observer)
    println()

    // Retry
    Observable.just(1, 2, "Err", 3)
            .map { it.toIntOrError() }
            .retry(2)
            .subscribe(observer)
    println()
    var count = 0
    Observable.just(1, 2, "Err", 3)
            .map { it.toIntOrError() }
            .retry{_ , _ -> (++count) < 3}
            .subscribe(observer)
    println()
}