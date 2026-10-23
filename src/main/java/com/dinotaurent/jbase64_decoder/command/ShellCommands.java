package com.dinotaurent.jbase64_decoder.command;

import com.dinotaurent.jbase64_decoder.service.ShellService;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class ShellCommands {


    private final ShellService service;

    public ShellCommands(ShellService service) {
        this.service = service;
    }

    @Command(name = "generar", description = "Comienza la decodificacion y generacion de los documentos")
    public void generar() throws IOException {
        service.verificarCarpetas();
        List<String> documentos = service.extraerDocumentos();

        if (documentos.isEmpty()) {
            return;
        }
        IO.println(documentos);

    }
}
