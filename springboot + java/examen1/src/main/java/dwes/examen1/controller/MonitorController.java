package dwes.examen1.controller;

import dwes.examen1.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MonitorController {

    @Autowired
    private MonitorService monitorService;

    @GetMapping("/monitores")
    public String index(Model model){
        model.addAttribute("monitores", monitorService.findAll());
        return "monitores/index";
    }
}
