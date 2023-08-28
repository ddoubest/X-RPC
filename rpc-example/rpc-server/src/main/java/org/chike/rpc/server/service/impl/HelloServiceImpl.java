package org.chike.rpc.server.service.impl;

import org.chike.rpc.core.annotation.Provider;
import org.chike.rpc.example.service.HelloService;

@Provider(group = "org.chike.rpc", version = "1.0.0")
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello() {
        return "Hello X-RPC!";
    }
}
