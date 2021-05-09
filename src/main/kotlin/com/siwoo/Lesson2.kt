package com.siwoo

import java.io.Serializable
import java.time.Instant


/**
 * 필드와 변수.
 *  1. 선언
 *     val [name]: type
 *  2. 상수 와 가변 변수
 *      val, var
 *  3. 타입 추론.
 *      타입을 추론 가능하므로 생략 가능.
 *  4. lazy init
 *      변수가 사용하기 직전까지 초기화를 지연.
 *      
 *      상수 지연 초기화
 *      val [name]: type? by lazy { 초기화.. }
 *      가변 필드 지연 초기화
 *      lateinit var [name]: type
 *      
 *      질문점.
 *          컬렉션을 돌면서 올바른 답을 찾아야할때는 이 방식으로 가능할까
 *          
 *  5. nullable type
 *      val [name]: type?
 */
fun fieldAndVariable() {
    val name: String by lazy(::getName) // not eval yet
    println("hello1")
    val name2: String by lazy { name }
    println("hello2")
    println(name)   // eval now
    println(name2)
}

fun getName(): String {
    return "siwoo"
}

/**
 * 클래스 & 인터페이스
 * 
 *  1. 코틀린 클래스는 기본적으로 public
 *  
 *  2. 가시성 private, protected, internal
 *      internal - 정의된 모듈 안에서만 클래스에 접근 가능. (같은 jar 에 속한 클래스)
 *          모듈 - 한꺼번에 컴파일되는 파일의 묶음.
 *          
 *      protected - 자바와 달리 상속하는 클래스는 접근 가능, 같은 패키지에선 접근 불가
 *      
 *  3. 코틀린 클래스는 기본적으로 final class   
 *  
 *  4. 생성자 정의
 *      class [className] constructor(args...) {
 *          init {
 *              생성자 초기화 블
 *          }
 *      }
 *      
 *  5. 자동 getter
 *  
 *  6. 코틀린 파일에 여러 public 클래스을 정의할 수 있고, 파일 이름과 클래스 이름이 매칭할 필요가 없다.
 *  
 *  7. 생성자 오버라이딩.
 *      constructor(args..): this()
 *      
 *  8. equals & hashCode & copy & toString
 *      data class 에 한해서 컴파일러에 의해 자동으로 생성.
 *  
 *  9. componentN & destructing object
 *      프로퍼티의 정의 순서대로 접근 가능 기능.
 *  
 *  10. 정적 메서드.
 *      companion object 동반 객체에 정의.
 *      
 *   11. 싱글톤
 *      object [singletonName] {
 *          ..
 *      }
 *  
 *  12. 원시 타입.
 *      코틀린에는 원시 타입이 없다.
 *      원시 타입이 없으므로 boxing/unboxing 도 없다.
 *      
 */
data class Person(val name: String,
             //provide default value
             val registered: Instant = Instant.now())
    //implementing interface
    :Serializable, Comparable<Person> {
    
    //constructor overloading
    constructor(name: Name): this(name.toString())
    
    fun show() {
        //componentN to access all properties
        println("${component1()}'s registration date: ${component2()}")
    }
    
    // 동반 객
    companion object {
        
        // 정적 메서드
        fun newInstance(name: String, instant: Instant?): Person {
            if (instant == null) return Person(name)
            else return Person(name, instant)  //다른 방법이 있지 싶은데...
        }
        
        fun show(people: List<Person>) {
            //destructing object
            for ((name, regis) in people)
                println("$name's registration date: $regis")
        }
    }
    
    override fun compareTo(other: Person): Int {
        return String.CASE_INSENSITIVE_ORDER.compare(name, other.name)
    }
}

data class Name(val name: String) {
    override fun toString(): String {
        return name
    }
}

/**
 * 컬렉션
 *  1. immutable & mutable collection
 *      listOf -> immutable
 *      mutableListOf -> mutable
 *      
 *  2. + 연산자. (중위 확장 함수. infix)
 *      불변 컬렉션과 가변 컬렉션의 결과는 같다.
 *      (기존 컬렉션에는 아무런 영향을 끼치지 않고 새로운 컬렉션을 생성해 리턴)
 */
fun collection() {
    val list = listOf(1, 2, 3)
    val list2 = list + 4
    println(list)   // immutable
    println(list2)  // new list
    val list3 = list + list
    println(list3)
    
    val mlist = mutableListOf(1, 2, 3)
    val mlist2 = mlist.add(4)   // boolean
    val mlist3 = mlist.addAll(mlist)    // boolean
    println(mlist)
    println(mlist2)
    println(mlist3)
}

/**
 * 함수
 *  1. 로컬 함수
 *      함수 내부에 함수를 정의.
 *      이 함수은 함수 외부의 프러퍼티에 접근 가능 - closure  
 *      
 *  2. 함수 오버라이딩.
 *      함수를 오버라이딩할 때, override 키워드를 생략할 수 없다.
 *
 */
fun sumOfPrimes(limit: Int): Long {
    val seq: Sequence<Long> = sequenceOf(2L) + generateSequence(3L, { it + 2 })
        .takeWhile { it < limit }
    
    fun isPrime(n: Long): Boolean = 
        //the inner function access variable in outside (clusure)
        seq.takeWhile { it * it <= n }.all { n % it != 0L }
    return seq.filter(::isPrime).sum()
}

/**
 * 확장 함수 (extension function)
 * 
 *  클래스의 내부가 아닌 외부에서 함수를 추가하고, 해당 함수를 인스턴스 함수처럼 사용.
 *  -> 마치 익명 클래스를 만들어 추가 함수를 정의한것처럼 사용.
 *  
 *  * fold(초기값, mergeFunc) 
 *      초기값을 주고, 컬렉션의 모든 값을 집계(혹은 누적 - accumulate)
 * 
 *  람다 (lambda)
 *      1. 코틀린 람다는 중괄호 사이에 람다가 정의.
 *      2. 함수의 마지막 인자가 람다일때는 괄호 밖에 람다를 정의. (currying)
 *      3. 람다에선 맨 마지막 값이 return 된다.
 *      
 */
fun functionExtension() {
    fun <T> List<T>.length() = this.size
    var ints = listOf(1, 2, 3, 4)
    val length = ints.length()
    println(length)
    
    fun List<Int>.product() = this.fold(1) {a, b -> a * b}  //function extension & currying
    ints = listOf(1, 2, 3, 4, 5, 6, 7)
    println(ints.product())
    
    fun triple(list: List<Int>): List<Int> = list.map { it -> it * 3 }  //lambda
    
}

//fun <T> length(list: List<T>) = list.size

fun <T> List<T>.length() = this.size

fun main(args: Array<String>) {
    fieldAndVariable()
    
    val person = Person("siwoo")
    println(person.name)    //getter
    person.show()
    collection()
    
    println(sumOfPrimes(10))
    functionExtension()
}
