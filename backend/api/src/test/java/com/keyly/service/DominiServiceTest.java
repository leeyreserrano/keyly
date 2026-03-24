package com.keyly.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
import com.keyly.mapper.DominiMapper;
import com.keyly.model.Domini;
import com.keyly.model.Sucursal;
import com.keyly.model.request.DominiRequest;
import com.keyly.model.response.DominiResponse;
import com.keyly.repo.DominiRepo;

@ExtendWith(MockitoExtension.class)
class DominiServiceTest {

    @Mock
    private DominiRepo repo;

    @Mock
    private SucursalService sucursalService;

    @Mock
    private DominiMapper mapper;

    @InjectMocks
    private DominiService service;

    private UUID uuid;
    private Domini domini;
    private Sucursal sucursal;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        sucursal = new Sucursal();
        sucursal.setUuid(uuid);
        domini = new Domini();
        domini.setUuid(uuid);
        domini.setDomini("test.com");
        domini.setSucursal(sucursal);
    }

    @Test
    void getAllDominis_shouldReturnListOfDominiResponse() {
        when(repo.findAll()).thenReturn(List.of(domini));

        List<DominiResponse> result = service.getAllDominis();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repo).findAll();
    }

    @Test
    void getByUuid_shouldReturnDominiResponse_whenExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(domini));

        DominiResponse result = service.getByUuid(uuid);

        assertNotNull(result);
        verify(repo).findByUuid(uuid);
    }

    @Test
    void getByUuid_shouldThrowException_whenNotExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThrows(EntitatNoTrobadaException.class, () -> service.getByUuid(uuid));
        verify(repo).findByUuid(uuid);
    }

    @Test
    void getDominiEntityByUuid_shouldReturnDomini_whenExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(domini));

        Domini result = service.getDominiEntityByUuid(uuid);

        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        verify(repo).findByUuid(uuid);
    }

    @Test
    void getDominiEntityByUuid_shouldThrowException_whenNotExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThrows(EntitatNoTrobadaException.class, () -> service.getDominiEntityByUuid(uuid));
        verify(repo).findByUuid(uuid);
    }

    @Test
    void save_shouldReturnDominiResponse() {
        DominiRequest request = new DominiRequest(uuid, "@newdomain.com");
        when(sucursalService.getSucursalEntityByUuid(uuid)).thenReturn(sucursal);
        when(repo.save(any(Domini.class))).thenReturn(domini);

        DominiResponse result = service.save(request);

        assertNotNull(result);
        verify(sucursalService).getSucursalEntityByUuid(uuid);
        verify(repo).save(any(Domini.class));
    }

    @Test
    void update_shouldReturnDominiResponse() {
        DominiRequest request = new DominiRequest(uuid, "@updated.com");
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(domini));
        when(sucursalService.getSucursalEntityByUuid(uuid)).thenReturn(sucursal);
        when(repo.save(domini)).thenReturn(domini);

        DominiResponse result = service.update(uuid, request);

        assertNotNull(result);
        verify(repo).findByUuid(uuid);
        verify(repo).save(domini);
    }
}
