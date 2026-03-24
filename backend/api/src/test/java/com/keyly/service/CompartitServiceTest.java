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
import com.keyly.mapper.CompartitMapper;
import com.keyly.model.Bagul;
import com.keyly.model.Carpeta;
import com.keyly.model.Compartit;
import com.keyly.model.Usuari;
import com.keyly.model.enums.Permisos;
import com.keyly.model.enums.TipusEntitat;
import com.keyly.model.request.CompartitRequest;
import com.keyly.model.response.CompartitResponse;
import com.keyly.model.response.CarpetaResponse;
import com.keyly.repo.CompartitRepo;

@ExtendWith(MockitoExtension.class)
class CompartitServiceTest {

    @Mock
    private CompartitRepo repo;

    @Mock
    private CarpetaService carpetaService;

    @Mock
    private ItemService itemService;

    @Mock
    private CompartitMapper mapper;

    @InjectMocks
    private CompartitService service;

    private UUID uuid;
    private Compartit compartit;
    private Usuari usuari;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        usuari = new Usuari();
        usuari.setUuid(uuid);
        compartit = new Compartit();
        compartit.setUuid(uuid);
        compartit.setUsuari(usuari);
        compartit.setTipusEntitat(TipusEntitat.CARPETA);
        compartit.setEntitatUuid(uuid);
    }

    @Test
    void getAllCompartits_shouldReturnListOfCompartitResponse() {
        CarpetaResponse carpetaResponse = mock(CarpetaResponse.class);
        when(carpetaResponse.uuid()).thenReturn(uuid);
        when(carpetaResponse.nom()).thenReturn("Test Carpeta");
        
        when(repo.findAll()).thenReturn(List.of(compartit));
        when(carpetaService.getByUuid(any())).thenReturn(carpetaResponse);

        List<CompartitResponse> result = service.getAllCompartits();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repo).findAll();
    }

    @Test
    void getAllCompartitsByUsuariUuid_shouldReturnListOfCompartitResponse() {
        CarpetaResponse carpetaResponse = mock(CarpetaResponse.class);
        when(carpetaResponse.uuid()).thenReturn(uuid);
        when(carpetaResponse.nom()).thenReturn("Test Carpeta");
        
        when(repo.findAllByUsuariUuid(uuid)).thenReturn(List.of(compartit));
        when(carpetaService.getByUuid(any())).thenReturn(carpetaResponse);
        
        List<CompartitResponse> result = service.getAllCompartitsByUsuariUuid(uuid);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repo).findAllByUsuariUuid(uuid);
    }

    @Test
    void getByUuid_shouldReturnCompartitResponse_whenExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(compartit));

        CompartitResponse result = service.getByUuid(uuid);

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
    void getUserCompartit_shouldReturnCompartitResponse_whenExists() {
        Bagul bagul = new Bagul();
        bagul.setUuid(uuid);
        bagul.setPropietari(usuari);
        Carpeta carpeta = new Carpeta();
        carpeta.setUuid(uuid);
        carpeta.setBagul(bagul);
        
        when(repo.findUserCompartitByUuid(usuari, uuid)).thenReturn(Optional.of(compartit));
        when(carpetaService.getCarpetaEntityByUuid(uuid)).thenReturn(carpeta);

        CompartitResponse result = service.getUserCompartit(usuari, uuid);

        assertNotNull(result);
        verify(repo).findUserCompartitByUuid(usuari, uuid);
    }

    @Test
    void getUserCompartit_shouldThrowException_whenNotExists() {
        when(repo.findUserCompartitByUuid(usuari, uuid)).thenReturn(Optional.empty());

        assertThrows(EntitatNoTrobadaException.class, () -> service.getUserCompartit(usuari, uuid));
        verify(repo).findUserCompartitByUuid(usuari, uuid);
    }

    @Test
    void save_shouldReturnCompartitResponse() {
        CompartitRequest request = new CompartitRequest(TipusEntitat.CARPETA, uuid, Permisos.LECTURA);
        Compartit savedCompartit = new Compartit();
        savedCompartit.setUuid(uuid); // Use the same uuid
        savedCompartit.setUsuari(usuari);
        savedCompartit.setTipusEntitat(TipusEntitat.CARPETA);
        savedCompartit.setEntitatUuid(uuid);
        
        when(repo.save(any(Compartit.class))).thenReturn(savedCompartit);
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(savedCompartit));
        
        Carpeta carpeta = new Carpeta();
        carpeta.setUuid(uuid);
        Bagul bagul = new Bagul();
        bagul.setUuid(uuid);
        carpeta.setBagul(bagul);
        when(carpetaService.getCarpetaEntityByUuid(uuid)).thenReturn(carpeta);

        CompartitResponse result = service.save(usuari, request);

        assertNotNull(result);
        verify(repo).save(any(Compartit.class));
    }

    @Test
    void update_shouldReturnCompartitResponse() {
        CompartitRequest request = new CompartitRequest(TipusEntitat.CARPETA, uuid, Permisos.ESCRIPTURA);
        Carpeta carpeta = new Carpeta();
        carpeta.setUuid(uuid);
        Bagul bagul = new Bagul();
        bagul.setUuid(uuid);
        bagul.setPropietari(usuari);
        carpeta.setBagul(bagul);
        
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(compartit));
        when(carpetaService.getCarpetaEntityByUuid(uuid)).thenReturn(carpeta);
        when(repo.save(compartit)).thenReturn(compartit);

        CompartitResponse result = service.update(uuid, request);

        assertNotNull(result);
        verify(repo).findByUuid(uuid);
        verify(mapper).updateCompartitFromDto(request, compartit);
        verify(repo).save(compartit);
    }

    @Test
    void deleteByUuid_shouldCallRepoDelete() {
        service.deleteByUuid(uuid);

        verify(repo).deleteByUuid(uuid);
    }

    @Test
    void deleteUserCompartit_shouldCallRepoDelete() {
        when(repo.findUserCompartitByUuid(usuari, uuid)).thenReturn(Optional.of(compartit));

        service.deleteUserCompartit(usuari, uuid);

        verify(repo).findUserCompartitByUuid(usuari, uuid);
        verify(repo).delete(compartit);
    }
}
