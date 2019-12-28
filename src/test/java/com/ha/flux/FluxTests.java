package com.ha.flux;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Arrays;
import java.util.Random;
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
}
 