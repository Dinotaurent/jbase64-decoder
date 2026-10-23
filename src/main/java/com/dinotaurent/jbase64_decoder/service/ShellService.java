package com.dinotaurent.jbase64_decoder.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jline.reader.LineReader;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
public class ShellService {

    private final LineReader lineReader;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public ShellService(@Lazy LineReader lineReader) {
        this.lineReader = lineReader;
    }

    public void verificarCarpetas(){
        try {

            // Definir la rutas relativas.
            Path dirTxt = Paths.get("./salida_txt");
            Path dirZip = Paths.get("./salida_zip");

            // Verifica si exiten y las crea de ser necesario.
            if (!Files.exists(dirTxt)){
                Files.createDirectories(dirTxt);
//                IO.println("Creo la carpeta txt");
            }

            if (!Files.exists(dirZip)){
                Files.createDirectory(dirZip);
//                IO.println("Creo la carpeta zip");
            }

        } catch (IOException e){
            throw new UncheckedIOException("No se pudieron crear los directorios de trabajo", e);
        }


    }

    public List<String> extraerDocumentos() throws IOException {
//        IO.println("Se ejecuta el metodo extraerDocumentos");

        if (!validarIngreso()) {
            return null;
        }

        File archivo = Paths.get("./ingreso.txt").toFile();
        JsonNode raiz = MAPPER.readTree(archivo);

        return StreamSupport.stream(raiz.spliterator(), false)
                .map(nodo -> nodo.path("document").asText(""))
                .filter(valor -> !valor.isBlank())
                .toList();

    }

    public void generar(List<String> documentos) throws IOException {
        limpiarCarpetas();

        try {
            for(int i=0;i<documentos.size();i++){
                // Decodificar el documento.
                byte[] salida = Base64.getDecoder().decode(documentos.get(i).getBytes(StandardCharsets.UTF_8));

                // Construir la ruta de salida dinamica.
                Path rutaSalidaTxt = Paths.get("./salida_txt", "documento_" + i + ".txt");

                // Crear el documento .txt.
                Files.write(rutaSalidaTxt, salida);
                IO.println("Se crea el documento: " + rutaSalidaTxt.getFileName().toString());
            }

            if(preguntar()){
                comprimir();
            } else {
                IO.println("No se genero el .zip a solicitud del usuario.");
            }
        } catch (Exception e){
            IO.println("No se pudo generar el documento: "  + e.getMessage());
        }
    }

    private void comprimir(){
//        IO.println("Se ejecuta el metodo comprimir");
        Path rutaOrigenTxt = Paths.get("./salida_txt");
        Path rutaSalidaZip = Paths.get("./salida_zip", "documentos_procesados.zip");

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(rutaSalidaZip));
             Stream<Path> archivos = Files.walk(rutaOrigenTxt)) {

            for (Path pathTxt : (Iterable<Path>) archivos
                    .filter(p -> Files.isRegularFile(p) && p.toString().endsWith(".txt"))::iterator)
            {
                ZipEntry zipEntry = new ZipEntry(rutaOrigenTxt.relativize(pathTxt)
                        .toString().replace('\\', '/'));

                zos.putNextEntry(zipEntry);
                Files.copy(pathTxt, zos);
                zos.closeEntry();
            }
            IO.println("Se genero el archivo comprimido en: " + rutaSalidaZip);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validarIngreso() {
        Path ruta = Paths.get("./ingreso.txt");

        if (!Files.isRegularFile(ruta)) {
            IO.println("No se encontró el archivo de ingreso: " + ruta.toAbsolutePath());
            return false;
        }

        return true;
    }

    private boolean preguntar() {
        while (true) {
            String input = lineReader.readLine( "¿Desea generar el .zip? (y/n): ")
                    .trim()
                    .toLowerCase();

            if (input.equals("y")) return true;
            if (input.equals("n")) return false;

            IO.println("Opción inválida — ingresa 'y' o 'n'");
        }
    }

    private void limpiarCarpetas() throws IOException {
        limpiarCarpeta(Paths.get("./salida_txt"));
        limpiarCarpeta(Paths.get("./salida_zip"));
    }

    private void limpiarCarpeta(Path carpeta) throws IOException {
        if (!Files.exists(carpeta)) {
            return;
        }

        try (Stream<Path> rutas = Files.walk(carpeta)) {
            rutas.sorted(Comparator.reverseOrder())
                    .filter(ruta -> !ruta.equals(carpeta))
                    .forEach(ruta -> {
                        try {
                            Files.delete(ruta);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
    }
}
