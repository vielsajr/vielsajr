package dwes.examen1.util;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class FileUploadUtil {

    /**
     * Guarda el archivo subido en la carpeta indicada.
     * Devuelve el nombre con el que se ha guardado, o null si no se subió ningún archivo.
     */
    public static String guardar(MultipartFile archivo, String carpeta) throws IOException {
        if (archivo == null || archivo.isEmpty()) {
            return null;
        }
        String original = StringUtils.cleanPath(archivo.getOriginalFilename());
        String nombre = UUID.randomUUID() + "_" + original;

        Path directorio = Paths.get(carpeta);
        Files.createDirectories(directorio);
        Files.copy(archivo.getInputStream(), directorio.resolve(nombre), StandardCopyOption.REPLACE_EXISTING);

        return nombre;
    }

    /**
     * Borra un archivo de la carpeta indicada, si existe.
     * Si el archivo no está en esa carpeta (por ejemplo, las imágenes de static/img), no hace nada.
     */
    public static void borrar(String nombre, String carpeta) {
        if (nombre == null || nombre.isBlank()) {
            return;
        }
        // Solo se borran imágenes subidas desde la app (empiezan por un UUID).
        // Las de las monedas iniciales (peseta.jpg, yen.jpg...) no se tocan.
        if (!nombre.matches("^[0-9a-fA-F\\-]{36}_.+")) {
            return;
        }
        try {
            Files.deleteIfExists(Paths.get(carpeta).resolve(nombre));
        } catch (IOException e) {
            System.out.println("No se pudo borrar la imagen " + nombre + ": " + e.getMessage());
        }
    }
}
