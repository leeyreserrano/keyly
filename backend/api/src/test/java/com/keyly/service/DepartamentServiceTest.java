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
import com.keyly.mapper.DepartamentMapper;
import com.keyly.model.Departament;
import com.keyly.model.Sucursal;
import com.keyly.model.request.DepartamentRequest;
import com.keyly.model.response.DepartamentResponse;
import com.keyly.repo.DepartamentRepo;

@ExtendWith(MockitoExtension.class)
class DepartamentServiceTest {

    @Mock
    private DepartamentRepo repo;

    @Mock
    private SucursalService sucursalService;

    @Mock
    private DepartamentMapper mapper;

    @InjectMocks
    private DepartamentService service;

    private UUID uuid;
    private Departament departament;
    private Sucursal sucursal;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        sucursal = new Sucursal();
        sucursal.setUuid(uuid);
        departament = new Departament();
        departament.setUuid(uuid);
        departament.setSucursal(sucursal);
    }

    @Test
    void getAllDepartaments_shouldReturnListOfDepartamentResponse() {
        when(repo.findAll()).thenReturn(List.of(departament));

        List<DepartamentResponse> result = service.getAllDepartaments();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repo).findAll();
    }

    @Test
    void getByUuid_shouldReturnDepartamentResponse_whenExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(departament));

        DepartamentResponse result = service.getByUuid(uuid);

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
    void getDepartamentEntityByUuid_shouldReturnDepartament_whenExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(departament));

        Departament result = service.getDepartamentEntityByUuid(uuid);

        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        verify(repo).findByUuid(uuid);
    }

    @Test
    void getDepartamentEntityByUuid_shouldThrowException_whenNotExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThrows(EntitatNoTrobadaException.class, () -> service.getDepartamentEntityByUuid(uuid));
        verify(repo).findByUuid(uuid);
    }

    @Test
    void save_shouldReturnDepartamentResponse() {
        DepartamentRequest request = new DepartamentRequest(uuid, "New Departament");
        when(sucursalService.getSucursalEntityByUuid(uuid)).thenReturn(sucursal);
        when(repo.save(any(Departament.class))).thenReturn(departament);

        DepartamentResponse result = service.save(request);

        assertNotNull(result);
        verify(sucursalService).getSucursalEntityByUuid(uuid);
        verify(repo).save(any(Departament.class));
    }

    @Test
    void update_shouldReturnDepartamentResponse() {
        DepartamentRequest request = new DepartamentRequest(uuid, "Updated Departament");
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(departament));
        when(sucursalService.getSucursalEntityByUuid(uuid)).thenReturn(sucursal);
        when(repo.save(departament)).thenReturn(departament);

        DepartamentResponse result = service.update(uuid, request);

        assertNotNull(result);
        verify(repo).findByUuid(uuid);
        verify(repo).save(departament);
    }
}
