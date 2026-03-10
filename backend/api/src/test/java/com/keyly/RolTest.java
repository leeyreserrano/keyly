// package com.keyly;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.when;

// import java.util.ArrayList;
// import java.util.List;

// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;

// import com.keyly.controller.RolController;
// import com.keyly.model.Rol;
// import com.keyly.model.Sucursal;
// import com.keyly.service.RolService;
// import com.keyly.service.SucursalService;

// @ExtendWith(MockitoExtension.class)
// public class RolTest {

//     /*
//         POSTMAN TEST

//         {
//             "sucursal": {
//                 "id": "2"
//             },
//             "nom": "pelele"
//         }
            
//     */

//     @Mock
//     private RolService service;

//     @Mock
//     private SucursalService sucursalService;

//     @InjectMocks
//     private RolController controller;

//     private List<Sucursal> sucursals = List.of(
//             new Sucursal(1L, "Sucursal1", "Dir1", "BCN", "España", "123", "a@a.com"),
//             new Sucursal(2L, "Sucursal2", "Dir2", "Madrid", "España", "456", "b@b.com"));

//     private List<Rol> rols = new ArrayList<>(List.of(
//             new Rol(1L, sucursals.get(0), "Rol1"),
//             new Rol(2L, sucursals.get(1), "Rol2")));

//     @Test
//     void getAllRols() {
//         when(service.getAllRols()).thenReturn(rols);

//         ResponseEntity<List<Rol>> response = controller.getAllRols();

//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertEquals(2, response.getBody().size());
//         assertEquals("Rol1", response.getBody().get(0).getNom());
//         assertEquals("Madrid", response.getBody().get(1).getSucursal().getCiutat());
//     }

//     @Test
//     void getRol() {
//         when(service.getById(1L)).thenReturn(rols.get(0));

//         ResponseEntity<Rol> response = controller.getRol(1L);

//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertEquals("Rol1", response.getBody().getNom());
//         assertEquals("Sucursal1", response.getBody().getSucursal().getNom());
//     }

//     @Test
//     void addRol() {
//         when(service.save(any(Rol.class))).thenAnswer(invocation -> {
//             Rol r = invocation.getArgument(0);
//             r.setId((long) (rols.size() + 1));
//             rols.add(r);
//             return r;
//         });

//         Rol rol = new Rol();
//         rol.setSucursal(sucursals.get(1));
//         rol.setNom("Rol3");

//         ResponseEntity<Rol> response = controller.addRol(rol);

//         assertEquals(HttpStatus.CREATED, response.getStatusCode());
//         assertEquals("Rol3", response.getBody().getNom());
//         assertEquals(3, rols.size());
//     }

//     @Test
//     void updateRol() {
//         when(service.getById(1L)).thenReturn(rols.get(0));
//         when(sucursalService.getById(1L)).thenReturn(sucursals.get(0));
//         when(service.save(any(Rol.class))).thenAnswer(invocation -> invocation.getArgument(0));

//         Rol r = service.getById(1L);

//         r.setNom("NouRol");

//         ResponseEntity<Rol> response = controller.updateRol(1L, r);

//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertEquals("NouRol", response.getBody().getNom());
//         assertEquals("NouRol", rols.get(0).getNom());

//         response = controller.updateRol(4L, r);

//         assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//     }

//     @Test
//     void deleteRol() {
//         when(service.getById(1L)).thenReturn(rols.get(0));

//         ResponseEntity<Rol> response = controller.deleteRol(1L);

//         assertEquals(HttpStatus.OK, response.getStatusCode());

//         response = controller.deleteRol(3L);

//         assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//     }

// }
