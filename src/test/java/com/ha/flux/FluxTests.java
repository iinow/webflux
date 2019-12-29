package com.ha.flux;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Processor;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.*;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;

public class FluxTests {

    @Test
    public void flux1(){
        Flux.range(1, 2)
                .repeat()
                .subscribe(v -> {
                    System.out.println(v + ", ");
                });
    }

    @Test
    public void flux2(){
        Flux.range(2010, 9)
                .subscribe(System.out::println);
    }

    @Test
    public void flux3(){
        Flux.fromArray(new Integer[]{1,2,3,4})
                .subscribe(System.out::println);
    }

    @Test
    public void flux4(){
        Flux.fromIterable(Arrays.asList(1,2,3,4))
                .subscribe(System.out::println);
    }

    /**
     * scan 연산자는 reduce 와 다르게 중간 결과 값을 다음 스트림으로 보낸다.
     * */
    @Test
    public void scan(){
        Flux.range(1,5)
                .scan(0, Integer::sum)
                .subscribe(System.out::println);
    }

    @Test
    public void reduce(){
        Flux.range(1, 5)
                .reduce(0, Integer::sum)
                .subscribe(System.out::println);
    }

    /**
     * then, thenMany, thenEmpty
     *
     * @return 4,5
     * */
    @Test
    public void thenMany(){
        Flux.just(1,2,3)
                .thenMany(Flux.just(4,5))
                .subscribe(System.out::println);
    }

    @Test
    public void concat(){
        Flux.concat(
            Flux.range(1, 3),
            Flux.range(4, 2),
            Flux.range(6, 5)
        ).subscribe(System.out::println);
    }

    @Test
    public void buffer(){
        Flux.range(1, 13)
            .buffer(4)
            .subscribe(System.out::println);
    }

    @Test
    public void windowUntil(){
        Flux<Flux<Integer>> windowedFlux = Flux.range(101, 20)
            .windowUntil(this::isPrime, true);
        windowedFlux.subscribe(window -> window.collectList().subscribe(System.out::println));
    }

    private boolean isPrime(int v){
        return true;
    }

    @Test
    public void groubBy(){
        Flux.range(1, 10)
                .groupBy(v -> v % 2 == 0)
                .map(Flux::collectList)
                .flatMap(list -> list)
                .subscribe(System.out::println);
    }

    /**
     * delayElements 는 1 밀리세컨드 간격으로 스트림을 전달하고
     * sample 은 20 밀레세컨드 간격으로 가장 마지막 값을 전달한다.
     * */
    @Test
    public void sample(){
        Flux.range(1, 100)
                .delayElements(Duration.ofMillis(1))
                .sample(Duration.ofMillis(20))
                .subscribe(System.out::println);

    }

    @Test
    public void reactiveSequenceToBlocking(){
        Iterable<Integer> iter = Flux.just(1, 2, 3, 4).toIterable();

    }

    @Test
    public void signal(){
        Flux.range(1, 3)
                .doOnNext(e -> System.out.println("data: "+e))
                .materialize()
                .doOnNext(e -> System.out.println("signal: "+e))
                .dematerialize()
                .collectList()
                .subscribe(r -> System.out.println("result: " + r));
    }

    /**
     * FluxSink Type 으로 전송한다. push
     *
     * 아래와 같은 방법은 기본 배압과 취소 전략을 사용해 비동기 API 를 적용할 때 유용하게 사용 가능
     * create 팩토리 메서드는 FluxSink 인스턴스를 추가로 직렬화하므로 다른 스레드에서 이벤트를 보낼 수 있음ㅁ
     * 두 메서드 모두 오버플로 전략을 재정의할 수 있음
     *
     * generate 다음 새 값을 생성하기 전에 새 값이 동기적으로 구독자에게 전파된다.
     * */
    @Test
    public void pushCreateGenerate(){
        Flux.push(emitter -> IntStream.range(2000, 3000)
                .forEach(emitter::next))
            .delayElements(Duration.ofMillis(1))
            .subscribe(e -> System.out.println("1 onNext: " + e));

        Flux.create(emitter -> emitter.onDispose(() -> System.out.println("Disposed")))
                .subscribe(e -> System.out.println("2 onNext: " + e));

        Flux.generate(
                () -> Tuples.of(0L, 1L),
                (state, sink) -> {
                    System.out.println("generated value: " + state.getT2());
                    sink.next(state.getT2());
                    long newValue = state.getT1() + state.getT2();
                    return Tuples.of(state.getT2(), newValue);
                })
                .delayElements(Duration.ofMillis(1))
                .take(7)
                .subscribe(e -> System.out.println("3 onNext: " + e));
    }

