package com.apelisser.algasensors.temperature.monitoring.infrastructure.rabbitmq;

import com.apelisser.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.apelisser.algasensors.temperature.monitoring.domain.service.TemperatureMonitoringService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class RabbitMQListener {

    private final TemperatureMonitoringService temperatureMonitoringService;

    public RabbitMQListener(TemperatureMonitoringService temperatureMonitoringService) {
        this.temperatureMonitoringService = temperatureMonitoringService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME, concurrency = "2-3")
    public void handle(@Payload TemperatureLogData temperatureLogData, @Headers Map<String, Object> headers) {
        temperatureMonitoringService.processTemperatureReading(temperatureLogData);
    }

}
