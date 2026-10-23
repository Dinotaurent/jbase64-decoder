package com.dinotaurent.jbase64_decoder.service;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ShellService {

    private final Terminal terminal;
    private final LineReader lineReader;

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
                IO.println("Creo la carpeta txt");
            }

            if (!Files.exists(dirZip)){
                Files.createDirectory(dirZip);
                IO.println("Creo la carpeta zip");
            }

        } catch (IOException e){
            throw new UncheckedIOException("No se pudieron crear los directorios de trabajo", e);
        }


    }

    public void extraerDocumentos(){
        IO.println("Se ejecuta el metodo extraerDocumentos");
    }

    public void generar(){}

    public void comprimir(){}
}
