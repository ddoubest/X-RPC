package org.chike.rpc.core.spring;

import org.apache.commons.lang3.ArrayUtils;
import org.chike.rpc.core.annotation.EnableRPC;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;

public class RpcBeanSelector implements ImportSelector {
    private static final String SERVER_PACKAGE_NAME =
            "org.chike.rpc.core.transport.netty.server.NettyServer";

    private static final String[] MUST_SELECT = {
            "org.chike.rpc.core.spring.ConsumerBeanPostProcessor",
            "org.chike.rpc.core.spring.ProviderBeanPostProcessor",
    };

    @Override
    @Nonnull
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(EnableRPC.class.getName())
        );

        if (attributes != null) {
            boolean startServer = attributes.getBoolean("startServer");
            if (startServer) {
                return ArrayUtils.add(MUST_SELECT, SERVER_PACKAGE_NAME);
            }
        }

        return MUST_SELECT;
    }
}
