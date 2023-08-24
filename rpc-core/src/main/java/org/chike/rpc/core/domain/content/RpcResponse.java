package org.chike.rpc.core.domain.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.chike.rpc.core.codec.ContentEncode;
import org.chike.rpc.core.enums.RpcResponseCodeEnum;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements ContentEncode, Serializable {
    private static final long serialVersionUID = -5633326222795826888L;

    private Integer responseCode;
    private String responseMsg;

    private Object result;

    public static RpcResponse success(Object result) {
        RpcResponse response = new RpcResponse();
        response.setResponseCode(RpcResponseCodeEnum.SUCCESS.getCode());
        response.setResponseMsg(RpcResponseCodeEnum.SUCCESS.getMessage());
        if (null != result) {
            response.setResult(result);
        }
        return response;
    }

    public static RpcResponse fail(RpcResponseCodeEnum rpcResponseCodeEnum) {
        RpcResponse response = new RpcResponse();
        response.setResponseCode(rpcResponseCodeEnum.getCode());
        response.setResponseMsg(rpcResponseCodeEnum.getMessage());
        return response;
    }
}
