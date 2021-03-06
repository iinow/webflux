# webflux

* H2 Console 접근 URL `http://localhost:8082`

* 리액티브 정의 개념 [링크](https://www.reactivemanifesto.org/ko/glossary)

<hr/>

## 왜 리액티브인가

* 기존 블로킹 처리 방식

```java
@RequestMapping("/resource")
public Object processRequest(){
    RestTemplate template = new RestTemplate();

    //응답 값을 가져올 때까지 해당 스레드는 대기를 한다. 
    ExamplesCollection result = template.getForObject(
        "http://example.com/api/resource2",
        ExamplesCollection.class
    )

    processResultFurther(result);
}
```

![](images/1.png)
* 스레드 B 의 작업이 끝날 때까지는 스레드 A 다른 작업을 처리할 수 없다.
    
* 자원을 효율적으로 사용할 수 없음
* 아마존이나 월마트 등 기업에서 증가한 부하에 대해 처리를 하지 못하고 장애가 일어난 일도 있음

* 위와 같은 문제를 해결하기 위해 리액티브가 필요로 함

<hr/>

## 리액티브 선언문 [링크](https://www.reactivemanifesto.org/ko)
![](images/2.svg)
* 가치
    * 응답성: 시스템이 가능한 즉각적으로 응답하는 것
* 매개체
    * 탄력성: 많고 적든 다양한 작업 부하에서 응답성을 유지하는 것
    * 복원력: 시스템 장애 발생 시에도 응답을 유지하는 것
* 표현 방식
    * 메시지 기반: 비동기 메시지 전달에 의존하여 구성 요소 간의 느슨한 결합

> 이러한 원칙에 따라 구축된 시스템은 모든 구성 요소가 독립적이고 격리돼 있기 때문에 유지 보수 및 확장 용이

<hr/>

## 스프링 5.0 이전에 논블로킹 처리 방법

1. Thread 
```java
public void calculate(Input value, Consumer<Output> c){
    new Thread(() -> {
        Output result = template.getForObject(...);
        ...
        c.accept(result);
    }).start();
}
```
* 개발자가 멀티 스레딩을 잘 이해를 해야한다. 
* 콜백 지옥에 빠지기 쉽다.

2. Future
```java
void process(){
    Future<Output> future = scService.calculate(input);
    ...
    Output output = future.get();
    ...
}
```
* Future 클래스를 사용함으로써 콜백 지옥을 막을 순 있지만 future.get() 을 가져오는 동안에 스레드가 차단됨 

3. java.util.concurrent.CompletionStage 선언
```java
interface ShoppingCardService {
    CompletionStage<Output> calculate(Input value);
}

class OrderService {
    private final ShoppingCardService scService;

    void process() {
        Input input = ...;
        scService.calcuate(input)
            .thenApply(out1 -> {...})
            .thenCombine(out2 -> {...})
            .thenAccept(out3 -> {...});
    }
}
```
* Spring 4 MVC 에서는 CompletionStage 를 지원하지 않음 

4. AsyncRestTemplate 사용

```java
AsyncRestTemplate template = new AsyncRestTemplate();
SuccessCallback onSuccess = r -> {...};
FailureCallback onFailure = e -> {...};
ListenableFuture<?> response = template.getForEntity(
    "http://example.com/api/examples",
    ExamplesCollection.class
);
response.addCallback(onSuccess, onFailure);
```
* 코드 작성 지저분함
* 스프링 프레임워크는 블로킹 네트워크 호출을 별도의 스레드로 래핑한다. 
* 스프링 MVC 는 모든 구현체가 각각의 요청에 별도의 스레드를 할당하는 서블릿 API를 사용
* 많은 수의 스레드를 활성화 시켜서 비효울적임

> 위와 같은 문제로 리액티브 프로그래밍 기술은 스프링 프레임워크 안에 잘 통합돼 있지 않아서 새로 구현하기로 함

<hr/>

## 스프링을 이용한 리액티브 프로그래밍

### 초기 해결법

* 옵저버 패턴
* @EventListener를 사용한 발행 구독 패턴

위와 같은 해결 방법은 있지만 스레드 풀을 사용해서 처리를 하는데 리액티브 프로그래밍에서는 스레드 풀을 사용하지 않음

또 구독자의 수와 관계없이 하나의 이벤트 스트림을 생성하는데 구독자가 없을 때도 생성한다. -> 하드웨어 수명 단축

<hr/>

### RxJava Operators [링크](https://rxmarbles.com/)

1. filter

![](images/3.PNG)

2. find

![](images/4.PNG)

3. withLatestFrom

![](images/5.PNG)

<hr/>

### RxJava 사용 이점

* 다양한 operators 사용 가능 
* 프로듀서-컨슈머 관계를 해지할 수 있는 채널이 일반적으로 존재함으로 생성, 소비되는 이벤트의 양 조절 가능 -> 데이터 작성시에만 필요하고 이후에는 사용되지 않는 CPU 사용량 줄일 수 있음
  

<hr/>

### 리액티브 라이브러리 RxJava

* Rx 는 MS 내부에서 대규모 비동기 및 데이터 집약적 인터넷 서비스 아키텍쳐에 적합한 프로그래밍 모델을 실험해서 2007년에 탄생함

* 후에 Rx.NET 시작으로 Javascript, C++, Ruby, Object-C 의 언어에서도 사용할 수 있는 라이브러리를 제공하게됨

* 넷플릭스의 벤 크리스텐슨은 Rx.NET 을 자바 플랫폼으로 이식하고 RxJava 라이브러리 오픈 소스 공개함

* 넷플릭스에서 엄청난 양의 인터넷 트래픽을 처리해야하는 문제가 있었는데 RxJava 로 처리하게 됨

