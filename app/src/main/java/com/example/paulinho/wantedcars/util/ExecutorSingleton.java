package com.example.paulinho.wantedcars.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public interface ExecutorSingleton {
    ExecutorService executor = Executors.newFixedThreadPool(4);
}
