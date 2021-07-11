package org.appsugar.archetypes.system.endpoint;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 提供给管理平台调用,spring boot admin,k8s pod preStop, manually curl localhost..
 * 设置程序预关闭
 * 比如注销dubbo所有注册于zk中的服务,在eureka中注销当前服务.
 */
@Component
@Endpoint(id = "preStop")
public class GracefulShutdownEndpoint {
    private AtomicBoolean flag = new AtomicBoolean();

    @ReadOperation
    public String preStop() {
        if (flag.compareAndSet(false, true)) {
            doPreStop();
        }
        return "preStop method Invoked";
    }

    private void doPreStop() {
        //TODO 注销eureka, 注销dubbo服务
    }
}
