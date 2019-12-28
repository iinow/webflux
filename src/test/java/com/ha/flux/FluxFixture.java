package com.ha.flux;

import java.util.Arrays;
import java.util.Random;

public class FluxFixture {

    public static class Connection implements AutoCloseable{
        private final Random rnd = new Random();

        public Iterable<String> getData(){
            if(rnd.nextInt(10) < 3){
                throw new RuntimeException("Communication error");
            }
            return Arrays.asList("Some", "data");
        }

        @Override
        public void close() {
            System.out.println("IO Connection closed");
        }

        public static Connection newConnection(){
            System.out.println("IO Connection created");
            return new Connection();
        }
    }
}
