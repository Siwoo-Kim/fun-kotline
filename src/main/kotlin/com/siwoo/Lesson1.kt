package com.siwoo

import java.lang.IllegalArgumentException

/**
 * 코틀린 코딩 기법.
 *  1. 상태 변이를 피하라.
 *  2. 상태 변이를 피할 수 없는 경우 그 부분을 추상화하라.
 *  3. 제어 구조를 피하라
 *  4. side effect 을 제한하라.
 *  5. 예외를 던지지 말아라.
 *  
 *  참조 투명성 (referential transparency)
 *      - 함수는 같은 인자에 대해서 항상 같은 값을 반환해야 된다.
 *      - side effect 가 없고 외부의 상태에 의존적이지 않다.
 *          (외부의 상태를 변경하지 않고, 의존성도 존재하지 않는다.)
 *          1. deterministic. 결정적이다. 즉, 언제나 함수의 결과가 예상 가능하다.
 *          2. 자기 완결적이다. 즉, 어떤 코드에서도 호환 가능하다.
 *          
 *  치환 모델. (substitution model)
 *      참조 투명한 값은 같은 인자에 대해서 항상 같은 값을 반환하므로
 *      함수의 호출 = 함수의 반환값.
 *      반환값을 변수에 저장하지 않고 바로 값처럼 사용하는 방식.
 *
 */

/**
 * substitution model example
 */
fun main(arg: Array<String>) {
    val x = add(multi(3, 2), multi(4, 5))
    println(x)
}

fun multi(a: Int, b: Int) = a * b

fun add(a: Int, b: Int): Int {
    log("Returning ${a + b} as the result of ${a} + ${b}")
    return a + b
}

fun log(s: String) {
    println(s)
}

/**
 * Side effect
 */
fun buyDonutSideEffect(credit: CreditCard): Donut {
    val donut = Donut()
    credit.charge(Donut.PRICE)  //side effect
    return donut
}

class Donut {
    companion object {  //Singleton
        const val PRICE = 5
    }
}

class CreditCard(var credit: Int = 50) {
    
    fun charge(amount: Int) {
        credit -= amount
    }
}

/**
 * 부수 효과를 수행하는 대신, 그 효과을 추상화.
 */
class Payment(val credit: CreditCard, val amount: Int) {
    fun combine(that: Payment): Payment = 
        if (credit == that.credit) Payment(credit, amount + that.amount) 
        else throw IllegalArgumentException("Card's not match")
    
    companion object {
        fun groupByCard(payments: List<Payment>): List<Payment> = 
            payments.groupBy { it -> it.credit }.values.map { it -> it.reduce(Payment::combine) }
    }
}

class Purchase(val donuts: List<Donut>, val payment: Payment)

fun buyDonut(credit: CreditCard): Purchase {
    return buyDonut(credit)
}

fun buyDonuts(quantity: Int = 1, credit: CreditCard): Purchase {
    return Purchase(List(quantity) {
        Donut()
    }, Payment(credit, Donut.PRICE * quantity))
}

/**
 * 정리.
 *  1. 함수의 side effect 을 없애고, 외부와 의존성을 줄이므로써 안전하게 프로그래밍 할 수 있다.
 *  2. 함수의 결과가 deterministic 이고, 외부 상태에 의존하지 않다면 예상하기 좋고, 테스트 하기 쉬운 프로그래밍을 할 수 있다.
 *  3. 상태 변이하는 부분을 추상화하라.
 *  4. 불변성, 참조 투명성은 다중 스레드 환경에서 꼭 필요하다.
 *  
 */