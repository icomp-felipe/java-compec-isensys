package compec.ufam.isensys.pdf;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import javax.imageio.*;
import java.awt.image.*;

import com.phill.libs.*;

import compec.ufam.isensys.model.*;
import compec.ufam.isensys.model.retorno.*;
import compec.ufam.isensys.constants.*;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.*;

/** Constrói o relatório de processamento de retornos.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.8, 14/NOV/2023 */
public class PDFEstatisticas {

	/** Exporta o relatório de processamento do resultado preliminar para PDF, no diretório especificado.
	 *  @param cabecalho - cabeçalho do edital
	 *  @param contagemAtual - contador de deferiddos e indeferidos
	 *  @param listaProcessados - lista de arquivos processados
	 *  @param diretorioDestino - diretório de destino do arquivo PDF 
	 *  @throws JRException quando há algum problema ao gerar o relatório Jasper
	 *  @throws IOException quando algum arquivo de recursos não foi encontrado */
	public static void export(final String cabecalho, final int[] contagemAtual, final List<ArquivoProcessado> listaProcessados, final File diretorioDestino) throws JRException, IOException {
		export(Resultado.PRELIMINAR, cabecalho, contagemAtual, null, null, listaProcessados, diretorioDestino);
	}
	
	/** Exporta o relatório de processamento do resultado definitivo para PDF, no diretório especificado.
	 *  @param cabecalho - cabeçalho do edital
	 *  @param contagemAtual - contador de deferiddos e indeferidos
	 *  @param contagemAnterior - contador de deferiddos e indeferidos (antes do recurso)
	 *  @param listaRecursos - lista de recursantes 
	 *  @param listaProcessados - lista de arquivos processados
	 *  @param diretorioDestino - diretório de destino do arquivo PDF 
	 *  @throws JRException quando há algum problema ao gerar o relatório Jasper
	 *  @throws IOException quando algum arquivo de recursos não foi encontrado */
	public static void export(final String cabecalho, final int[] contagemAtual, final int[] contagemAnterior, final List<Retorno> listaRecursos, final List<ArquivoProcessado> listaProcessados, final File diretorioDestino) throws JRException, IOException {
		export(Resultado.DEFINITIVO, cabecalho, contagemAtual, contagemAnterior, listaRecursos, listaProcessados, diretorioDestino);
	}
	
	/** Exporta o relatório de processamento do resultado preliminar/definitivo para PDF, no diretório especificado.
	 *  @param tipoResultado - {@link Resultado}
	 *  @param cabecalho - cabeçalho do edital
	 *  @param contagemAtual - contador de deferiddos e indeferidos
	 *  @param contagemAnterior - contador de deferiddos e indeferidos (antes do recurso)
	 *  @param listaRecursos - lista de recursantes 
	 *  @param listaProcessados - lista de arquivos processados
	 *  @param diretorioDestino - diretório de destino do arquivo PDF
	 *  @throws JRException quando há algum problema ao gerar o relatório Jasper
	 *  @throws IOException quando algum arquivo de recursos não foi encontrado */
	private static void export(final Resultado tipoResultado, final String cabecalho, final int[] contagemAtual, final int[] contagemAnterior, final List<Retorno> listaRecursos, final List<ArquivoProcessado> listaProcessados, final File diretorioDestino) throws JRException, IOException {
		
		// Carregando imagem de cabeçalho (imagem)
		File imagePath = new File(ResourceManager.getResource("img/logo.jpg"));
		BufferedImage image = ImageIO.read(imagePath);
		
		// Carregando relatório Jasper
		String reportPath = ResourceManager.getResource("relatorios/Estatisticas.jasper");
		JasperReport report = (JasperReport) JRLoader.loadObjectFromFile(reportPath);
		
		final String stringResultado = StringUtils.BR.normaliza(tipoResultado.name());
		
		// Preparando parâmetros do relatório
		Map<String,Object> parameters = new HashMap<String,Object>();
		
		parameters.put("PAR_LOGO"             , image           );
		parameters.put("PAR_CABECALHO"        , cabecalho       );
		parameters.put("PAR_COUNT_DEF"        , contagemAtual[0]);
		parameters.put("PAR_COUNT_INDEF"      , contagemAtual[1]);
		parameters.put("PAR_LISTA_PROCESSADOS", listaProcessados);
		parameters.put("PAR_TIPO_RESULTADO"   , stringResultado );
		
		// Parâmetros exclusivos do resultado definitivo
		if (tipoResultado == Resultado.DEFINITIVO) {
			
			parameters.put("PAR_COUNT_LAST_DEF"  , contagemAnterior[0]);
			parameters.put("PAR_COUNT_LAST_INDEF", contagemAnterior[1]);
			
			// Recuperando dados
			Map<Boolean, List<Retorno>> map = listaRecursos.stream().collect(Collectors.groupingBy(Retorno::deferido));
			
			parameters.put("PAR_LISTA_DEFERIDOS"  , map.get(true ));
			parameters.put("PAR_LISTA_INDEFERIDOS", map.get(false));
			
		}
		
		// Construindo o relatório
		JasperPrint relatorio = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());

		// Preparando o arquivo de saída
		File arquivoDestino = new File(diretorioDestino, "Isenção - Processamento " + stringResultado + ".pdf");
		
		// Exportando pra PDF
		JasperExportManager.exportReportToPdfFile(relatorio, arquivoDestino.getAbsolutePath());
		
	}
	
}