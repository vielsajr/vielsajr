package dwes.examen1.controller;

import dwes.examen1.model.Actividad;
import dwes.examen1.service.ActividadService;
import dwes.examen1.service.MonitorService;
import dwes.examen1.util.FileUploadUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/actividades")
public class ActividadController {

    @Autowired
    private ActividadService actividadService;
    @Autowired
    private MonitorService monitorService;

    @GetMapping("")
    public String index(@RequestParam(name = "monitor", required = false) Long monitorId, Model model) {
        if (monitorId != null) {
            model.addAttribute("actividad", actividadService.findByMonitor(monitorId));
        } else {
            model.addAttribute("actividad", actividadService.findAll());
        }
        return "actividades/index";
    }

    @GetMapping("/crear")
    public String create(Model model){
        model.addAttribute("monitor", monitorService.findAll());
        model.addAttribute("actividad", new Actividad());
        return "actividades/create";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Actividad actividad, BindingResult result,
                          @RequestParam(value = "archivo", required = false) MultipartFile archivo,
                          RedirectAttributes flash, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("monitor", monitorService.findAll());
            return "actividades/create";
        }
        try {
            String nombreImagen = FileUploadUtil.guardar(archivo, "uploads/imagenes");
            if (nombreImagen != null) {
                actividad.setImagen(nombreImagen);
            }
        } catch (IOException e) {
            model.addAttribute("monitor", monitorService.findAll());
            model.addAttribute("error", "No se pudo guardar la imagen");
            return "actividades/create";
        }
        actividad.setMonitor(monitorService.findById(actividad.getMonitor().getId()));
        actividadService.guardar(actividad);
        flash.addFlashAttribute("mensaje", "Actividad " + actividad.getNombre() + " añadida correctamente");
        return "redirect:/actividades";
    }

    @DeleteMapping("/{id}/borrar")
    public String delete(@PathVariable("id") Long id, RedirectAttributes flash) {
        Actividad actividad = actividadService.findById(id);
        if (actividad == null) {
            flash.addFlashAttribute("error", "La actividad no existe");
            return "redirect:/actividades";
        }
        FileUploadUtil.borrar(actividad.getImagen(), "uploads/imagenes");
        actividadService.eliminar(id);
        flash.addFlashAttribute("mensaje", "Actividad " + actividad.getNombre() + " eliminada con éxito");
        return "redirect:/actividades";
    }
}
