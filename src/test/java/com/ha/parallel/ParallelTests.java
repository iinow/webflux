package com.ha.parallel;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;

public class ParallelTests {

    @Test
    public void parallel1(){
        int[] arr = {1,2,3,4,5,6,7,8,9,10};

        Arrays.stream(arr).parallel().forEach(v -> {
            System.out.println("실행: " + v +", time: " + new Date().toString());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
