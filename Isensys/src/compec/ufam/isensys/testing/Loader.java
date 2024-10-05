package compec.ufam.isensys.testing;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.phill.libs.StringUtils;

import compec.ufam.isensys.model.retorno.Retorno;

public class Loader {

	public static void main(String[] args) throws Exception {
		
		File fileIsensys = new File("D:\\lista-isensys.csv");
		File filePSConcursos = new File("D:\\lista-psconcursos.csv");
		
		String[] dataIsensys = FileUtils.readFileToString(fileIsensys, StandardCharsets.UTF_8).split("\n");
		String[] dataPSConcursos = FileUtils.readFileToString(filePSConcursos, StandardCharsets.UTF_8).split("\n");
		
		List<Retorno> listaIsensys = parse(dataIsensys);
		List<Retorno> listaPSConcursos = parse(dataPSConcursos);
		
		/*for (Retorno retorno: listaIsensys) {
			if (listaPSConcursos.contains(retorno))
				listaPSConcursos.remove(retorno);
			else
				System.out.println("Isensys: " + retorno.toString());
		}*/
		
		for (Retorno retorno: listaPSConcursos) {
			if (listaIsensys.contains(retorno))
				listaIsensys.remove(retorno);
			else
				System.out.println("PSConcursos: " + retorno.toString());
		}
		
	}
	
	private static List<Retorno> parse(final String[] data) {
		
		List<Retorno> listaRetornos = new ArrayList<Retorno>(data.length);
		
		for (String line: data) {
			
			String[] split = line.split(",");
			Retorno retorno = new Retorno(StringUtils.wipeMultipleSpaces(split[0]).toUpperCase(),
										  StringUtils.extractNumbers(split[2]),
										  split[1].trim(),
										  split[3].trim().equals("deferido") ? "S" : "N",
										  "");
			
			listaRetornos.add(retorno);
		}
		
		return listaRetornos;
	}

}