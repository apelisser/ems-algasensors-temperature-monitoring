package com.apelisser.algasensors.temperature.monitoring.infrastructure.rabbitmq;

import com.apelisser.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import io.hypersistence.tsid.TSID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
public class RabbitMQListener {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handle(@Payload TemperatureLogData temperatureLogData, @Headers Map<String, Object> headers) throws InterruptedException {
        TSID sensorId = temperatureLogData.getSensorId();
        Double temperature = temperatureLogData.getValue();
        log.info("Temperature updated: sensorId={}, temperature={}", sensorId, temperature);
        log.info("Headers: {}", headers);

        Thread.sleep(Duration.ofSeconds(5));
    }

}
