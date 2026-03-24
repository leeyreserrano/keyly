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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.mapper.UsuariMapper;
import com.keyly.model.Departament;
import com.keyly.model.Rol;
import com.keyly.model.Sucursal;
import com.keyly.model.Usuari;
import com.keyly.model.response.UsuariResponse;
import com.keyly.repo.UsuariRepo;

@ExtendWith(MockitoExtension.class)
class UsuariServiceTest {

    @Mock
    private UsuariRepo repo;

    @Mock
    private SucursalService sucursalService;

    @Mock
    private DepartamentService departamentService;

    @Mock
    private ConfigService configService;

    @Mock
    private DominiService dominiService;

    @Mock
    private RolService rolService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuariMapper mapper;

    @InjectMocks
    private UsuariService service;

    private UUID uuid;
    private Usuari usuari;
    private Sucursal sucursal;
    private Departament departament;
    private Rol rol;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        usuari = new Usuari();
        usuari.setUuid(uuid);
        usuari.setPotAdministrar(false);
        sucursal = new Sucursal();
        sucursal.setUuid(uuid);
        departament = new Departament();
        departament.setUuid(uuid);
        rol = new Rol();
        rol.setUuid(uuid);
        usuari.setSucursal(sucursal);
        usuari.setDepartament(departament);
        usuari.setRol(rol);
    }

    @Test
    void getAllUsuaris_shouldReturnListOfUsuariResponse() {
        when(repo.findAll()).thenReturn(List.of(usuari));

        List<UsuariResponse> result = service.getAllUsuaris();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repo).findAll();
    }

    @Test
    void getByUuid_shouldReturnUsuariResponse_whenExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(usuari));

        UsuariResponse result = service.getByUuid(uuid);

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
    void getUsuariEntityByUuid_shouldReturnUsuari_whenExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(usuari));

        Usuari result = service.getUsuariEntityByUuid(uuid);

        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        verify(repo).findByUuid(uuid);
    }

    @Test
    void getUsuariEntityByUuid_shouldThrowException_whenNotExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThrows(EntitatNoTrobadaException.class, () -> service.getUsuariEntityByUuid(uuid));
        verify(repo).findByUuid(uuid);
    }

    @Test
    void getUsuariEntityByMail_shouldReturnUsuari_whenExists() {
        String email = "test@test.com";
        when(repo.findByCorreu(email)).thenReturn(Optional.of(usuari));

        Usuari result = service.getUsuariEntityByMail(email);

        assertNotNull(result);
        verify(repo).findByCorreu(email);
    }

    @Test
    void getUsuariEntityByMail_shouldThrowException_whenNotExists() {
        String email = "test@test.com";
        when(repo.findByCorreu(email)).thenReturn(Optional.empty());

        assertThrows(EntitatNoTrobadaException.class, () -> service.getUsuariEntityByMail(email));
        verify(repo).findByCorreu(email);
    }
}
