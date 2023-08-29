package org.chike.rpc.server.service.impl;

import lombok.SneakyThrows;
import org.chike.rpc.core.annotation.Provider;
import org.chike.rpc.example.service.HelloService;

import java.util.concurrent.CompletableFuture;

@Provider(group = "org.chike.rpc", version = "1.0.0")
public class HelloServiceImpl implements HelloService {
    @SneakyThrows
    @Override
    public String sayHello() {
        Thread.sleep(5000);
        return "Hello X-RPC!";
    }

    @Override
    public CompletableFuture<String> sayHelloAsync() {
        return CompletableFuture.supplyAsync(this::sayHello);
    }
}
