package com.example.kotlin_lib

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.toObservable
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    /**
     * 一个Observer需要实现四个方法
     *      onNext()
     *      onError()
     *      onComplete()
     *      onSubscribe()
     * 当我们把Observable连接到Observer上的时候, 系统会调用这四个方法并把相对应的值传给它们
     */
    /**
     * what can Subscribe's attribute do?
     *      subscribe在Rx中几个重载方法;
     *      基本模式有两个:
     *          1. subscribe(onNext, onError, onComplete, onSubscribe)
     *              参数可以省略, 但是只能从后先前省略
     *              因为subscribe是在java文件中定义的, 所以不能使用kotlin的命名参数
     *          2. subscribe(observer): 见 Rx5-Observer.kt
     */
    // 这个observer没有onSubscribe()
    val observable: Observable<Int> = Observable.range(0, 3)
    observable.subscribe({
        println("Next $it")
    }, {
        println("Error ${it.message}")
    }, {
        println("All Completed")
    })
    println()
    // 这个observer有onSubscribe()
    Observable.range(0, 3).subscribe(observer)
    println()
    // 除了subscribe, 还有subscribeBy
    // 因为定义在kotlin中, 所以可以使用命名参数
    val list: List<Any> = listOf("One", 2, "Three", "Four", 4.5, "Five", 6.0f)
    val observable03: Observable<Any> = list.toObservable()
    observable03.subscribeBy(
            onNext = { println(it)},
            onError = {it.printStackTrace()},
            onComplete = { println("Done") }
    )
    println()

    /**
     * Subscribe:
     *  作用: 用来连接Observer & Observable
     *  它有两种形式:
     *      - 把onNext等方法, 以参数的形式传出去
     *      - 直接定义一个Observer对象
     *  如果选择第一种形式, 那么subscribe方法是有返回值的, 返回值的类型是Disposable
     *  如果选择第二种形式, 那么subscribe方法是没有返回值的,
     *  这两种形式中的onSubscribe()都是一个(d: Disposable): Unit 类型的函数
     */
    /**
     * Disposable:
     *  Disposable对象的dispose方法可以停止本次订阅
     */
    val observable04: Observable<Long> = Observable.interval(100, TimeUnit.MILLISECONDS)
    val observer02: Observer<Long> = object : Observer<Long> {
        /**
         * lateinit: 延迟初始化
         *  在Kotlin, 如果在类型声明之后, 没有使用符号?, 则表示该变量不能为null.
         *  lateinit就是用来告诉编译器, 这个变量会被初始化, 并且不会为null
         */
        lateinit var disposable: Disposable
        override fun onSubscribe(d: Disposable) {
            disposable = d
        }

        /**
         * 当disposable设置为disposed时(disposable.dispose());
         * 那么之后的onNext方法将不会被执行;
         * 所以Received只能输出到 5, 因为disposable在item == 5时, 被设置为了disposed;
         * 因为这次onNext还在执行中, 所以会输出5, 但是后面的将会不会被执行
         */
        override fun onNext(item: Long) {
            if (item >= 5 && !disposable.isDisposed) {
                disposable.dispose() // lateinit 真正初始化; 设置disposable为Dispose
                println("Disposed")
            }
            println("Received $item")
        }

        override fun onError(e: Throwable) {
            println("Error ${e.message}")
        }

        override fun onComplete() {
            println("Complete")
        }
    }
    observable04.subscribe(observer02)
    Thread.sleep(1000)

    // Hot/Cold Observable
    /**
     * Cold Observable: 静态被观察者
     *
     */
    val observable05: Observable<Int> = listOf<Int>(1, 2, 3, 4).toObservable()
    observable05.subscribe(observer)
    observable05.subscribe(observer)
    println()
    /**
     * Hot Observable: 动态被观察者
     */
    // 使用publish方法, 将Cold Observable 变成 ConnectableObservable(ConnectableObservable是Hot Observable的一种)
    val observable06 = listOf<Int>(1, 2, 3).toObservable().publish()
    observable06.subscribe {
        println("Subscription 1: $it")
    }
    observable06.subscribe {
        println("Subscription 2: $it")
    }
    // 前面的消息不会发送, 它们会在调用connect()方法时开始发送消息; 而Cold Observable会在调用subscribe的时候开始发送
    observable06.connect()
    // 如果订阅晚了, 则会错过一些消息
    // 这里抽过了所有, 因为计算机运行速度太快了
    observable06.subscribe {
        println("Subscription 3: $it")
    }
    println()

    val observable07 = Observable.interval(10, TimeUnit.MILLISECONDS).publish()
    observable07.subscribe{
        println("Subscription 1: $it")
    }
    observable07.subscribe{
        println("Subscription 2: $it")
    }
    observable07.connect()
    Thread.sleep(20) // 到这里我们还没开始 订阅 subscribe3 所以只输出了1 & 2 两次
    observable07.subscribe {
        println("Subscription 3: $it")
    }
    Thread.sleep(30) // 而到了这里, 我们开始订阅subscribe3 所以输出了 1, 2, 3 三次
}