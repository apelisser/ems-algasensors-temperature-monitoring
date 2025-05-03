package com.apelisser.algasensors.temperature.monitoring.api.controller;

import com.apelisser.algasensors.temperature.monitoring.api.model.SensorAlertInput;
import com.apelisser.algasensors.temperature.monitoring.api.model.SensorAlertOutput;
import com.apelisser.algasensors.temperature.monitoring.domain.model.SensorAlert;
import com.apelisser.algasensors.temperature.monitoring.domain.model.SensorId;
import com.apelisser.algasensors.temperature.monitoring.domain.repository.SensorAlertRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/sensors/{sensorId}/alert")
public class SensorAlertController {

    private final SensorAlertRepository sensorAlertRepository;

    public SensorAlertController(SensorAlertRepository sensorAlertRepository) {
        this.sensorAlertRepository = sensorAlertRepository;
    }

    @GetMapping
    public SensorAlertOutput getAlert(@PathVariable SensorId sensorId) {
        SensorAlert alert = findAlertOrFail(sensorId);

        return SensorAlertOutput.builder()
            .id(alert.getSensorId().getValue())
            .maxTemperature(alert.getMaxTemperature())
            .minTemperature(alert.getMinTemperature())
            .build();

    }

    @PutMapping
    public SensorAlertOutput createAlert(@PathVariable SensorId sensorId, @RequestBody SensorAlertInput input) {
        SensorAlert sensorAlert = sensorAlertRepository.findById(sensorId)
            .map(alert -> {
                alert.setMaxTemperature(input.getMaxTemperature());
                alert.setMinTemperature(input.getMinTemperature());
                return alert;
            })
            .orElseGet(() -> SensorAlert.builder()
                .sensorId(sensorId)
                .maxTemperature(input.getMaxTemperature())
                .minTemperature(input.getMinTemperature())
                .build());

        SensorAlert savedAlert = sensorAlertRepository.save(sensorAlert);

        return SensorAlertOutput.builder()
            .id(savedAlert.getSensorId().getValue())
            .maxTemperature(savedAlert.getMaxTemperature())
            .minTemperature(savedAlert.getMinTemperature())
            .build();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlert(@PathVariable SensorId sensorId) {
        SensorAlert alert = findAlertOrFail(sensorId);
        sensorAlertRepository.delete(alert);
    }

    private SensorAlert findAlertOrFail(SensorId sensorId) {
        return sensorAlertRepository.findById(sensorId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
