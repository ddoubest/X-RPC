package org.chike.rpc.client;

import org.chike.rpc.core.annotation.Consumer;
import org.chike.rpc.core.annotation.EnableRPC;
import org.chike.rpc.example.service.HelloService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Component
@ComponentScan("org.chike.rpc.client")
@EnableRPC(startServer = false)
public class ClientApplication {
    private static HelloService helloService;

    @Consumer(group = "org.chike.rpc", version = "1.0.0")
    private void setHelloService(HelloService helloService) {
        ClientApplication.helloService = helloService;
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ClientApplication.class);

        System.out.println(helloService.sayHello());
    }
}
