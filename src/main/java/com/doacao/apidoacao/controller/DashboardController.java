package com.doacao.apidoacao.controller;

import com.doacao.apidoacao.dto.DashboardResumoDTO;
import com.doacao.apidoacao.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard", description = "Indicadores gerais da plataforma para administradores")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/resumo")
    @Operation(summary = "Retorna os indicadores gerais da plataforma")
    public DashboardResumoDTO resumo() {
        return dashboardService.resumo();
    }
}
