package org.chike.rpc.client;

import lombok.SneakyThrows;
import org.chike.rpc.core.annotation.Consumer;
import org.chike.rpc.core.annotation.EnableRPC;
import org.chike.rpc.core.constant.RpcConstants;
import org.chike.rpc.example.service.HelloService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@ComponentScan("org.chike.rpc.client")
@EnableRPC(startServer = false)
public class ClientApplication {
    private static HelloService helloService;

    @Consumer(group = "org.chike.rpc", version = "1.0.0")
    private void setHelloService(HelloService helloService) {
        ClientApplication.helloService = helloService;
    }

    @SneakyThrows
    @SuppressWarnings("BusyWait")
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ClientApplication.class);

        System.out.println("客户端同步调用开始");
        long start = System.currentTimeMillis();
        System.out.println(helloService.sayHello());
        System.out.println("客户端同步调用结束：" + (System.currentTimeMillis() - start));

        CompletableFuture<String> result = CompletableFuture.supplyAsync(() -> helloService.sayHello());
        while (!result.isDone()) {
            System.out.println("客户端异步模式~");
            Thread.sleep(1000);
        }
        System.out.println(result.get());


        System.out.println("服务端异步调用开始");
        start = System.currentTimeMillis();
        CompletableFuture<String> future = helloService.sayHelloAsync();
        System.out.println("服务端异步调用结束：" + (System.currentTimeMillis() - start));
        System.out.println("服务端异步结果获取：" + future.get(RpcConstants.TIMEOUT, TimeUnit.MILLISECONDS));
        System.out.println("服务端异步获取结果耗时：" + (System.currentTimeMillis() - start));
    }
}
