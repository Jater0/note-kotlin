package com.example.kotlin_lib

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.AsyncSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.ReplaySubject
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    /**
     * 使用PublishSubject.create()来创建PublishSubject对象
     *  - PublishSubject是Subject的一种
     *  - Subject是Hot Observable的一种
     *
     * Subject: 是Observable与Observer的组合体
     *  - Observable的所有操作符, Subject都有
     *  - 也可以像Observer一样接收值
     *  - 如果你向它的Observer接口传入值, 它会**选择性的**从Observable接口弹出
     *  (PublishSubject会把所有从Observer接口传入的值按照时间顺序**全部**弹出)
     */
//    val observable01 = Observable.interval(10, TimeUnit.MILLISECONDS)
//    val subject01 = PublishSubject.create<Long>()
//    // Observer
//    observable01.subscribe(subject01)
//    // Observable
//    subject01.subscribe{
//        println("Received $it")
//    }
//    Thread.sleep(60)
//    println()

    /**
     * Output：
        Message 1; Received 0
        Message 1; Received 1
        Message 1; Received 2
        Message 2; Received 0 // subscribe2 从 0 开始接收消息(因为它订阅的Observable是一个Cold Observable, 所以会从头发送)
        Message 2; Received 1
        Message 1; Received 3
        Message 2; Received 2
     */
//    val observable02 = Observable.interval(100, TimeUnit.MILLISECONDS)
//    observable02.subscribe{
//        println("Message 1; Received $it")
//    }
//    Thread.sleep(200)
//    observable02.subscribe{
//        println("Message 2; Received $it")
//    }
//    Thread.sleep(300)
//    println()

    /**
     * Output:
        Message 1; Received 0
        Message 1; Received 1
        Message 1; Received 2
        Message 1; Received 3
        Message 2; Received 3 // Subscribe2从 3 开始接受消息(错过了 0, 1, 2)
        Message 1; Received 4
        Message 2; Received 4
     */
//    val observable03 = Observable.interval(100, TimeUnit.MILLISECONDS)
//    val subject03 = PublishSubject.create<Long>()
//    observable03.subscribe(subject03)
//    subject03.subscribe{
//        println("Message 1; Received $it")
//    }
//    Thread.sleep(300)
//    subject03.subscribe{
//        println("Message 2; Received $it")
//    }
//    Thread.sleep(200)

    // Subject的其他实现
    /**
     * AsyncSubject
     *  会从源Observable(Subject的Observer接口传入值来自 源Observable)接收所有值
     *  并将最后一个值从Observable接口弹出
     */
    /**
     * Output
        New Subscription
        Next 4
        All Complete
     */
//    val observable04 = Observable.just(1, 2, 3, 4)
//    val subject04 = AsyncSubject.create<Int>()
//    observable04.subscribe(subject04)
//    subject04.subscribe(observer)
//    println()
    /**
     * Output:
        New Subscription
        New Subscription
        Next 4 // 为什么不是输出2
        All Complete
        Next 4
        All Complete
     * AsyncSubject当且仅当调用onComplete的时候才会弹出值
     * 所以Subscribe1 并没有输出 Next 2 而是输出了Next 4
     */
//    val subject05 = AsyncSubject.create<Int>()
//    subject05.onNext(1)
//    subject05.onNext(2)
//    subject05.subscribe(observer)
//    subject05.onNext(3)
//    subject05.subscribe(observer)
//    subject05.onNext(4)
//    subject05.onComplete()
//    println()

    /**
     * BehaviorSubject
     *  相当于把PublishSubject & AsyncSubject结合
     *  BehaviorSubject会弹出订阅BehaviorSubject之前的最后一个值(AsyncSubject的特性) 和
     *  订阅之后的所有值(PublishSubject的特性)
     */
    /**
     * Output
        New Subscription
        Next 2 // Subscribe01 Async行为
        Next 3 // Subscribe01 获取订阅之后的值
        New Subscription
        Next 3 // Subscribe02 获取订阅之后的值
        Next 4 // Subscribe01
        Next 4 // Subscribe02
        All Complete
        All Complete
     */
//    val subject06 = BehaviorSubject.create<Int>()
//    subject06.onNext(1)
//    subject06.onNext(2)
//    subject06.subscribe(observer)
//    subject06.onNext(3)
//    subject06.subscribe(observer)
//    subject06.onNext(4)
//    subject06.onComplete()
//    println()

    /**
     * ReplaySubject
     */
    /**
     * Output
        New Subscription
        Next 1 // Sub01
        Next 2 // Sub01
        New Subscription
        Next 1 // Sub02
        Next 2 // Sub02
        Next 3 // Sub01
        Next 3 // Sub02
        New Subscription
        Next 1 // Sub03
        Next 2 // Sub03
        Next 3 // Sub03
        Next 4 // Sub01
        Next 4 // Sub02
        Next 4 // Sub03
        All Complete
        All Complete
        All Complete
     * desc:
     *  当订阅了sub01, sub02, sub03时; 都会输出 1, 2, 3, 4;
     *  调用顺序如下:
     *      1. 订阅sub01: sub01输出 1, 2
     *      2. 订阅了sub02: sub02会在sub01输出 3之前, 输出 1, 2; 然后sub01, sub02 先后输出 3, 所以会有两个 3; 达到replay的效果
     *      3. 订阅了sub03: sub03会在sub01和sub02输出4之前, 输出 1, 2, 3; 如何sub01, sub02, sub03 先后输出4;
     *  我觉得输出 sub02 和 sub03的消息 更像是插队式输出;
     *  意思大概为:
     *      三个监听者 去 监听一个 被监听者;
     *      监听的顺序肯定是先来后到;
     *      但是因为 sub02插入的时候呢, sub01 已经监听了 Observable的 输出 1, 2
     *      而sub02也要监听,但是要先把前面欠下的监听输出; 因为observable没有调用onComplete()方法;
     *      所以在sub02插入之后, 先是输出了属于sub02的 1 & 2
     *      然后 再根据顺序输出 3
     *      sub03插入同理
     *      结束之后 onComplete
     */
    val subject07 = ReplaySubject.create<Int>()
    subject07.subscribe(observer) // sub01
    subject07.onNext(1)
    subject07.onNext(2)
    subject07.subscribe(observer) // sub02
    subject07.onNext(3)
    subject07.subscribe(observer) // sub03
    subject07.onNext(4)
    subject07.onComplete()
    println()
}