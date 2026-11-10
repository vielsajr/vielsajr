package dwes.examen1.controller;

import dwes.examen1.service.InstalacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/instalaciones")
public class InstalacionController {

    @Autowired
    private InstalacionService instalacionService;

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("historial", instalacionService.historialReservas());
        model.addAttribute("masCaras", instalacionService.findMasCarasQue(10));
        model.addAttribute("precioTotal", instalacionService.calcularPrecioTotal(2));
        model.addAttribute("reservables", instalacionService.contarReservables());
        model.addAttribute("masCara", instalacionService.obtenerMasCara());
        return "instalaciones/index";
    }
}
