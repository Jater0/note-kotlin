# RxKotlin

## Maybe

### Description

**Maybe**  一个容器里可能有, 也可能没有

- **`onComplete`**  没有值的时候调用的函数
- **`onError`**  出错回调函数
- **`onSuccess`**  有值的时候调用的函数

这三个函数只会执行一个

### `Maybe.just()`

```kotlin
fun main(args: Array<String>) {
    val maybeValue: Maybe<Int> = Maybe.just(14)
    // 订阅了maybeValue
    maybeValue.subscribeBy(
        onComplete = { println("Completed Empty")},
        onError = { println("Error $it")},
        onSuccess = { println("Completed with value $it")}
    )
}
```

### `Maybe.empty()`

```kotlin
fun main(args: Array<String>) {
    val maybeValue: Maybe<Int> = Maybe.empty()
    maybeValue.subscribeBy(
        onComplete = { println("Completed Empty")},
        onError = { println("Error $it")},
        onSuccess = { println("Completed with value $it")}
    )
}
```

----



## Observable, Observer, Subscribe

| name       | description  |
| ---------- | ------------ |
| Observable | 被观察者     |
| Observer   | 观察者       |
| Subscribe  | 订阅(action) |

----



## Create Observer(创建观察者)

**一个 Observer 需要实现四个方法**

- **`onNext`** Observable 一个接一个地将所有内容传递给该方法。
- **`onComplete`** 当所有内容都处理后调用该方法
- **`onError`** 出错处理函数
- **`onSubscribe`** 开始订阅 Observable 时调用该方法

**`onError`** & **`onComplete`**都是终止方法, 只能调用其中一个

```kotlin
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
```

### use observer

``` kotlin
val observable01: Observable<Any> = listOf("One", 2, "Three", "Four", 4.5, "Five", 6.0f).toObservable()
observable01.subscribe(observer)
```

-----



## Create Observable(创建被观察者)

### `Observable.create`

**通过`Observable.create`来创建Observable**

这个函数是一个高阶函数, 他接收另一个(以`ObservableEmitter`为参数的)函数为参数,返回一个Observable.

我们可以在函数中利用 `ObservableEmitter` 弹射消息(), 在这里, it即为`ObservableEmitter`

Kotlin中Lambda函数如果只有一个参数, 可以省略声明, 在函数体中以it指代


```kotlin
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
```

**`ObservableEmitter`** 目前能使用的方法有:

- **`onNext`**
- **`onComplete`**
- **`onError`**

**PS** 这三个方法都可以省略

``` kotlin
// 省略onComplete()
val observable04: Observable<Int> = Observable.create<Int> {
    it.onNext(1)
}
```

- **Subscribe**会被停止, 但是不会调用**Observer**的**`onComplete`**方法

``` kotlin
val observable05: Observable<Int> = Observable.create<Int> {
    it.onComplete()
    it.onNext(1)
}
```

- 当调用了 **`onComplete()`**之后, Observable还是会执行**`onNext`**, 但是Observer并不会这些消息作出反应



### `Observable.fromX`

```kotlin
val list = listOf<Int>(1, 2, 3, 4)
// 可以使用list.toObservable() 替代 Observable.fromIterable(list)
val observable06: Observable<Int> = Observable.fromIterable(list)
observable06.subscribe(observer)
```

- X means unknown
- 可以使用**`list.toObservable()`** 替代 **`Observable.fromIterable(list)`**



### `Observable.just`

```kotlin
Observable.just(54).subscribe(observer)
println()
Observable.just(listOf(1, 2, 3)).subscribe(observer)
println()
Observable.just(1, 2, 3).subscribe(observer)
println()
```

- **just**会把每个参数作为一个整体. 如果有多个参数会把它们一个一个的弹出去



### just & fromX

| method                 | description                                                  |
| ---------------------- | ------------------------------------------------------------ |
| **`Observable.fromX`** | 会把每一个参数作为一个整体, 如果有多个参数会把它们一个一个弹出来 |
| **`Observable.just`**  | 会把参数展开                                                 |



### just & fromX & create

| method                   | description                                                  |
| ------------------------ | ------------------------------------------------------------ |
| **`create`**             | 需要显式调用**`onComplete()`**(**`it.onComplete()`**), Observer才会调用**`onComplete`**方法 |
| **`just`** , **`fromX`** | 自动调用**`onComplete()`**方法                               |

----



## Others way to create Observable

### `Observable.empty`

```kotlin
Observable.empty<String>().subscribe(observer)
```

- 没有值
- 会调用 **`onComplete()`** & **`onSubscribe()`**



### `Observable.interval(period, TimeUnit)`

```kotlin
Observable.interval(600, TimeUnit.MILLISECONDS).subscribe(observer)
Thread.sleep(1801) // 必须要
```

