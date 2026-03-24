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
import com.keyly.mapper.ItemMapper;
import com.keyly.model.Bagul;
import com.keyly.model.Item;
import com.keyly.model.Usuari;
import com.keyly.model.request.ItemRequest;
import com.keyly.model.response.ItemResponse;
import com.keyly.repo.ItemRepo;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepo repo;

    @Mock
    private BagulService bagulService;

    @Mock
    private UsuariService usuariService;

    @Mock
    private ItemMapper mapper;

    @Mock
    private CarpetaService carpetaService;

    @InjectMocks
    private ItemService service;

    private UUID uuid;
    private Item item;
    private Usuari usuari;
    private Bagul bagul;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        usuari = new Usuari();
        usuari.setUuid(uuid);
        bagul = new Bagul(usuari);
        bagul.setUuid(uuid);
        item = new Item(bagul, new ItemRequest("Title", "User", "Password", "URL", "Notes", false));
        item.setUuid(uuid);
    }

    @Test
    void getAllItems_shouldReturnListOfItemResponse() {
        when(repo.findAll()).thenReturn(List.of(item));
        when(carpetaService.hasItemInAnyCarpeta(uuid)).thenReturn(false);

        List<ItemResponse> result = service.getAllItems();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repo).findAll();
        verify(carpetaService).hasItemInAnyCarpeta(uuid);
    }

    @Test
    void getAllItemsByUsuariUuid_shouldReturnListOfItemResponse() {
        when(repo.findAllByUsuariUuid(uuid)).thenReturn(List.of(item));
        when(carpetaService.hasItemInAnyCarpeta(uuid)).thenReturn(false);

        List<ItemResponse> result = service.getAllItemsByUsuariUuid(uuid);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repo).findAllByUsuariUuid(uuid);
    }

    @Test
    void getByUuid_shouldReturnItemResponse_whenExists() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(item));
        when(carpetaService.hasItemInAnyCarpeta(uuid)).thenReturn(false);

        ItemResponse result = service.getByUuid(uuid);

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
    void getUserItem_shouldReturnItemResponse_whenExists() {
        when(repo.findUserItemByUuid(usuari, uuid)).thenReturn(Optional.of(item));
        when(carpetaService.hasItemInAnyCarpeta(uuid)).thenReturn(false);

        ItemResponse result = service.getUserItem(usuari, uuid);

        assertNotNull(result);
        verify(repo).findUserItemByUuid(usuari, uuid);
    }

    @Test
    void getUserItem_shouldThrowException_whenNotExists() {
        when(repo.findUserItemByUuid(usuari, uuid)).thenReturn(Optional.empty());

        assertThrows(EntitatNoTrobadaException.class, () -> service.getUserItem(usuari, uuid));
        verify(repo).findUserItemByUuid(usuari, uuid);
    }

    @Test
    void update_shouldReturnItemResponse() {
        ItemRequest request = new ItemRequest("Title", "User", "Password", "URL", "Notes", false);
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(item));
        when(repo.save(item)).thenReturn(item);
        when(carpetaService.hasItemInAnyCarpeta(uuid)).thenReturn(false);

        ItemResponse result = service.update(uuid, request);

        assertNotNull(result);
        verify(repo).findByUuid(uuid);
        verify(mapper).updateItemFromDto(request, item);
        verify(repo).save(item);
    }

    @Test
    void deleteByUuid_shouldReturnItemResponse() {
        when(repo.findByUuid(uuid)).thenReturn(Optional.of(item));
        when(carpetaService.hasItemInAnyCarpeta(uuid)).thenReturn(false);

        ItemResponse result = service.deleteByUuid(uuid);

        assertNotNull(result);
        verify(repo).findByUuid(uuid);
        verify(repo).deleteByUuid(uuid);
    }

    @Test
    void deleteByUuid_withUsuari_shouldReturnItemResponse() {
        when(repo.findUserItemByUuid(usuari, uuid)).thenReturn(Optional.of(item));

        ItemResponse result = service.deleteByUuid(usuari, uuid);

        assertNotNull(result);
        verify(repo).findUserItemByUuid(usuari, uuid);
        verify(repo).deleteByUuid(uuid);
    }
}
