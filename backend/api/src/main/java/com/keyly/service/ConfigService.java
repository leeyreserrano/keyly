package com.keyly.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.model.request.ConfigRequest;
import com.keyly.model.response.ConfigResponse;
import com.keyly.repo.ConfigRepo;

import com.keyly.model.Config;
import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.mapper.ConfigMapper;

@Service
public class ConfigService {

    @Autowired
    private ConfigRepo repo;

    @Autowired
    private ConfigMapper mapper;

    @Autowired
    private SucursalService sucursalService;

    public List<ConfigResponse> getConfigs() {
        return repo.findAll()
                .stream()
                .map(config -> new ConfigResponse(config))
                .toList();
    }

    public ConfigResponse getConfig(UUID uuid) {
        return new ConfigResponse(repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Config no trobada amb el uuid: " + uuid)));
    }

    public ConfigResponse getConfigBySucursalUuid(UUID uuid) {
        Long sucursalId = sucursalService.getSucursalEntityByUuid(uuid).getId();

        return new ConfigResponse(repo.findBySucursalId(sucursalId)
                .orElseThrow(() -> new EntitatNoTrobadaException("Config no trobada amb el uuid: " + uuid)));
    }

    public Config getConfigEntity(UUID uuid) {
        return repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Config no trobada amb el uuid: " + uuid));
    }

    public ConfigResponse updateConfig(UUID uuid, ConfigRequest c) {
        Config config = getConfigEntity(uuid);

        mapper.updateConfigFromDto(c, config);

        return new ConfigResponse(repo.save(config));
    }

    public ConfigResponse updateConfigBySucursalUuid(UUID uuid, ConfigRequest c) {
        Long sucursalId = sucursalService.getSucursalEntityByUuid(uuid).getId();

        Config config = repo.findBySucursalId(sucursalId)
                .orElseThrow(() -> new EntitatNoTrobadaException("Config no trobada amb el uuid: " + uuid));

        mapper.updateConfigFromDto(c, config);

        return new ConfigResponse(repo.save(config));
    }

}
