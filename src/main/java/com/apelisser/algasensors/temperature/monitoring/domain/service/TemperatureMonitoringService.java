package com.apelisser.algasensors.temperature.monitoring.domain.service;

import com.apelisser.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.apelisser.algasensors.temperature.monitoring.domain.model.SensorId;
import com.apelisser.algasensors.temperature.monitoring.domain.model.SensorMonitoring;
import com.apelisser.algasensors.temperature.monitoring.domain.model.TemperatureLog;
import com.apelisser.algasensors.temperature.monitoring.domain.model.TemperatureLogId;
import com.apelisser.algasensors.temperature.monitoring.domain.repository.SensorMonitoringRepository;
import com.apelisser.algasensors.temperature.monitoring.domain.repository.TemperatureLogRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class TemperatureMonitoringService {

    private final SensorMonitoringRepository sensorMonitoringRepository;
    private final TemperatureLogRepository temperatureLogRepository;

    public TemperatureMonitoringService(SensorMonitoringRepository sensorMonitoringRepository,
        TemperatureLogRepository temperatureLogRepository) {
        this.sensorMonitoringRepository = sensorMonitoringRepository;
        this.temperatureLogRepository = temperatureLogRepository;
    }

    @Transactional
    public void processTemperatureReading(TemperatureLogData temperatureData) {
        SensorId sensorId = new SensorId(temperatureData.getSensorId());
        Optional<SensorMonitoring> sensor = sensorMonitoringRepository.findById(sensorId);

        if (sensor.isEmpty()) {
            logIgnoredTemperature(temperatureData);
            return;
        }

        handleSensorMonitoring(temperatureData, sensor.get());
    }

    private void handleSensorMonitoring(TemperatureLogData temperatureData, SensorMonitoring sensor) {
        if (sensor.isEnabled()) {
            sensor.setLastTemperature(temperatureData.getValue());
            sensor.setUpdatedAt(temperatureData.getRegisteredAt());
            sensorMonitoringRepository.save(sensor);

            TemperatureLog temperatureLog = TemperatureLog.builder()
                .id(new TemperatureLogId(temperatureData.getId()))
                .registeredAt(temperatureData.getRegisteredAt())
                .sensorId(new SensorId(temperatureData.getSensorId()))
                .value(temperatureData.getValue())
                .build();

            temperatureLogRepository.save(temperatureLog);

            log.info("Temperature updated: SensorId={} temperature={}",
                temperatureData.getSensorId(),
                temperatureData.getValue());
        } else {
            logIgnoredTemperature(temperatureData);
        }
    }

    private void logIgnoredTemperature(TemperatureLogData temperatureData) {
        log.info("Temperature ignored: SensorId={} temperature={}",
            temperatureData.getSensorId(),
            temperatureData.getValue());
    }

}
