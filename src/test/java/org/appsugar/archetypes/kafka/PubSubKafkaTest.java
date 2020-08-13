package org.appsugar.archetypes.kafka;

import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class PubSubKafkaTest extends BaseKafkaTest {
    private static final String topic = "private_topic";
    private static final int times = 4;
    private CountDownLatch latch = new CountDownLatch(times);

    @Test
    @SneakyThrows
    public void testPubSub() {
        String value = "hello times ";
        for (int i = 0; i < times; i++) {
            template.send(topic, value, value + ":" + i).get();
        }
        latch.await(5, TimeUnit.SECONDS);
        logger.debug("testPubSub success");
    }

    @KafkaListener(topics = PubSubKafkaTest.topic)
    public void listen(ConsumerRecord<String, String> data) throws Exception {
        logger.info(data.value());
        latch.countDown();
    }
}
