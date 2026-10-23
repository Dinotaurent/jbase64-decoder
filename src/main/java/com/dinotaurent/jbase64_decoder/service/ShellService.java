package com.dinotaurent.jbase64_decoder.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;


@Service
public class ShellService {

    private final Terminal terminal;
    private final LineReader lineReader;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public ShellService(Terminal terminal,@Lazy LineReader lineReader) {
        this.terminal = terminal;
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
        IO.println("Se ejecuta el metodo extraerDocumentos");

        if (!validarIngreso()) {
            return List.of();
        }

        File archivo = Paths.get("./ingreso.txt").toFile();
        JsonNode raiz = MAPPER.readTree(archivo);

        return StreamSupport.stream(raiz.spliterator(), false)
                .map(nodo -> nodo.path("document").asText(""))
                .filter(valor -> !valor.isBlank())
                .toList();

    }

    public void generar(){}

    public void comprimir(){}

    private boolean validarIngreso() {
        Path ruta = Paths.get("./ingreso.txt");

        if (!Files.isRegularFile(ruta)) {
            IO.println("No se encontró el archivo de ingreso: " + ruta.toAbsolutePath());
            return false;
        }

        return true;
    }
}
