package org.appsugar.archetypes.hibernate;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;

/**
 * 每个实体类对应一个ID生成器实例对象
 *
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.hibernate
 * @className SnowflakeIdGenerator
 * @date 2021-03-27  09:56
 */
@Slf4j
public class SnowflakeIdGenerator implements IdentifierGenerator, Configurable {
    /**
     * 在集群下,需要修改workerId,datacenterId
     */
    SnowflakeIdWorker worker = new SnowflakeIdWorker(0, 0);

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return worker.nextId();
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        log.debug(" configure id generator {} params is {}", this.hashCode(), params);
    }
}
