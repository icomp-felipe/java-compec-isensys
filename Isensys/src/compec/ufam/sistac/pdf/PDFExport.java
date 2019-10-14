package compec.ufam.sistac.pdf;

import java.io.*;
import java.util.*;
import javax.imageio.*;
import com.phill.libs.*;
import java.awt.image.*;
import compec.ufam.sistac.model.*;
import net.sf.jasperreports.view.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;

/** Classe que controla a visualização do relatório em PDF com o resultado do processamento
 *  @author Felipe André
 *  @version 2.50, 08/07/2018 */
public class PDFExport {

	/** Monta o relatório (edital) de acordo com os parâmetros. Utiliza o arquivo já compilado (.jasper) */
	public static void export(ListaRetornos retornos, String cabecalho, Resultado tipoResultado) throws JRException, IOException {
		
		File imagePath = new File(ResourceManager.getResource("img/header.png"));
		BufferedImage image = ImageIO.read(imagePath);
		
		String reportPath = ResourceManager.getResource("relatorios/edital-isensys.jasper");
		JasperReport report = (JasperReport) JRLoader.loadObjectFromFile(reportPath);
		
		Map<String,Object> parameters = new HashMap<String,Object>();
		
		parameters.put("PAR_LOGO", image);
		parameters.put("PAR_CABECALHO", cabecalho);
		parameters.put("PAR_TIPO_RESULTADO",tipoResultado.name());
		parameters.put("PAR_LISTA_ERROS",SituacaoDAO.getErros());
		parameters.put("PAR_LISTA_RETORNOS", retornos.getList());
		
		JasperPrint  prints = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
		
		JasperViewer jrv = new JasperViewer(prints,false);
		jrv.setTitle(cabecalho);
		jrv.setVisible(true);
		
	}
	
	/** Compila e monta o relatório (edital) de acordo com os parâmetros. Utiliza o arquivo fonte (.jrxml) */
	public static void compileAndExport(ListaRetornos retornos, String cabecalho, Resultado tipoResultado) throws JRException, IOException {
		
		File imagePath = new File(ResourceManager.getResource("img/header.png"));
		BufferedImage image = ImageIO.read(imagePath);
		
		String reportPath = ResourceManager.getResource("relatorios/edital-isensys.jrxml");
		JasperReport report = JasperCompileManager.compileReport(reportPath);
		
		Map<String,Object> parameters = new HashMap<String,Object>();
		
		parameters.put("PAR_LOGO", image);
		parameters.put("PAR_CABECALHO", cabecalho);
		parameters.put("PAR_TIPO_RESULTADO",tipoResultado.name());
		parameters.put("PAR_LISTA_ERROS",SituacaoDAO.getErros());
		parameters.put("PAR_LISTA_RETORNOS", retornos.getList());
		
		JasperPrint  prints = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
		
		JasperViewer jrv = new JasperViewer(prints,false);
		jrv.setTitle(cabecalho);
		jrv.setVisible(true);
		
	}
	
}
