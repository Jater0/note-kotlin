package com.example.kotlin_lib

import io.reactivex.rxjava3.core.Observable

fun main(args: Array<String>) {
    /**
     * Map函数 会把当前的observable01中的每一个值用传入的函数做一下变化
     * 再放入新的数据流中
     *  - 重点是在 源 Observable(observable01) 的值并不会改变
     *  -
     */
//    val observable01 = Observable.just(1, 2, 3)
//    observable01.map {
//        x -> 10 * x
//    }.subscribe(observer)
}