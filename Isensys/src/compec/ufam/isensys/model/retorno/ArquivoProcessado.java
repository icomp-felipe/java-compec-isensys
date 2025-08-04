package compec.ufam.isensys.model.retorno;

import java.io.File;

public record ArquivoProcessado(File getArquivo, String getTipo) {};