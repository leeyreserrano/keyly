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
import com.keyly.mapper.SucursalMapper;
import com.keyly.model.Sucursal;
import com.keyly.model.request.SucursalRequest;
import com.keyly.model.response.SucursalResponse;
import com.keyly.repo.SucursalRepo;

@ExtendWith(MockitoExtension.class)
class SucursalServiceTest {

    @Mock
    private SucursalRepo repo;

    @Mock
    private SucursalMapper mapper;

    @InjectMocks
    private SucursalService service;

    private UUID uuid;
    private Sucursal sucursal;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        sucursal = new Sucursal();
        sucursal.setUuid(uuid);
        sucursal.setNom("Test Sucursal");
    }

    @Test
    void getAllSucursals_shouldReturnListOfSucursalResponse() {
        when(repo.findAll()).thenReturn(List.of(sucursal));

        List<SucursalResponse> result = service.getAllSucursals();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repo).findAll();
    }

    @Test
    void getByUuid_shouldReturnSucursalResponse_whenExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(sucursal));

        SucursalResponse result = service.getByUuid(uuid);

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
    void getSucursalEntityByUuid_shouldReturnSucursal_whenExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(sucursal));

        Sucursal result = service.getSucursalEntityByUuid(uuid);

        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        verify(repo).findByUuid(uuid);
    }

    @Test
    void getSucursalEntityByUuid_shouldThrowException_whenNotExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThrows(EntitatNoTrobadaException.class, () -> service.getSucursalEntityByUuid(uuid));
        verify(repo).findByUuid(uuid);
    }

    @Test
    void save_shouldReturnSucursalResponse() {
        SucursalRequest request = new SucursalRequest("New", "Sucursal", "Address", "City", "PostCode", "Country");
        when(repo.save(any(Sucursal.class))).thenReturn(sucursal);
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(sucursal));

        SucursalResponse result = service.save(request);

        assertNotNull(result);
        verify(repo).save(any(Sucursal.class));
    }

    @Test
    void update_shouldReturnSucursalResponse() {
        SucursalRequest request = new SucursalRequest("Updated", "Sucursal", "Address", "City", "PostCode", "Country");
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(sucursal));
        when(repo.save(sucursal)).thenReturn(sucursal);

        SucursalResponse result = service.update(uuid, request);

        assertNotNull(result);
        verify(repo).findByUuid(uuid);
        verify(mapper).updateSucursalFromDto(request, sucursal);
        verify(repo).save(sucursal);
    }
}
