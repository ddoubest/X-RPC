package org.chike.rpc.core.domain.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.chike.rpc.core.codec.ContentEncode;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements ContentEncode, Serializable {
    private static final long serialVersionUID = -1372275369287319053L;

    private String interfaceName;
    private String methodName;
    private Class<?>[] argsClass;
    private Object[] argsInstance;

    private String group;
    private String version;

    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }
}
