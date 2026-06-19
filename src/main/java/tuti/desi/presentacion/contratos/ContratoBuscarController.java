package tuti.desi.presentacion.contratos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import tuti.desi.servicios.ContratoServicio;

@Controller
@RequestMapping("/contratosBuscar")
public class ContratoBuscarController {

    @Autowired
    private ContratoServicio service;

    @GetMapping
    public String buscar(Model model) {

        model.addAttribute("contratos", service.getAll());

        return "contratosBuscar";
    }
}

