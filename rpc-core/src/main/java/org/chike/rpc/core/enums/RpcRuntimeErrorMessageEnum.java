package org.chike.rpc.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RpcRuntimeErrorMessageEnum {
    SERVICE_CAN_NOT_BE_FOUND("注册中心中未找到指定服务"),
    PUBLISH_SERVICE_ERROR("发布服务失败"),
    ;

    private final String message;
}
