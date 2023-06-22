package compec.ufam.isensys.pdf;

import java.io.*;
import java.util.*;
import javax.imageio.*;
import java.awt.image.*;

import com.phill.libs.*;

import compec.ufam.isensys.constants.*;
import compec.ufam.isensys.model.retorno.*;

import net.sf.jasperreports.view.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.*;

/** Classe que controla a visualização do relatório em PDF com o resultado do processamento.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.8, 21/JUN/2023 */
public class PDFEdital {

	/** Monta o relatório (edital) de acordo com os parâmetros. Utiliza o arquivo já compilado (.jasper).
	 *  @param retornos - lista de retornos de processamento
	 *  @param cabecalho - cabeçalho do edital
	 *  @param windowTitle - título da janela
	 *  @param tipoResultado - tipo de resultado (preliminar ou final)
	 *  @throws JRException quando há algum problema ao gerar o relatório Jasper
	 *  @throws IOException quando algum arquivo de recursos não foi encontrado */
	public static void export(final ListaRetornos retornos, final String cabecalho, final String windowTitle, final Resultado tipoResultado) throws JRException, IOException {
		
		// Carregando imagem de cabeçalho (imagem)
		File imagePath = new File(ResourceManager.getResource("img/logo.jpg"));
		BufferedImage image = ImageIO.read(imagePath);
		
		// Carregando relatório Jasper
		String reportPath = ResourceManager.getResource("relatorios/Edital.jasper");
		JasperReport report = (JasperReport) JRLoader.loadObjectFromFile(reportPath);
		
		// Preparando parâmetros do relatório
		Map<String,Object> parameters = new HashMap<String,Object>();
		
		parameters.put("PAR_LOGO"          , image);
		parameters.put("PAR_CABECALHO"     , cabecalho);
		parameters.put("PAR_TIPO_RESULTADO", tipoResultado.name    ());
		parameters.put("PAR_LISTA_ERROS"   , SituacaoDAO  .getErros());
		parameters.put("PAR_LISTA_RETORNOS", retornos     .getList ());
		
		// Gerando relatório
		JasperPrint  prints = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
		
		// Exibindo resultados
		JasperViewer jrv = new JasperViewer(prints, false);
		jrv.setTitle  (windowTitle);
		jrv.setVisible(true);
		
	}
	
	/** Monta o relatório (edital) de acordo com os parâmetros. Utiliza o arquivo fonte (.jrxml).
	 *  @param retornos - lista de retornos de processamento
	 *  @param cabecalho - cabeçalho do edital
	 *  @param windowTitle - título da janela
	 *  @param tipoResultado - tipo de resultado (preliminar ou final)
	 *  @throws JRException quando há algum problema ao gerar o relatório Jasper
	 *  @throws IOException quando algum arquivo de recursos não foi encontrado */
	public static void compileAndExport(final ListaRetornos retornos, final String cabecalho, final String windowTitle, final Resultado tipoResultado) throws JRException, IOException {
		
		// Carregando imagem de cabeçalho (imagem)
		File imagePath = new File(ResourceManager.getResource("img/logo.jpg"));
		BufferedImage image = ImageIO.read(imagePath);
		
		// Compilando relatório Jasper
		String reportPath = ResourceManager.getResource("relatorios/Edital.jrxml");
		JasperReport report = JasperCompileManager.compileReport(reportPath);
		
		// Preparando parâmetros do relatório
		Map<String,Object> parameters = new HashMap<String,Object>();
		
		parameters.put("PAR_LOGO"          , image);
		parameters.put("PAR_CABECALHO"     , cabecalho);
		parameters.put("PAR_TIPO_RESULTADO", tipoResultado.name    ());
		parameters.put("PAR_LISTA_ERROS"   , SituacaoDAO  .getErros());
		parameters.put("PAR_LISTA_RETORNOS", retornos     .getList ());
		
		// Gerando relatório
		JasperPrint  prints = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
		
		// Exibindo resultados
		JasperViewer jrv = new JasperViewer(prints,false);
		jrv.setTitle(windowTitle);
		jrv.setVisible(true);
		
	}
	
}