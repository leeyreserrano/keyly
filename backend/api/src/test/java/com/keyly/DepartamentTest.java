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

// import com.keyly.controller.DepartamentController;
// import com.keyly.model.Departament;
// import com.keyly.model.Sucursal;
// import com.keyly.service.DepartamentService;
// import com.keyly.service.SucursalService;

// @ExtendWith(MockitoExtension.class)
// public class DepartamentTest {

//     /*
//       POSTMAN TEST
      
//       {
//       "sucursal": {
//       "id": "2"
//       },
//       "nom": "Metrologia"
//       }
      
//      */

//     @Mock
//     private DepartamentService service;

//     @Mock
//     private SucursalService sucursalService;

//     @InjectMocks
//     private DepartamentController controller;

//     private List<Sucursal> sucursals = List.of(
//             new Sucursal(1L, "Sucursal1", "Dir1", "BCN", "España", "123", "a@a.com"),
//             new Sucursal(2L, "Sucursal2", "Dir2", "Madrid", "España", "456", "b@b.com"));

//     private List<Departament> departaments = new ArrayList<>(List.of(
//             new Departament(1L, sucursals.get(0), "Dep1"),
//             new Departament(2L, sucursals.get(1), "Dep2")));

//     @Test
//     void getAllDepartaments() {
//         when(service.getAllDepartaments()).thenReturn(departaments);

//         ResponseEntity<List<Departament>> response = controller.getAllDepartaments();

//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertEquals(2, response.getBody().size());
//         assertEquals("Dep1", response.getBody().get(0).getNom());
//         assertEquals("Dir1", response.getBody().get(0).getSucursal().getDireccio());
//     }

//     @Test
//     void getDepartament() {
//         when(service.getById(1L)).thenReturn(departaments.get(0));
//         when(service.getById(2L)).thenReturn(departaments.get(1));

//         ResponseEntity<Departament> response = controller.getDepartament(1L);

//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertEquals("Dep1", response.getBody().getNom());
//         assertEquals("Sucursal1", response.getBody().getSucursal().getNom());

//         response = controller.getDepartament(2L);

//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertEquals("Dep2", response.getBody().getNom());
//         assertEquals("Sucursal2", response.getBody().getSucursal().getNom());

//         response = controller.getDepartament(7L);

//         assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//     }

//     @Test
//     void addDepartament() {

//         when(service.save(any(Departament.class)))
//                 .thenAnswer(invocation -> {
//                     Departament d = invocation.getArgument(0);
//                     d.setId((long) (departaments.size() + 1));
//                     departaments.add(d);
//                     return d;
//                 });

//         Departament departament = new Departament();
//         departament.setSucursal(sucursals.get(1));
//         departament.setNom("Dep3");

//         ResponseEntity<Departament> response = controller.addDepartament(departament);

//         assertEquals(HttpStatus.CREATED, response.getStatusCode());
//         assertEquals("Dep3", response.getBody().getNom());
//         assertEquals(3, departaments.size());
//     }

//     @Test
//     void updateDepartament() {
//         when(service.getById(1L)).thenReturn(departaments.get(1));
//         when(sucursalService.getById(2L)).thenReturn(sucursals.get(1));
//         when(service.save(any(Departament.class))).thenAnswer(invocation -> invocation.getArgument(0));

//         Departament d = service.getById(1L);

//         d.setNom("NouDep");

//         ResponseEntity<Departament> response = controller.updateDepartament(1L, d);

//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertEquals("NouDep", response.getBody().getNom());
//         assertEquals("NouDep", departaments.get(1).getNom());

//         response = controller.updateDepartament(4L, d);

//         assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//     }

//     @Test
//     void deleteDepartament() {
//         when(service.getById(1L)).thenReturn(departaments.get(0));

//         ResponseEntity<Departament> response = controller.deleteDepartament(1L);

//         assertEquals(HttpStatus.OK, response.getStatusCode());

//         response = controller.deleteDepartament(3L);

//         assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//     }

// }
