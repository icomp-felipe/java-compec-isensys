package compec.ufam.isensys.pdf;

import java.io.*;
import java.util.*;
import javax.imageio.*;
import java.awt.image.*;

import com.phill.libs.*;

import compec.ufam.isensys.model.retorno.*;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.*;

/** Constrói o relatório de similaridade.
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 3.9, 25/FEV/2025 */
public class PDFSimilaridade {

	/** Monta o relatório de distância e similaridade dos candidatos deferidos no recurso de isenção.
	 *  @param cabecalho - cabeçalho do edital
	 *  @param edital - dados do edital
	 *  @param listaDeferidos - lista de deferidos no recurso
	 *  @param diretorioDestino - diretório de destino do arquivo PDF
	 *  @throws JRException quando há algum problema ao gerar o relatório Jasper
	 *  @throws IOException quando algum arquivo de recursos não foi encontrado */
	public static void export(final String cabecalho, final String edital, final List<Similaridade> listaDeferidos, final File diretorioDestino) throws JRException, IOException {
		
		// Carregando imagem de cabeçalho (imagem)
		File imagePath = new File(ResourceManager.getResource("img/compec-header.png"));
		BufferedImage image = ImageIO.read(imagePath);
		
		// Carregando relatório Jasper
		String reportPath = ResourceManager.getResource("relatorios/Similaridades.jasper");
		JasperReport report = (JasperReport) JRLoader.loadObjectFromFile(reportPath);
		
		// Preparando parâmetros do relatório
		Map<String,Object> parameters = new HashMap<String,Object>();
		
		parameters.put("PAR_LOGO"           , image);
		parameters.put("PAR_CABECALHO"      , cabecalho);
		parameters.put("PAR_LISTA_SIMILARES", listaDeferidos);
		
		// Construindo o relatório
		JasperPrint relatorio = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());

		// Preparando o arquivo de saída
		String filename = String.format("Edital %s de %s - Isenção - Relatório de Similaridade.pdf", edital.substring(0, 2), edital.substring(2));
		File arquivoDestino = new File(diretorioDestino, filename);
		
		// Exportando pra PDF
		JasperExportManager.exportReportToPdfFile(relatorio, arquivoDestino.getAbsolutePath());
		
	}
	
}
