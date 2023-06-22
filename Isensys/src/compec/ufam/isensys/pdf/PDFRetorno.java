package compec.ufam.isensys.pdf;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import javax.imageio.*;
import java.awt.image.*;

import com.phill.libs.*;

import compec.ufam.isensys.constants.Resultado;
import compec.ufam.isensys.model.ArquivoProcessado;
import compec.ufam.isensys.model.retorno.*;

import net.sf.jasperreports.view.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.*;

/** Constrói o relatório de processamento de retornos.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.8, 22/JUN/2023 */
public class PDFRetorno {

	/** Monta o relatório de processamento de retornos.
	 *  @throws JRException quando há algum problema ao gerar o relatório Jasper
	 *  @throws IOException quando algum arquivo de recursos não foi encontrado */
	public static void show(final String windowTitle, final String cabecalho, final int[] atual, final int[] anterior, final Resultado tipoResultado, final List<Retorno> listaRetornos, final List<ArquivoProcessado> listaProcessados) throws JRException, IOException {
		
		// Carregando imagem de cabeçalho (imagem)
		File imagePath = new File(ResourceManager.getResource("img/logo.jpg"));
		BufferedImage image = ImageIO.read(imagePath);
		
		// Carregando relatório Jasper
		String reportPath = ResourceManager.getResource("relatorios/Retorno.jasper");
		JasperReport report = (JasperReport) JRLoader.loadObjectFromFile(reportPath);
		
		// Preparando parâmetros do relatório
		Map<String,Object> parameters = new HashMap<String,Object>();
		
		parameters.put("PAR_LOGO"           , image);
		parameters.put("PAR_CABECALHO"      , cabecalho);
		parameters.put("PAR_TIPO_RESULTADO", StringUtils.BR.normaliza(tipoResultado.name    ()));
		
		parameters.put("PAR_COUNT_DEF", atual[0]);
		parameters.put("PAR_COUNT_INDEF", atual[1]);
		
		parameters.put("PAR_LISTA_PROCESSADOS", listaProcessados);
		
		if (tipoResultado == Resultado.DEFINITIVO) {
			
			parameters.put("PAR_COUNT_LAST_DEF"  , anterior[0]);
			parameters.put("PAR_COUNT_LAST_INDEF", anterior[1]);
			
			// Recuperando dados
			Map<Boolean, List<Retorno>> map = listaRetornos.stream().collect(Collectors.groupingBy(Retorno::deferido));
			
			parameters.put("PAR_LISTA_DEFERIDOS"  , map.get(true ));
			parameters.put("PAR_LISTA_INDEFERIDOS", map.get(false));
			
		}
		
		// Gerando relatório
		JasperPrint  prints = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
		
		// Exibindo resultados
		JasperViewer viewer = new JasperViewer(prints, false);
		viewer.setTitle  (windowTitle);
		viewer.setVisible(true);
		
	}
	
}