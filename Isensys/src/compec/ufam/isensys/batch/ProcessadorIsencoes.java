package compec.ufam.isensys.batch;

import java.io.*;
import java.util.*;

import org.apache.commons.io.*;
import org.apache.poi.openxml4j.opc.*;
import org.apache.poi.xwpf.usermodel.*;

import org.jodconverter.core.document.*;

import com.phill.libs.*;
import com.phill.libs.br.*;
import com.phill.libs.files.*;

import com.pdf.export.LibreOfficePDFExporter;

/** Realiza uma série de validações nos formulários de isenção do PSI/UFAM.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 1.0, 27/MAR/2023 */
public class ProcessadorIsencoes {

	public static void main(String[] args) throws Exception {
		
		// Diretórios
		File inputDir      = new File("/home/felipe/indeferidos");
		File docxOutputDir = new File(inputDir, "docx");
		File pdfOutputDir  = new File(inputDir, "pdf" );
		
		// Criando diretórios
		docxOutputDir.mkdirs(); pdfOutputDir.mkdirs();
		
		// Recuperando apenas os arquivos do Word (.docx)
		List<File> filtered = PhillFileUtils.filterByExtension(inputDir, "docx"); Collections.sort(filtered);
		List<File> exported = new ArrayList<File>(filtered.size());
		
		// Preparando filtros de exportação pra PDF
    	final Map<String, Object> filterData = new HashMap<>();
    	filterData.put("ExportFormFields", false);

    	final Map<String, Object> customProperties = new HashMap<>();
    	customProperties.put("Overwrite", true);
    	customProperties.put("FilterData", filterData);
		
    	// Índice para impressão
    	int i = 1;
    	
		for (File docx: filtered) {
			
			// Preparando nome do arquivo
			String candName = FilenameUtils.removeExtension(docx.getName());
			String filename = StringUtils.BR.normaliza(StringUtils.wipeMultipleSpaces(candName));
			
			// Preparando arquivos de saída
			File docxFile = new File(docxOutputDir, filename + ".docx");
			//File pdfFile  = new File(pdfOutputDir , filename + ".pdf" );
			
			exported.add(docxFile);
			
			// Imprimindo status
			System.out.printf("Processando arquivo %d/%d: '%s'\n", i++, filtered.size(), filename);
			
			// Carregando formulário do word
			XWPFDocument document = new XWPFDocument(OPCPackage.open(docx));
			
			// Aplicando as correções
			documentFixes(document, candName, filename);
			
			// Preparando a saída
			final FileOutputStream fileOutputStream = new FileOutputStream(docxFile);
			final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			
			// Exportando o novo formulário do word pra uma stream em memória.
			// Desse modo, consigo aproveitar os mesmos dados para gerar o novo .docx e o .pdf
			document.write(outputStream);
			document.close();
			
			// Gerando o novo arquivo .docx
			fileOutputStream.write(outputStream.toByteArray());
			fileOutputStream.close();
			
			// Gerando o novo arquivo .pdf
		    //LibreOfficePDFExporter.toPDF(outputStream.toByteArray(), DefaultDocumentFormatRegistry.DOCX, pdfFile, customProperties);
				
		}
		
		LibreOfficePDFExporter.toPDF(exported, pdfOutputDir, customProperties, true);
		
	}
	
	/** Implementa as correções nos formulários de isenção.
	 *  @param document - documento do Word (Apache POI)
	 *  @param candName - nome do candidato solicitante
	 *  @param filename - nome do candidato com as correções e já normalizado */
	private static void documentFixes(final XWPFDocument document, final String candName, final String filename) {
		
		for (XWPFParagraph p: document.getParagraphs()) {
			
			List<XWPFRun> runs = p.getRuns();
				
			if (runs != null) {
					
				for (XWPFRun r: runs) {
						
					String text = r.getText(0);
					//System.out.printf("'%s'\n", text);
					
					if (text != null) {
						
						// Normaliza o nome do candidato
						if (text.contains(candName)) {
							
							text = text.replace(candName, filename);
							r.setText(text, 0);
								
						}
						
						// Converte 'E-mail' para 'e-mail'
						else if (text.contains("E-mail")) {
								
							text = text.replace("E-mail", "e-mail");
							r.setText(text,0);
								
						}

						// Adiciona espaço antes do edital
						else if (text.contains("- Edital")) {

							text = text.trim().replace("- Edital", " - Edital");
							r.setText(text,0);

						}

						// Tratamento de máscara de CPF
						else if (text.contains("-XXX-XXX-")) {

							text = text.replace("-XXX-XXX-", ".XXX.XXX-");
							r.setText(text,0);

						}
						
						// Tratamento de máscara de CPF
						else if (text.contains("XXX-XXX")) {

							text = text.replace("XXX-XXX", "XXX.XXX");
							r.setText(text,0);

						}
						
						// Oculta números de CPF
						else if (CPFParser.parse(text)) {

							if (text.length() == 14) {
								
								final String cpf = String.format("%s.XXX.XXX-%s", text.substring(0,3), text.substring(12));
									
								text = text.replace(text, cpf);
								r.setText(text,0);
									
							}
							else if (text.length() == 11) {
								
								final String cpf = String.format("%s.XXX.XXX-%s", text.substring(0,3), text.substring(10));
									
								text = text.replace(text, cpf);
								r.setText(text,0);
									
							}
							
						}
						
					}
						
				}
				
			}
				
		}
		
	}

}