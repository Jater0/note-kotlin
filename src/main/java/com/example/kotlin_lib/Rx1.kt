package com.example.kotlin_lib

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.toObservable

fun main(args: Array<String>) {
    val list: List<Any> = listOf("One", 2, "Three", "Four", 4.5, "Five", 6.0f)
    val observable: Observable<Any> = list.toObservable()
    observable.subscribeBy(
        onNext = { println(it)},
        onError = {it.printStackTrace()},
        onComplete = { println("Done") }
    )
}