    /**
     * 일횟성 리소스 만드는 방법
     * */
    @Test
    public void using(){
        Flux<String> ioRequestResults = Flux.using(
                FluxFixture.Connection::newConnection,
                connection -> Flux.fromIterable(connection.getData()),
                FluxFixture.Connection::close
        );

        ioRequestResults.subscribe(
                data -> System.out.println("Received data: " + data),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Stream finished"));
    }

    /**
     * 리소스의 라이프 사이클 관리 가능
     * ex) Transaction
     * */
    @Test
    public void usingWhen(){
//        Flux.usingWhen()
    }

    private Random random = new Random();

    private Flux<String> recommendedBooks(String userId){
        return Flux.defer(() -> {
            if(random.nextInt(10) < 7){
                return Flux.<String>error(new RuntimeException("Err"))
                        .delaySequence(Duration.ofMillis(100));
            }else{
                return Flux.just("Blue Mars", "The Expanse")
                        .delayElements(Duration.ofMillis(50));
            }
        });
    }

    @Test
    public void exceptionHandle() throws InterruptedException {
        Flux.just("user-1")
            .flatMap(user ->
                recommendedBooks(user)
                    .retryBackoff(5, Duration.ofMillis(100))
                    .timeout(Duration.ofSeconds(3))
                    .onErrorResume(e -> Flux.just("The Martian")))
            .subscribe(
                b -> System.out.println("onNext: " + b),
                e -> System.out.println("onError: " + e),
                () -> System.out.println("onComplete")
            );
        Thread.sleep(10000);
    }

    /**
     * 구독자가 오기 전에 데이터를 미리 생성하지 않는다.
     * */
    @Test
    public void coldStream(){
        Flux<String> coldPublisher = Flux.defer(() -> {
                System.out.println("generate uuid");
                return Flux.just(UUID.randomUUID().toString());
            }
        );
        System.out.println("No data was generated so far");
        coldPublisher.subscribe(e -> System.out.println("onNext: " + e));
        coldPublisher.subscribe(e -> System.out.println("onNext: " + e));
        System.out.println("Data was generated twice for two subscribers");
    }

    @Test
    public void hotStream(){
        Flux<String> hotPublisher = Flux.just(UUID.randomUUID().toString());
        hotPublisher.subscribe(e -> System.out.println("onNext: " + e));
        hotPublisher.subscribe(e -> System.out.println("onNext: " + e));
    }

    /**
     * coldPublisher -> hotPublisher 로 변환 ConnectableFlux 를 사용한다.
     * */
    @Test
    public void connectableFlux(){
        Flux<Integer> source = Flux.range(0, 3)
            .doOnSubscribe(s ->
                System.out.println("new subscription for the cold publisher"));
        ConnectableFlux<Integer> conn = source.publish();

        conn.subscribe(e -> System.out.println("[Subscriber 1] onNext: " + e));
        conn.subscribe(e -> System.out.println("[Subscriber 2] onNext: " + e));

        //기존 coldPublisher
//        source.subscribe(e -> System.out.println("[Subscriber 1] onNext: " + e));
//        source.subscribe(e -> System.out.println("[Subscriber 2] onNext: " + e));

        System.out.println("all subscribers are ready, connecting");
        conn.connect();
    }

    /**
     * The Data caching at 1 seconds After ColdPublisher generated
     * */
    @Test
    public void cache() throws InterruptedException {
        Flux<Integer> source = Flux.range(0, 2)
                .doOnSubscribe(s -> System.out.println("new subscription for the cold publisher"));

        Flux<Integer> cachedSource = source.cache(Duration.ofSeconds(1));

        cachedSource.subscribe(e -> System.out.println("[S 1] onNext: " + e));
        cachedSource.subscribe(e -> System.out.println("[S 2] onNext: " + e));

        Thread.sleep(1200);

        cachedSource.subscribe(e -> System.out.println("[S 3] onNext: " + e));
    }

    /**
     * ColdPublisher 의 Stream 을 공유한다.
     * */
    @Test
    public void share() throws InterruptedException {
        Flux<Integer> source = Flux.range(0, 5)
            .delayElements(Duration.ofMillis(1000))
            .doOnSubscribe(s -> System.out.println("new subscription for the cold publisher"));

        Flux<Integer> cachedSource = source.share();

        cachedSource.subscribe(e -> System.out.println("[S 1] onNext: " + e));
        Thread.sleep(4000);
        cachedSource.subscribe(e -> System.out.println("[S 2] onNext: " + e));
        Thread.sleep(10000);
    }

    /**
     * 정확한 시간 지연을 보장하지 않는다.
     * Java ScheduledExecutorService 를 사용하기 때문에
     * */
    @Test
    public void elapsed(){
       Flux.range(0, 5)
            .delayElements(Duration.ofMillis(100))
            .elapsed()
            .subscribe(e -> System.out.println("Elapsed " + e.getT1() + " ms: " + e.getT2()));
    }

    /**
     * 스트림이 올때마다 조합하고 변환한다.
     * 몇 명의 구독자가 오든 항상 동일한 변환 작업을 수행한다.
     * */
    @Test
    public void transform(){
        Function<Flux<String>, Flux<String>> logUserInfo =
                stream -> stream.index()
                    .doOnNext(tp -> System.out.println("["+tp.getT1()+"] User: " + tp.getT2()))
                    .map(Tuple2::getT2);

        Flux.range(1000, 3)
            .map(i -> "user-" + i)
            .transform(logUserInfo)
            .subscribe(e -> System.out.println("onNext: " + e));
    }

    private boolean b = false;
    /**
     * 구독자가 올때마다 다른 변환 작업을 수행한다.
     * compose deprecated 되고 transformDeferred 를 사용해야함
     * */
    @Test
    public void compose(){
        Function<Flux<String>, Flux<String>> logUserInfo = stream -> {
            if(b){
                b = false;
                return stream
                        .doOnNext(e -> System.out.println("[path A] User: " + e));
            }else {
                b = true;
                return stream
                        .doOnNext(e -> System.out.println("[path B] User: " + e));
            }
        };

        Flux<String> publisher = Flux.just("1", "2")
                .transform(logUserInfo);
//                .transformDeferred(logUserInfo);
//                .compose(logUserInfo);

        publisher.subscribe();
        publisher.subscribe();
    }

    @Test
    public void processer(){
        FluxProcessor.just(1, 2, 3, 4)
            .subscribe(System.out::println);
    }

    @Test
    public void debug(){
        Hooks.onOperatorDebug();
        Flux.just(1).log();

    }

    /**
     * any -> Mono<Boolean>
     * */
    @Test
    public void any(){
        Flux.just(2, 4, 5, 6)
            .any(v -> v % 2 == 1)
            .subscribe(exist -> System.out.println("event " + exist));
    }

    @Test
    public void distinctUntilChanged(){
        Flux.just(1, 1, 1, 2, 2, 3, 2, 1, 1, 4)
            .distinctUntilChanged()
            .subscribe(System.out::println);
    }

    @Test
    public void pressureBuffer(){
        Flux.just(1, 2, 3, 4)
            .onBackpressureBuffer(2)
            .subscribe(System.out::println);
    }

    @Test
    public void pressureDrop(){
        Flux.just(1, 2, 3, 4)
            .onBackpressureDrop(v -> {
                System.out.println("onBackpressureDrop, v: " + v);
            })
            .subscribe(System.out::println);
    }

    @Test
    public void pressureLast(){
        Flux.just(1, 2, 3, 4)
            .onBackpressureLatest()
            .subscribe(System.out::println);
    }

    @Test
    public void publishOn(){
        Scheduler scheduler = Schedulers.elastic();

        Flux.range(0, 100)
            .map(String::valueOf)
            .filter(s -> s.length() > 1)
            .publishOn(scheduler)
            .map(s -> s + "!")
            .subscribe();
    }
}
 