- 间隔一定时间弹射出来一个值;
- 不会调用**`onComplete()`**

**interval的处理机制**

> 如果有两个**`Thread.sleep()`**, 将 第一个的时间耗尽之后, 才会开始调用第二个的**`Thread.sleep()`**
>
> interval还是同一个interval



### `Observable.timer(delay, TimeUnit)`

``` kotlin
Observable.timer(20, TimeUnit.MILLISECONDS).subscribe(observer)
Thread.sleep(50) // 必须要
```

- 一段时间后弹出一个值; 
- 会调用**`onComplete()`**, 前提 **`Thread.sleep`**的时间要**大于****`Observable.timer`**的时间



### `Observable.range(start, limit)`

```kotlin
Observable.range(0, 3).subscribe(observer)
```

- 在一个范围内依次调用**`onNext`** 
- 会调用**`onComplete()`**

----



## Subscribe

一个**Observer**需要是实现四个方法

- **`onNext()`**

*      **`onError()`**
*      **`onComplete()`**
*      **`onSubscribe()`**

当我们把**Observable**连接到**Observer**上的时候, 系统会调用这四个方法并把相对应的值传给它们

```kotlin
val observable: Observable<Int> = Observable.range(0, 3)
observable.subscribe({
    println("Next $it")
}, {
    println("Error ${it.message}")
}, {
    println("All Completed")
})
```

```kotlin
Observable.range(0, 3).subscribe(observer)
```



- **Subscribe**可以用来连接**Observer** & **Observable**
- 它有两种形式:
  *      把**`onNext`**等方法, 以参数的形式传出去
  *      直接定义一个**Observer**对象传入
- 如果选择第一种形式, 那么**subscribe**方法是有返回值的, 返回值的类型是**Disposable**
- 如果选择第二种形式, 那么**subscribe**方法是没有返回值的
- 这两种形式中的**`onSubscribe()`**都是一个**(d: Disposable): Unit** 类型的函数



### What can Subscribe attribute do

#### `subscribe()`

**Subscribe**在**ReactiveX**中有几个重载方法

基本模式有两个

- **`subscribe(onNext,onError,onComplete,onSubscribe)`** 因为subscribe 是在Java文件中定义的,不能使用Kotlin的命名参数
- **`subscribe(observer)`**

#### `subscribeBy()`

```kotlin
val list: List<Any> = listOf("One", 2, "Three", "Four", 4.5, "Five", 6.0f)
val observable03: Observable<Any> = list.toObservable()
observable03.subscribeBy(
    onNext = { println(it)},
    onError = {it.printStackTrace()},
    onComplete = { println("Done") }
)
```

- 因为**`subscribeBy`**定义在kotlin中, 所以可以使用命名函数

----



## Disposable

>  **Disposable**对象的**dispose**方法可以停止本次订阅

```kotlin
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
```

> 当**disposable**设置为**disposed**时(**`disposable.dispose()`**)
>
> 那么之后的**`onNext`**方法将不会被执行;
>
> 所以Received只能输出到 5, 因为**disposable**在item == 5时, 被设置为了**disposed**;
>
> 因为这次**`onNext`**还在执行中, 所以会输出5, 但是后面的将会不会被执行



## Hot & Cold Observable

### Cold Observable

```kotlin
val observable05: Observable<Int> = listOf<Int>(1, 2, 3, 4).toObservable()
observable05.subscribe(observer)
observable05.subscribe(observer)
```

- 每一个 **Observer** 都被推送了了从 1-4 的所有值
- 这样的**Observer**被称为**Old Observable**



### Hot Observable

```kotlin
val observable06 = listOf<Int>(1, 2, 3).toObservable().publish()
observable06.subscribe {
    println("Subscription 1: $it")
}
observable06.subscribe {
    println("Subscription 2: $it")
}
observable06.connect()
// 如果订阅晚了, 则会错过一些消息
// 这里抽过了所有, 因为计算机运行速度太快了
observable06.subscribe {
    println("Subscription 3: $it")
}
```

- 使用**publish**方法, 将**Cold Observable**变成 **`ConnectableObservable`**(**`ConnectableObservable`**是**Hot Observable**的一种)
- 在调用**`connect()`**方法之前, 订阅的消息不会发送, 在调用时开始发送消息
- 而**Cold Observable**会在调用**Subscribe**的时候开始发送

> 上面的代码 subscribe 3输出, 因为计算机运行速度太快了
>
> 下面的代码 subscribe 3可以输出

```kotlin
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
```

------



## Subject

**Subject**

* 是Observable与Observer的组合体
* Subject是Hot Observable的一种
* Observable的所有操作符, Subject都有
* 也可以像Observer一样接收值
* 如果你向它的Observer接口传入值, 它会**选择性的**从Observable接口弹出



### `PublishSubject`

