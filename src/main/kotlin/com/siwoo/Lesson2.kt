package com.siwoo

import java.io.Serializable
import java.nio.file.Paths
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

/**
 * non-nullable & nullable
 *  nullable 은 non-nullable 의 자식 타입.
 *  
 * non-nullable 의 참조 연산
 *   non-nullable 은 참조 연산을 하지 못함.
 *   if (e != null) e else null 식으로 사용해야 됨.
 *   
 * safe call (안전 호출)
 *  e?.length => null 일 경우, null 을 반환
 *  연쇄 호출 가능.
 *  
 *  obj?.obj?.obj?
 *  
 * 엘비스 연산자와 default value.
 *  ?: 연산자로 기본 값 지정.
 *  
 */
data class Company(var manager: Manager?)
data class Manager(var address: Address?)
data class Address(var city: City?)
data class City(var name: String?)

fun nullTest() {
    val x: Int = 3
    val y: Int? = x
//    val x: Int? = 3
//    val y: Int = x
    
    val s: String? = someFun()
    val l = s?.length ?: -1
    println(l)
    
    val city = City("Toronto")
    val address = Address(city)
    val manager = Manager(address)
    val company: Company? = Company(manager)
    
    company?.manager = null
    
    var cityNullable = company?.manager?.address?.city ?: "unknown"  //Toronto
    println(cityNullable)
}

fun someFun(): String? {
    return null
}

/**
 * 다중 비교 구문
 * 
 *  switch 대신 when 을 사용
 *  break 가 필요없음.
 *  가능한 모든 경우를 다 체크해야 된다.
 *  
 * when(o) {
 *      match1 -> value
 *      match2 -> value
 *      else -> value
 *  }
 */
fun noSwitchButWhen() {
    val country = "Korea"
    
    val capital = when (country) {
        "Korea" -> "Seoul"
        "Canada" -> "Toronto"
        "Japan" -> "Tokyo"
        else -> "Unknown"
    }
    println(capital)
    
    println(when {
        country == "Korea" -> "Seoul"
        country == "Canada" -> "Toronto"
        country == "Japan" -> "Tokyo"
        else -> "Unknown"
    })
}


/**
 * 
 * 인덱스 루프.
 * 
 * until
 *  -> 증가, end 값 포함하지 않음
 * ..
 *  -> 증가, end 값 포함
 * downTo
 *  -> 감소, end 값 포함
 *  1. for (i in 0 until N step 2)
 *      pinrtln(i)
 *      
 *  2. val range = 0 until N step 2 (IntProgression)
 */
fun loop() {
    val N = 10
    for (i in 0 until N step 2)
        println(i)
    val range = 0 until N step 2
    for (i in range) println(i)
    for (i in 10 downTo 0)
        println(i)
}

/**
 * Closeable 과 use
 *   리소스의 close 처리를 위해서 try 대신 use 을 사용.
 *   
 *   use {  //it = FileInputStream
 *      
 *   }
 *   
 */
fun closeable() {
    Paths.get("./README.md").toFile()
        .inputStream()
        .use {
            it.bufferedReader().lineSequence().forEach(::println)
        }
    
    val lines: List<String> = Paths.get("./README.md").toFile()
        .inputStream()
        .use { 
            it.bufferedReader().lineSequence().toList()
        }
    lines.forEach(::println)
}

/**
 * 스마트 캐스팅 (smart cast)
 *  
 *  스마트 캐스팅 if
 *  if (o is Type)
 *     // o use as given Type
 *  
 *  스마트 캐스팅 when
 *  when (o) {
 *      is [type] ->    ...
 *      is [type] ->    ...
 *      else -> ...
 *  }
 *  
 *  강제 캐스팅 as
 *      o as [Type]
 *      
 *  안전한 캐스팅 as?
 *      o as? [Type]
 *          => 타입 변환이 실패하면 null 을 리턴.
 *          
 *  동등성과 동일성
 *      ==, !=  동등성
 *      ===, !== 동일성
 */
fun casting() {
    val o: Any = "test"
    
    val length = if (o is String) o.length else -1
    println(length)
    
    println(
        when (o) {
            is String -> o.length
            else -> -1
        }
    )
    
    val casted: String = o as String
    println(casted)
}

/**
 * covariance 과 in & out
 *  제네릭에서 A 가 B 의 부모 타입이라 하더라도
 *  List<A> 와 List<B> 은 아무 관계가 없다.
 * 
 * 언어적 환경 무공변성 때문에 이러한 제네렉 메서드를 작성시 제한전이 많다.
 * 제네릭 메서드에서 타입 안정성을 지키기 위해 out, in 을 사용.
 * 
 * out = 공성
 *  A 가 B 의 부모일때, List<A> 가 List<B> 도 부모.
 *  out 을 사용하여 List<out T> 을 할당해도 안전한 이유는
 *  T 에 대해서 출력만 할 것이기 때문.
 */

fun <T> addAll(left: MutableList<T>, right: MutableList<out T>) {
    for (e in right)
        left.add(e)
}

open class MyParent
class MyChild: MyParent()

interface Bag<out T> {
    fun get(): T
}

class BagImpl: Bag<MyChild> {
    override fun get(): MyChild = MyChild()
}

fun covariance() {
    val s = "A String"
    val a: Any = s  //co variance

    val ls = mutableListOf("s")
    val la: MutableList<Any> = mutableListOf()
    addAll(la, ls)
    
    val bag: Bag<MyParent> = BagImpl()
}

fun main(args: Array<String>) {
//    fieldAndVariable()
//
//    val person = Person("siwoo")
//    println(person.name)    //getter
//    person.show()
//    collection()
//
//    println(sumOfPrimes(10))
//    functionExtension()
    
    nullTest()
    noSwitchButWhen()
    loop()
    
    closeable()
    casting()
}