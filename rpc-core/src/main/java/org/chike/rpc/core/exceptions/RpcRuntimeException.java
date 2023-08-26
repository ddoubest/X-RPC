package org.chike.rpc.core.exceptions;

import org.chike.rpc.core.enums.RpcRuntimeErrorMessageEnum;

public class RpcRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 4417014507989130678L;

    public RpcRuntimeException() {
        super();
    }

    public RpcRuntimeException(String message) {
        super(message);
    }

    public RpcRuntimeException(RpcRuntimeErrorMessageEnum msgEnum, String detail) {
        super(msgEnum.getMessage() + ":" + detail);
    }

    public RpcRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcRuntimeException(Throwable cause) {
        super(cause);
    }

    protected RpcRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
