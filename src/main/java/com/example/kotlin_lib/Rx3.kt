package com.example.kotlin_lib

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.kotlin.subscribeBy

fun main(args: Array<String>) {
    val maybeValue: Maybe<Int> = Maybe.just(14)
    /**
     * Maybe: 一个容器里可能有, 也可能没有
     *  onComplete: maybeValue没有值的时候调用的函数
     *  onError: 出错回调函数
     *  onSuccess: maybeValue有值的时候调用的函数
     *  这三个函数只会执行一个
     */
    // 订阅了maybeValue
    maybeValue.subscribeBy(
        onComplete = { println("Completed Empty")},
        onError = { println("Error $it")},
        onSuccess = { println("Completed with value $it")}
    )
}