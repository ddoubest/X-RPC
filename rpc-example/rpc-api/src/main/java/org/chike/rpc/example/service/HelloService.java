package org.chike.rpc.example.service;

import java.util.concurrent.CompletableFuture;

public interface HelloService {
    String sayHello();
    CompletableFuture<String> sayHelloAsync();
}
