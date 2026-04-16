package com.keyly.controller;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/compartit")
@Tag(name = "Compartit Controller", description = "Operacions sobre la taula Compartits")
public class CompartitController {

}
