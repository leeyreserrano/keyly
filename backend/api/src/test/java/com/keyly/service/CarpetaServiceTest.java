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
import com.keyly.mapper.CarpetaMapper;
import com.keyly.model.Bagul;
import com.keyly.model.Carpeta;
import com.keyly.model.Usuari;
import com.keyly.model.request.CarpetaRequest;
import com.keyly.model.response.CarpetaResponse;
import com.keyly.repo.CarpetaRepo;

@ExtendWith(MockitoExtension.class)
class CarpetaServiceTest {

    @Mock
    private CarpetaRepo repo;

    @Mock
    private BagulService bagulService;

    @Mock
    private ItemService itemService;

    @Mock
    private CarpetaMapper mapper;

    @InjectMocks
    private CarpetaService service;

    private UUID uuid;
    private Carpeta carpeta;
    private CarpetaResponse carpetaResponse;
    private Usuari usuari;
    private Bagul bagul;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        usuari = new Usuari();
        usuari.setUuid(uuid);
        bagul = new Bagul(usuari);
        bagul.setUuid(uuid);
        carpeta = new Carpeta(bagul, new CarpetaRequest(uuid, "Test Carpeta", false));
        carpeta.setUuid(uuid);
        carpetaResponse = new CarpetaResponse(carpeta);
    }

    @Test
    void getAllCarpetes_shouldReturnListOfCarpetaResponse() {
        // Given
        when(repo.findAll()).thenReturn(List.of(carpeta));

        // When
        List<CarpetaResponse> result = service.getAllCarpetes();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(carpetaResponse.nom(), result.get(0).nom());
        verify(repo).findAll();
    }

    @Test
    void getAllCarpetesByUsuariUuid_shouldReturnListOfCarpetaResponse() {
        // Given
        when(repo.findByBagulPropietariUuid(uuid)).thenReturn(List.of(carpeta));

        // When
        List<CarpetaResponse> result = service.getAllCarpetesByUsuariUuid(uuid);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repo).findByBagulPropietariUuid(uuid);
    }

    @Test
    void getByUuid_shouldReturnCarpetaResponse_whenExists() {
        // Given
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(carpeta));

        // When
        CarpetaResponse result = service.getByUuid(uuid);

        // Then
        assertNotNull(result);
        assertEquals(carpetaResponse.nom(), result.nom());
        verify(repo).findByUuid(uuid);
    }

    @Test
    void getByUuid_shouldThrowException_whenNotExists() {
        // Given
        when(repo.findByUuid(uuid)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntitatNoTrobadaException.class, () -> service.getByUuid(uuid));
        verify(repo).findByUuid(uuid);
    }

    @Test
    void getUserCarpeta_shouldReturnCarpetaResponse_whenExists() {
        // Given
        when(repo.findByBagulPropietariAndUuid(usuari, uuid)).thenReturn(Optional.of(carpeta));

        // When
        CarpetaResponse result = service.getUserCarpeta(usuari, uuid);

        // Then
        assertNotNull(result);
        assertEquals(carpetaResponse.nom(), result.nom());
        verify(repo).findByBagulPropietariAndUuid(usuari, uuid);
    }

    @Test
    void getUserCarpeta_shouldThrowException_whenNotExists() {
        // Given
        when(repo.findByBagulPropietariAndUuid(usuari, uuid)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntitatNoTrobadaException.class, () -> service.getUserCarpeta(usuari, uuid));
        verify(repo).findByBagulPropietariAndUuid(usuari, uuid);
    }

    @Test
    void save_shouldReturnCarpetaResponse() {
        // Given
        CarpetaRequest request = new CarpetaRequest(uuid, "New Carpeta", false);
        when(bagulService.getBagulEntityByUuid(uuid)).thenReturn(bagul);
        when(repo.save(any(Carpeta.class))).thenReturn(carpeta);

        // When
        CarpetaResponse result = service.save(request);

        // Then
        assertNotNull(result);
        verify(bagulService).getBagulEntityByUuid(uuid);
        verify(repo).save(any(Carpeta.class));
    }

    @Test
    void save_withUsuari_shouldReturnCarpetaResponse_whenAuthorized() {
        // Given
        CarpetaRequest request = new CarpetaRequest(uuid, "New Carpeta", false);
        when(bagulService.getBagulEntityByUuid(uuid)).thenReturn(bagul);
        when(repo.save(any(Carpeta.class))).thenReturn(carpeta);

        // When
        CarpetaResponse result = service.save(usuari, request);

        // Then
        assertNotNull(result);
        verify(bagulService).getBagulEntityByUuid(uuid);
        verify(repo).save(any(Carpeta.class));
    }

    @Test
    void save_withUsuari_shouldThrowException_whenNotAuthorized() {
        // Given
        Usuari otherUsuari = new Usuari();
        otherUsuari.setUuid(UUID.randomUUID());
        CarpetaRequest request = new CarpetaRequest(uuid, "New Carpeta", false);
        when(bagulService.getBagulEntityByUuid(uuid)).thenReturn(bagul);

        // When & Then
        assertThrows(EntitatNoTrobadaException.class, () -> service.save(otherUsuari, request));
        verify(bagulService).getBagulEntityByUuid(uuid);
    }

    @Test
    void update_shouldReturnCarpetaResponse() {
        // Given
        CarpetaRequest request = new CarpetaRequest(uuid, "Updated Carpeta", false);
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(carpeta));
        when(bagulService.getBagulEntityByUuid(uuid)).thenReturn(bagul);
        when(repo.save(carpeta)).thenReturn(carpeta);

        // When
        CarpetaResponse result = service.update(uuid, request);

        // Then
        assertNotNull(result);
        verify(repo).findByUuid(uuid);
        verify(bagulService).getBagulEntityByUuid(uuid);
        verify(mapper).updateCarpetaFromDto(request, carpeta);
        verify(repo).save(carpeta);
    }

    @Test
    void update_withUsuari_shouldReturnCarpetaResponse() {
        // Given
        CarpetaRequest request = new CarpetaRequest(uuid, "Updated Carpeta", false);
        when(repo.findByBagulPropietariAndUuid(usuari, uuid)).thenReturn(Optional.of(carpeta));
        when(bagulService.getBagulEntityByUuid(uuid)).thenReturn(bagul);
        when(repo.save(carpeta)).thenReturn(carpeta);

        // When
        CarpetaResponse result = service.update(usuari, uuid, request);

        // Then
        assertNotNull(result);
        verify(repo).findByBagulPropietariAndUuid(usuari, uuid);
        verify(bagulService).getBagulEntityByUuid(uuid);
        verify(mapper).updateCarpetaFromDto(request, carpeta);
        verify(repo).save(carpeta);
    }

    @Test
    void deleteByUuid_shouldCallRepoDelete() {
        // Given
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(carpeta));

        // When
        CarpetaResponse result = service.deleteByUuid(uuid);

        // Then
        assertNotNull(result);
        verify(repo).findByUuid(uuid);
        verify(repo).deleteByUuid(uuid);
    }

    @Test
    void deleteUserCarpeta_shouldCallRepoDelete() {
        // Given
        when(repo.findByBagulPropietariAndUuid(usuari, uuid)).thenReturn(Optional.of(carpeta));

        // When
        service.deleteUserCarpeta(usuari, uuid);

        // Then
        verify(repo).findByBagulPropietariAndUuid(usuari, uuid);
        verify(repo).delete(carpeta);
    }
}