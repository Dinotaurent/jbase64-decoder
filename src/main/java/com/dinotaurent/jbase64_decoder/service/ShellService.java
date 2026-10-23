package com.dinotaurent.jbase64_decoder.service;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class ShellService {

    private final Terminal terminal;
    private final LineReader lineReader;

    public ShellService(Terminal terminal,@Lazy LineReader lineReader) {
        this.terminal = terminal;
        this.lineReader = lineReader;
    }


    public void extraerDocumentos(){
        IO.println("Se ejecuta el metodo extraerDocumentos");
    }
}
