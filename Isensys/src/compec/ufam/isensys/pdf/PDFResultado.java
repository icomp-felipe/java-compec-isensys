package compec.ufam.isensys.pdf;

import java.io.*;
import java.time.*;
import java.util.*;
import javax.imageio.*;
import java.awt.image.*;

import com.phill.libs.*;

import compec.ufam.isensys.constants.*;
import compec.ufam.isensys.model.Edital;
import compec.ufam.isensys.model.retorno.*;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.*;

/** Classe que controla a visualização do relatório em PDF com o resultado do processamento.
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 3.9, 25/FEV/2025 */
public class PDFResultado {

	/** Monta o relatório (edital) de acordo com os parâmetros. Utiliza o arquivo já compilado (.jasper).
     *  @param tipoResultado - {@link Resultado}
     *  @param cabecalho - cabeçalho do edital
     *  @param edital - dados do edital
     *  @param dataPublicacao - data de publicação do edital
	 *  @param listaRetornos - lista de retornos de processamento
	 *  @param diretorioDestino - diretório de destino do arquivo PDF
	 *  @throws JRException quando há algum problema ao gerar o relatório Jasper
	 *  @throws IOException quando algum arquivo de recursos não foi encontrado */
	public static void export(final Resultado tipoResultado, final String cabecalho, final Edital edital, final LocalDate dataPublicacao, final List<Retorno> listaRetornos, final File diretorioDestino) throws JRException, IOException {
		
		// Carregando imagem de cabeçalho (imagem)
		File imagePath = new File(ResourceManager.getResource("img/compec-header.png"));
		BufferedImage image = ImageIO.read(imagePath);
		
		// Carregando relatório Jasper
		String reportPath = ResourceManager.getResource("relatorios/Resultado.jasper");
		JasperReport report = (JasperReport) JRLoader.loadObjectFromFile(reportPath);
		
		// Preparando parâmetros do relatório
		Map<String,Object> parameters = new HashMap<String,Object>();
		
		parameters.put("PAR_LOGO"           , image               );
		parameters.put("PAR_DATA_PUBLICACAO", dataPublicacao      );
		parameters.put("PAR_CABECALHO"      , cabecalho           );
		parameters.put("PAR_TIPO_RESULTADO" , tipoResultado.name());
		parameters.put("PAR_LISTA_ERROS"    , SituacaoDAO  .load());
		parameters.put("PAR_LISTA_RETORNOS" , listaRetornos       );
		
		// Construindo o relatório
		JasperPrint relatorio = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
		
		// Preparando o arquivo de saída
		String filename = String.format("Edital %s de %s - Isenção - Resultado %s.pdf", edital.getNumeroEdital(), edital.getAnoEdital(), StringUtils.BR.normaliza(tipoResultado.name()));
		File arquivoDestino = new File(diretorioDestino, filename);
		
		// Exportando pra PDF
		JasperExportManager.exportReportToPdfFile(relatorio, arquivoDestino.getAbsolutePath());
		
	}
	
}