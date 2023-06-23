package compec.ufam.isensys.pdf;

import java.io.*;
import java.util.*;
import javax.imageio.*;
import java.awt.image.*;

import com.phill.libs.*;

import compec.ufam.isensys.constants.*;
import compec.ufam.isensys.model.retorno.*;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.*;

/** Classe que controla a visualização do relatório em PDF com o resultado do processamento.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.8, 22/JUN/2023 */
public class PDFEdital {

	/** Monta o relatório (edital) de acordo com os parâmetros. Utiliza o arquivo já compilado (.jasper).
     *  @param tipoResultado - {@link Resultado}
     *  @param cabecalho - cabeçalho do edital
	 *  @param listaRetornos - lista de retornos de processamento
	 *  @param diretorioDestino - diretório de destino do arquivo PDF
	 *  @throws JRException quando há algum problema ao gerar o relatório Jasper
	 *  @throws IOException quando algum arquivo de recursos não foi encontrado */
	public static void export(final Resultado tipoResultado, final String cabecalho, final List<Retorno> listaRetornos, final File diretorioDestino) throws JRException, IOException {
		
		// Carregando imagem de cabeçalho (imagem)
		File imagePath = new File(ResourceManager.getResource("img/logo.jpg"));
		BufferedImage image = ImageIO.read(imagePath);
		
		// Carregando relatório Jasper
		String reportPath = ResourceManager.getResource("relatorios/Edital.jasper");
		JasperReport report = (JasperReport) JRLoader.loadObjectFromFile(reportPath);
		
		// Preparando parâmetros do relatório
		Map<String,Object> parameters = new HashMap<String,Object>();
		
		parameters.put("PAR_LOGO"          , image                   );
		parameters.put("PAR_CABECALHO"     , cabecalho               );
		parameters.put("PAR_TIPO_RESULTADO", tipoResultado.name    ());
		parameters.put("PAR_LISTA_ERROS"   , SituacaoDAO  .getErros());
		parameters.put("PAR_LISTA_RETORNOS", listaRetornos           );
		
		// Construindo o relatório
		JasperPrint relatorio = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
		
		// Preparando o arquivo de saída
		File arquivoDestino = new File(diretorioDestino, "Isenção - Resultado " + StringUtils.BR.normaliza(tipoResultado.name()) + ".pdf");
		
		// Exportando pra PDF
		JasperExportManager.exportReportToPdfFile(relatorio, arquivoDestino.getAbsolutePath());
		
	}
	
}