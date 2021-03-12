package com.example.kotlin_lib

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.kotlin.subscribeBy

fun main(args: Array<String>) {
    val maybeValue: Maybe<Int> = Maybe.empty()
    maybeValue.subscribeBy(
        onComplete = { println("Completed Empty")},
        onError = { println("Error $it")},
        onSuccess = { println("Completed with value $it")}
    )
}