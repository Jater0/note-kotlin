package com.example.kotlin_lib

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.toObservable
import java.lang.Exception
import java.util.concurrent.TimeUnit

val observer: Observer<Any> = object : Observer<Any> {
    override fun onComplete() {
        println("All Complete")
    }

    override fun onNext(t: Any?) {
        println("Next $t")
    }

    override fun onError(e: Throwable) {
        println("Error Occur ${e.message}")
    }

    override fun onSubscribe(d: Disposable?) {
        println("New Subscription")
    }
}

fun main(args: Array<String>) {
    val observable01: Observable<Any> = listOf("One", 2, "Three", "Four", 4.5, "Five", 6.0f).toObservable()
    observable01.subscribe(observer)
    println()

    /**
     * 通过Observable.create来创建Observable
     * 这个函数是一个高阶函数, 他接收另一个(以ObservableEmitter为参数的)函数为参数,返回一个Observable.
     * 我们可以在函数中利用ObservableEmitter弹射信息.
     * In here, it即为ObservableEmitter
     * Kotlin中Lambda函数如果只有一个参数, 可以省略声明, 在函数体中以it指代
     * ObservableEmitter 目前能使用的方法有:
     *      onNext
     *      onError
     *      onComplete
     */
    val observable02: Observable<Int> = Observable.create<Int> {
        it.onNext(1)
        it.onNext(2)
        it.onNext(3)
        it.onComplete()
    }
    val observable03: Observable<Int> = Observable.create<Int> {
        it.onNext(1)
        it.onNext(2)
        it.onNext(3)
        it.onError(Exception("My Custom Exception"))
    }
    observable02.subscribe(observer)
    println()
    observable03.subscribe(observer)
    println()

    /**
     * 订阅依然会停止, 但是不会调用observer的onComplete方法
     */
    val observable04: Observable<Int> = Observable.create<Int> {
        it.onNext(1)
    }
    observable04.subscribe(observer)
    println()

    /**
     * onComplete执行之后, Observable还会执行onNext,但是Observer不会对这些信息作出反应
     */
    val observable05: Observable<Int> = Observable.create<Int> {
        it.onComplete()
        it.onNext(1)
    }
    observable05.subscribe(observer)
    println()

    // 创建Observable的基本方法 --- Observable.fromX 与 Observable.just
    /**
     * Observable.just和Observable.fromX的区别:
     * Observable.just会把每一个参数作为一个整体, 如果有多个参数会把它们一个一个弹出来
     * 而 Observable.fromX会把参数展开
     */
    // Observable.fromX
    // X means unknown
    val list = listOf<Int>(1, 2, 3, 4)
    val observable06: Observable<Int> = Observable.fromIterable(list)
    observable06.subscribe(observer)
    println()
    // Observable.just
    Observable.just(54).subscribe(observer)
    println()
    Observable.just(listOf(1, 2, 3)).subscribe(observer)
    println()
    Observable.just(1, 2, 3).subscribe(observer)
    println()

    /**
     * Observable.just, Observable.fromX 和 Observable.create 的区别:
     *  create: 需要显式调用onComplete()(it.onComplete()), Observer才会调用onComplete方法
     *  just, fromX: 自动调用onComplete()方法
     */

    // 创建Observable的其他常用方法
    // empty(): 没有值, 会调用onComplete()
    Observable.empty<String>().subscribe(observer)
    println()
    // interval(间隔, 单位): 间隔一定时间弹射出来一个值; 不会调用onComplete()
    /*
        Output On CMD:
            New Subscription
            Next 0
            Next 1
            Next 2
        Reason: 2000 / 600 ≈ 3;
            so equals: for (i in 0..3) pln(it.OnNext(i))
        因为创建Observable对象 和 调用observer 需要时间, maybe
        所以 Thread.sleep(1800) 并不会输出 0, 1, 2; 而是输出 0 & 1
        interval的处理机制: 如果有两个Thread.sleep(), 将 第一个的时间耗尽之后, 才会开始调用第二个的interval
     */
    Observable.interval(600, TimeUnit.MILLISECONDS).subscribe(observer)
    Thread.sleep(1801) // 必须要
    println()
    // timer(时间, 单位): 一段时间后弹出一个值; 会调用onComplete(), 前提 Thread.sleep的时间要 > Observable.timer的时间
    Observable.timer(20, TimeUnit.MILLISECONDS).subscribe(observer)
    Thread.sleep(50) // 必须要
    println()
    // range(起点, 次数): 在一个范围内依次调用onNext; 会调用onComplete();
    Observable.range(0, 3).subscribe(observer)
    println()
}