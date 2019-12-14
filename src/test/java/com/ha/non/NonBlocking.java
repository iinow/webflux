package com.ha.non;

import java.util.Date;
import java.util.stream.IntStream;

public class NonBlocking {

    public static int cnt = 0;

    public synchronized static void add(){
        cnt++;
    }

    public static void main(String[] args) {
//        IntStream.range(1, 10)
//                .parallel()
//                .forEach(i -> Process.newInstace().process());
        long id = Thread.currentThread().getId();
        System.out.println(id + ", start" + new Date().toString());

        Process p = Process.newInstace();
//        new Thread(() -> {
//            p.process(false);
//        }).start();
        p.process(true);
        p.process(true);
        p.process(true);
    }

    static class Process {

        public static Process newInstace(){
            return new Process();
        }

        public void process(boolean self){
            NonBlocking.add();
            long id = Thread.currentThread().getId();
            System.out.println("id: "+id+","+NonBlocking.cnt + " 출력" + new Date().toString()+", "+self);
            if(NonBlocking.cnt % 2 == 0){
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) { }
            }
        }
    }
}


