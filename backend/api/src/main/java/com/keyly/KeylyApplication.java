package com.keyly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
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
