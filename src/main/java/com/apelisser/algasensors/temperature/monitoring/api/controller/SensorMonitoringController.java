package com.apelisser.algasensors.temperature.monitoring.api.controller;

import com.apelisser.algasensors.temperature.monitoring.api.model.SensorMonitoringOutput;
import com.apelisser.algasensors.temperature.monitoring.domain.model.SensorId;
import com.apelisser.algasensors.temperature.monitoring.domain.model.SensorMonitoring;
import com.apelisser.algasensors.temperature.monitoring.domain.repository.SensorMonitoringRepository;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sensors/{sensorId}/monitoring")
@RequiredArgsConstructor
public class SensorMonitoringController {

    private final SensorMonitoringRepository sensorMonitoringRepository;

    @GetMapping
    public SensorMonitoringOutput getDetail(@PathVariable TSID sensorId) {
        SensorMonitoring sensorMonitoring = findByIdOrDefault(sensorId);

        return SensorMonitoringOutput.builder()
            .id(sensorMonitoring.getId().getValue())
            .enabled(sensorMonitoring.getEnabled())
            .lastTemperature(sensorMonitoring.getLastTemperature())
            .updatedAt(sensorMonitoring.getUpdatedAt())
            .build();
    }

    @PutMapping("/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enable(@PathVariable TSID sensorId) {
        SensorMonitoring sensorMonitoring = findByIdOrDefault(sensorId);
        sensorMonitoring.enable();
        sensorMonitoringRepository.saveAndFlush(sensorMonitoring);
    }

    @DeleteMapping("/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disable(@PathVariable TSID sensorId) {
        SensorMonitoring sensorMonitoring = findByIdOrDefault(sensorId);
        sensorMonitoring.disable();
        sensorMonitoringRepository.saveAndFlush(sensorMonitoring);
    }

    private SensorMonitoring findByIdOrDefault(TSID sensorId) {
        return sensorMonitoringRepository.findById(new SensorId(sensorId))
            .orElseGet(() -> SensorMonitoring.builder()
                .id(new SensorId(sensorId))
                .enabled(false)
                .lastTemperature(null)
                .updatedAt(null)
                .build());
    }

}