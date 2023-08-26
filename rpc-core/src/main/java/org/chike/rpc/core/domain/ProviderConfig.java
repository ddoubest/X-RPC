package org.chike.rpc.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProviderConfig {
    private Object provider;

    private String group = "";
    private String version = "";

    public String getRpcServiceName() {
        return provider.getClass().getInterfaces()[0].getCanonicalName() + "#" + this.getGroup() + "#" + this.getVersion();
    }
}
