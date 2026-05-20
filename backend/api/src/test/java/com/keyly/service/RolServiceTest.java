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
import com.keyly.mapper.RolMapper;
import com.keyly.model.Rol;
import com.keyly.model.Sucursal;
import com.keyly.model.request.RolRequest;
import com.keyly.model.response.RolResponse;
import com.keyly.repo.RolRepo;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    @Mock
    private RolRepo repo;

    @Mock
    private SucursalService sucursalService;

    @Mock
    private RolMapper mapper;

    @InjectMocks
    private RolService service;

    private UUID uuid;
    private Rol rol;
    private Sucursal sucursal;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        sucursal = new Sucursal();
        sucursal.setUuid(uuid);
        rol = new Rol();
        rol.setUuid(uuid);
        rol.setNom("Test Rol");
        rol.setSucursal(sucursal);
    }

    @Test
    void getAllRols_shouldReturnListOfRolResponse() {
        when(repo.findAll()).thenReturn(List.of(rol));

        List<RolResponse> result = service.getAllRols();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repo).findAll();
    }

    @Test
    void getByUuid_shouldReturnRolResponse_whenExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(rol));

        RolResponse result = service.getByUuid(uuid);

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
    void getRolEntityByUuid_shouldReturnRol_whenExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(rol));

        Rol result = service.getRolEntityByUuid(uuid);

        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        verify(repo).findByUuid(uuid);
    }

    @Test
    void getRolEntityByUuid_shouldThrowException_whenNotExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThrows(EntitatNoTrobadaException.class, () -> service.getRolEntityByUuid(uuid));
        verify(repo).findByUuid(uuid);
    }

    @Test
    void save_shouldReturnRolResponse() {
        RolRequest request = new RolRequest(uuid, "New Rol");
        when(sucursalService.getSucursalEntityByUuid(uuid)).thenReturn(sucursal);
        when(repo.save(any(Rol.class))).thenReturn(rol);

        RolResponse result = service.save(request);

        assertNotNull(result);
        verify(sucursalService).getSucursalEntityByUuid(uuid);
        verify(repo).save(any(Rol.class));
    }

    @Test
    void update_shouldReturnRolResponse() {
        RolRequest request = new RolRequest(uuid, "Updated Rol");
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(rol));
        when(sucursalService.getSucursalEntityByUuid(uuid)).thenReturn(sucursal);
        when(repo.save(rol)).thenReturn(rol);

        RolResponse result = service.update(uuid, request);

        assertNotNull(result);
        verify(repo).findByUuid(uuid);
        verify(repo).save(rol);
    }
}
