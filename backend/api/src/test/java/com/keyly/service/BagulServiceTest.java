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
import com.keyly.mapper.BagulMapper;
import com.keyly.model.Bagul;
import com.keyly.model.Usuari;
import com.keyly.model.request.BagulRequest;
import com.keyly.model.response.BagulResponse;
import com.keyly.repo.BagulRepo;

@ExtendWith(MockitoExtension.class)
class BagulServiceTest {

    @Mock
    private BagulRepo repo;

    @Mock
    private UsuariService usuariService;

    @Mock
    private BagulMapper mapper;

    @InjectMocks
    private BagulService service;

    private UUID uuid;
    private Bagul bagul;
    private Usuari usuari;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        usuari = new Usuari();
        usuari.setUuid(uuid);
        bagul = new Bagul(usuari);
        bagul.setUuid(uuid);
    }

    @Test
    void getAllBaguls_shouldReturnListOfBagulResponse() {
        when(repo.findAll()).thenReturn(List.of(bagul));

        List<BagulResponse> result = service.getAllBaguls();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repo).findAll();
    }

    @Test
    void getByUuid_shouldReturnBagulResponse_whenExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(bagul));

        BagulResponse result = service.getByUuid(uuid);

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
    void getBagulEntityByUuid_shouldReturnBagul_whenExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(bagul));

        Bagul result = service.getBagulEntityByUuid(uuid);

        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        verify(repo).findByUuid(uuid);
    }

    @Test
    void getBagulEntityByUuid_shouldThrowException_whenNotExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThrows(EntitatNoTrobadaException.class, () -> service.getBagulEntityByUuid(uuid));
        verify(repo).findByUuid(uuid);
    }

    @Test
    void getBagulEntityByUsuariUuid_shouldReturnBagul_whenExists() {
        when(repo.findByPropietariUuid(uuid)).thenReturn(Optional.of(bagul));

        Bagul result = service.getBagulEntityByUsuariUuid(uuid);

        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        verify(repo).findByPropietariUuid(uuid);
    }

    @Test
    void getBagulEntityByUsuariUuid_shouldThrowException_whenNotExists() {
        when(repo.findByPropietariUuid(uuid)).thenReturn(Optional.empty());

        assertThrows(EntitatNoTrobadaException.class, () -> service.getBagulEntityByUsuariUuid(uuid));
        verify(repo).findByPropietariUuid(uuid);
    }

    @Test
    void update_shouldReturnBagulResponse() {
        BagulRequest request = new BagulRequest(uuid);
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(bagul));
        when(usuariService.getUsuariEntityByUuid(uuid)).thenReturn(usuari);
        when(repo.save(bagul)).thenReturn(bagul);

        BagulResponse result = service.update(uuid, request);

        assertNotNull(result);
        verify(repo).findByUuid(uuid);
        verify(usuariService).getUsuariEntityByUuid(uuid);
        verify(mapper).updateBagulFromDto(request, bagul);
        verify(repo).save(bagul);
    }
}
