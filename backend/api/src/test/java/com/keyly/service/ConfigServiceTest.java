package com.keyly.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.mapper.ConfigMapper;
import com.keyly.model.Config;
import com.keyly.model.Sucursal;
import com.keyly.model.response.ConfigResponse;
import com.keyly.repo.ConfigRepo;

@ExtendWith(MockitoExtension.class)
class ConfigServiceTest {

    @Mock
    private ConfigRepo repo;

    @Mock
    private ConfigMapper mapper;

    @Mock
    private SucursalService sucursalService;

    @InjectMocks
    private ConfigService service;

    private UUID uuid;
    private Config config;
    private Sucursal sucursal;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        sucursal = new Sucursal();
        sucursal.setUuid(uuid);
        sucursal.setId(1L);
        config = new Config();
        config.setUuid(uuid);
        config.setSucursal(sucursal);
        config.setPermetreTotsDominis(true);
    }

    @Test
    void getConfigs_shouldReturnListOfConfigResponse() {
        when(repo.findAll()).thenReturn(List.of(config));

        List<ConfigResponse> result = service.getConfigs();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repo).findAll();
    }

    @Test
    void getConfig_shouldReturnConfigResponse_whenExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(config));

        ConfigResponse result = service.getConfig(uuid);

        assertNotNull(result);
        verify(repo).findByUuid(uuid);
    }

    @Test
    void getConfig_shouldThrowException_whenNotExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThrows(EntitatNoTrobadaException.class, () -> service.getConfig(uuid));
        verify(repo).findByUuid(uuid);
    }

    @Test
    void getConfigBySucursalUuid_shouldReturnConfigResponse_whenExists() {
        when(sucursalService.getSucursalEntityByUuid(uuid)).thenReturn(sucursal);
        when(repo.findBySucursalId(1L)).thenReturn(Optional.of(config));

        ConfigResponse result = service.getConfigBySucursalUuid(uuid);

        assertNotNull(result);
        verify(sucursalService).getSucursalEntityByUuid(uuid);
        verify(repo).findBySucursalId(1L);
    }

    @Test
    void getConfigBySucursalUuid_shouldThrowException_whenNotExists() {
        when(sucursalService.getSucursalEntityByUuid(uuid)).thenReturn(sucursal);
        when(repo.findBySucursalId(1L)).thenReturn(Optional.empty());

        assertThrows(EntitatNoTrobadaException.class, () -> service.getConfigBySucursalUuid(uuid));
        verify(sucursalService).getSucursalEntityByUuid(uuid);
    }

    @Test
    void getConfigEntity_shouldReturnConfig_whenExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(config));

        Config result = service.getConfigEntity(uuid);

        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        verify(repo).findByUuid(uuid);
    }

    @Test
    void getConfigEntity_shouldThrowException_whenNotExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThrows(EntitatNoTrobadaException.class, () -> service.getConfigEntity(uuid));
        verify(repo).findByUuid(uuid);
    }
}
