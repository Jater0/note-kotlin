package com.example.kotlin_lib

import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject

fun isEvenOrOdd(n: Int): String = if ((n % 2) == 0) "Even" else "Odd"

fun main(args: Array<String>) {
    val subject: Subject<Int> = PublishSubject.create()
    subject.map{ isEvenOrOdd(it)}.subscribe{ println("The Number is $it")}
    subject.onNext(4)
    subject.onNext(9)
}