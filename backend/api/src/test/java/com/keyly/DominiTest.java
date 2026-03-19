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

// import com.keyly.controller.DominiController;
// import com.keyly.model.Domini;
// import com.keyly.service.DominiService;

// @ExtendWith(MockitoExtension.class)
// public class DominiTest {

//     @Mock
//     private DominiService service;

//     @InjectMocks
//     private DominiController controller;

//     private List<Domini> dominis = new ArrayList<>(List.of(
//             new Domini(1L, "@gmail.com"),
//             new Domini(2L, "@insgabrielamistral.cat")));

//     @Test
//     void getAllDominis() {
//         when(service.getAllDominis()).thenReturn(dominis);

//         ResponseEntity<List<Domini>> response = controller.getAllDominis();

//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertEquals(2, response.getBody().size());
//         assertEquals("@insgabrielamistral.cat", response.getBody().get(1).getDomini());
//     }

//     @Test
//     void getDomini() {
//         when(service.getById(1L)).thenReturn(dominis.get(0));

//         ResponseEntity<Domini> response = controller.getDomini(1L);

//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertEquals("@gmail.com", response.getBody().getDomini());
//     }

//     @Test
//     void addDomini() {
//         when(service.save(any(Domini.class))).thenAnswer(invocation -> {
//             Domini d = invocation.getArgument(0);
//             d.setId((long) (dominis.size() + 1));
//             dominis.add(d);
//             return d;
//         });

//         Domini domini = new Domini();
//         domini.setDomini("@hotmail.com");

//         ResponseEntity<Domini> response = controller.addDomini(domini);

//         assertEquals(HttpStatus.CREATED, response.getStatusCode());
//         assertEquals(3, dominis.size());
//     }

//     @Test
//     void updateDomini() {
//         when(service.getById(1L)).thenReturn(dominis.get(0));
//         when(service.save(any(Domini.class))).thenAnswer(invocation -> invocation.getArgument(0));

//         Domini d = service.getById(1L);

//         d.setDomini("@yahoo.com");

//         ResponseEntity<Domini> response = controller.updateDomini(1L, d);

//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertEquals("@yahoo.com", response.getBody().getDomini());
//         assertEquals("@yahoo.com", dominis.get(0).getDomini());

//         response = controller.updateDomini(4L, d);

//         assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//     }

//     @Test
//     void deleteDomini() {
//         when(service.getById(1L)).thenReturn(dominis.get(0));

//         ResponseEntity<Domini> response = controller.deleteDomini(1L);

//         assertEquals(HttpStatus.OK, response.getStatusCode());

//         response = controller.deleteDomini(3L);

//         assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//     }

// }
