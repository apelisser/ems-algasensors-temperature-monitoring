package com.apelisser.algasensors.temperature.monitoring.infrastructure.rabbitmq;

import com.apelisser.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.apelisser.algasensors.temperature.monitoring.domain.service.SensorAlertService;
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
    private final SensorAlertService sensorAlertService;

    public RabbitMQListener(TemperatureMonitoringService temperatureMonitoringService,
        SensorAlertService sensorAlertService) {
        this.temperatureMonitoringService = temperatureMonitoringService;
        this.sensorAlertService = sensorAlertService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_PROCESS_TEMPERATURE, concurrency = "2-3")
    public void handleProcessTemperature(@Payload TemperatureLogData temperatureLogData, @Headers Map<String, Object> headers) {
        if (temperatureLogData.getValue() == 10.5f) {
            throw new RuntimeException("Error processing temperature. Temperature value is 10.5");
        }
        temperatureMonitoringService.processTemperatureReading(temperatureLogData);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ALERTING, concurrency = "2-3")
    public void handleAlerting(@Payload TemperatureLogData temperatureLogData, @Headers Map<String, Object> headers) {
        sensorAlertService.handleAlert(temperatureLogData);
    }

}
