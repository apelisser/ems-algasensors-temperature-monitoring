package com.apelisser.algasensors.temperature.monitoring.domain.service;

import com.apelisser.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.apelisser.algasensors.temperature.monitoring.domain.model.SensorId;
import com.apelisser.algasensors.temperature.monitoring.domain.repository.SensorAlertRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SensorAlertService {

    private final SensorAlertRepository sensorAlertRepository;

    public SensorAlertService(SensorAlertRepository sensorAlertRepository) {
        this.sensorAlertRepository = sensorAlertRepository;
    }

    @Transactional
    public void handleAlert(TemperatureLogData temperatureLogData) {
        sensorAlertRepository.findById(new SensorId(temperatureLogData.getSensorId()))
            .ifPresentOrElse(alert -> {
                if (alert.getMaxTemperature() != null
                        && temperatureLogData.getValue().compareTo(alert.getMaxTemperature()) >= 0) {

                    log.info("Alert Max temperature: SensorId={} temperature={}",
                        temperatureLogData.getSensorId(),
                        temperatureLogData.getValue());

                } else if (alert.getMinTemperature() != null
                    && temperatureLogData.getValue().compareTo(alert.getMinTemperature()) <= 0) {

                    log.info("Alert Min temperature: SensorId={} temperature={}",
                        temperatureLogData.getSensorId(),
                        temperatureLogData.getValue());

                } else {
                    logIgnoredAlert(temperatureLogData);
                }
            }, () -> logIgnoredAlert(temperatureLogData));
    }

    private void logIgnoredAlert(TemperatureLogData temperatureLogData) {
        log.info("Alert ignored: SensorId={} temperature={}",
            temperatureLogData.getSensorId(),
            temperatureLogData.getValue());
    }

}
