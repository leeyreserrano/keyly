package com.keyly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
	TODO - HACER DTO
	TODO - Optimizar consultas
	TODO - Devolver los errores en formato json
	TODO - Darle un par de vueltas a los delete, se hace una consulta innecesaria desde mi punto de vista en el controller
	TODO - Cuando se crean varios items de golpe, ir informando del estado, si se crea el 1 que avise y pase al dos, si 
	alguno falla que lo indique también y siga trabajando
	TODO - Las fechas dentro de la bd tienen que estar en formato y-m-d h-m-s
*/

@SpringBootApplication
public class KeylyApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeylyApplication.class, args);
	}

}
