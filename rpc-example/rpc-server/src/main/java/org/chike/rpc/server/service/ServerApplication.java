package org.chike.rpc.server.service;

import lombok.SneakyThrows;
import org.chike.rpc.core.annotation.EnableRPC;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("org.chike.rpc.server")
@EnableRPC
public class ServerApplication {
    @SneakyThrows
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ServerApplication.class);
        for (;;) {
            System.in.read();
        }
    }
}