```kotlin
val observable01 = Observable.interval(10, TimeUnit.MILLISECONDS)
val subject01 = PublishSubject.create<Long>()
// Observer
observable01.subscribe(subject01)
// Observable
subject01.subscribe{
    println("Received $it")
}
Thread.sleep(60)
println()
```

 使用**`publishSubject.create()`**来创建Subject对象

- **`PublishSubject`**会把所有从**Observer**接口传入的值按照时间顺序**全部**弹出

```kotlin
/**
 * Output：
    Message 1; Received 0
    Message 1; Received 1
    Message 1; Received 2
    Message 2; Received 0 // subscribe2 从0开始接收消息(因为它订阅的Observable是一个Cold Observable, 所以会从头发送)
    Message 2; Received 1
    Message 1; Received 3
	Message 2; Received 2
*/
val observable02 = Observable.interval(100, TimeUnit.MILLISECONDS)
observable02.subscribe{
    println("Message 1; Received $it")
}
Thread.sleep(200)
observable02.subscribe{
    println("Message 2; Received $it")
}
Thread.sleep(300)
```



### `AsyncSubject`

会从**源Observable**(**Subject**的**Observer**接口传入值来自 **源Observable**)接收所有值, 并将最后一个值从**Observable**接口弹出

```kotlin
val observable04 = Observable.just(1, 2, 3, 4)
val subject04 = AsyncSubject.create<Int>()
observable04.subscribe(subject04)
subject04.subscribe(observer)
```

```kotlin
val subject05 = AsyncSubject.create<Int>()
subject05.onNext(1)
subject05.onNext(2)
subject05.subscribe(observer)
subject05.onNext(3)
subject05.subscribe(observer)
subject05.onNext(4)
subject05.onComplete()
println()
```

- **`AsyncSubject`**当且仅当调用**`onComplete`**的时候才会弹出值



### **`BehaviorSubject`**

相当于把**`PublishSubject`** & **`AsyncSubject`**结合
*  **`BehaviorSubject`**会弹出订阅**`BehaviorSubject`**之前的最后一个值(**`AsyncSubject`**的特性) 和
*  订阅之后的所有值(**`PublishSubject`**的特性)

```kotlin
val subject06 = BehaviorSubject.create<Int>()
subject06.onNext(1)
subject06.onNext(2)
subject06.subscribe(observer)
subject06.onNext(3)
subject06.subscribe(observer)
subject06.onNext(4)
subject06.onComplete()
println()
```



### **`ReplaySubject`**

```kotlin
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
```

> **Description**
>
> 当订阅了sub01, sub02, sub03时; 都会输出 1, 2, 3, 4
>
> **调用顺序如下:**
>
> 1. 订阅sub01: sub01输出 1, 2
>
> 2. 订阅了sub02: sub02会在sub01输出 3之前, 输出 1, 2; 然后sub01, sub02 先后输出 3, 所以会有两个 3; 达到replay的效果
>
> 3. 订阅了sub03: sub03会在sub01和sub02输出4之前, 输出 1, 2, 3; 如何sub01, sub02, sub03 先后输出4;
>
> **我觉得输出 sub02 和 sub03的消息 更像是插队式输出**
>
> **意思大概为:**
>
> 三个监听者 去 监听一个 被监听者;
>
> 监听的顺序肯定是先来后到;
>
> 但是因为 sub02插入的时候呢, sub01 已经监听了 Observable的 输出 1, 2
>
> 而sub02也要监听,但是要先把前面欠下的监听输出; 因为observable没有调用onComplete()方法;
>
> 所以在sub02插入之后, 先是输出了属于sub02的 1 & 2
>
> 然后 再根据顺序输出 3
>
> sub03插入同理
>
> 结束之后 onComplete

---



## Operator

### Operator Table

| name                       | title                                                        |
| -------------------------- | ------------------------------------------------------------ |
| Creating                   | 创建新的**Observable**                                       |
| Transforming               | 把**源Observable**的值**进行变化后**再弹出                   |
| Filtering                  | 把**源Observable**的值**选择性**的弹出                       |
| Combining                  | 把多个**源Observable**整合为一个**Observable**               |
| Error Handling             | 当**Observable**遇到**Error**时恢复                          |
| Observable Utility         | 处理**Observable**的**工具集**                               |
| Conditional and Boolean    | **判断**一个或多个Observables/Observable弹出的值             |
| Mathematical and Aggregate | 对一个Observable弹出的**全部值进行处理**                     |
| Backpressure               | 用来处理Observable弹出值的**速度大于**Observer接收值的速度的情况 |
| Connectable Observable     | 把源Observable**转换**为一个**特定**的Observable,使其满足特定要求 |
| Convert Observables        | 把Observable变成一个**数据结构/对象**                        |

[operators line](http://reactivex.io/documentation/operators.html)