package org.chike.rpc.core.extensions.impl;

import lombok.AllArgsConstructor;
import org.chike.rpc.core.domain.content.RpcResponse;
import org.chike.rpc.core.factory.SingletonFactory;
import org.junit.jupiter.api.Test;

class KryoSerializerTest {
    private static final KryoSerializer kryoSerializer = SingletonFactory.getInstance(KryoSerializer.class);

    @Test
    void serialize() {
        RpcResponse rpcResponse = new RpcResponse(100, "123", new Person("hjh", 1));
        System.out.println(kryoSerializer.serialize(rpcResponse).length);
    }

    @Test
    void deserialize() {
    }
}

@AllArgsConstructor
class Person {
    String name;
    Integer gender;
}