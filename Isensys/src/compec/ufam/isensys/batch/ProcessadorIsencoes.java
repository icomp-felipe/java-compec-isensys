package compec.ufam.isensys.batch;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.*;
import org.apache.poi.openxml4j.opc.*;
import org.apache.poi.xwpf.usermodel.*;

import com.phill.libs.*;
import com.phill.libs.br.*;
import com.phill.libs.files.*;

import com.pdf.export.LibreOfficePDFExporter;

/** Realiza uma série de validações nos formulários de isenção do PSI/UFAM.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 1.0, 27/MAR/2023 */
public class ProcessadorIsencoes {

	public static void main2(String[] args) throws IOException {
		
    	final List<String> listFiles;
    	final Collator collator = Collator.getInstance(Locale.of("pt", "BR"));
		
		File formsDir = new File("/Windows/Felipe's Files/Downloads/Isenções");
		File nomesCSV = new File("/Windows/solicitacoes-full-processed.csv");
		
		// Carregando nomes do csv
		String[] nomes = PhillFileUtils.readFileToString(nomesCSV).split("\n");
		
		// Normalizando os nomes
		for (int i=0; i<nomes.length; i++)
			nomes[i] = StringUtils.BR.normaliza(StringUtils.wipeMultipleSpaces(nomes[i]));
		
		// Ordenando a lista de nomes
		Arrays.sort(nomes, collator);
		
		// Convertendo pra lista
		List<String> listNomes = new ArrayList<String>(Arrays.asList(nomes));
		
		// Buscando formulários (.docx)
    	try (Stream<Path> walker = Files.walk(formsDir.toPath())) {
    		
    		listFiles = walker.filter (path -> !Files.isDirectory(path))
    				         .map    (path -> StringUtils.BR.normaliza(StringUtils.wipeMultipleSpaces(path.toFile().getName())))
    				         .filter (file -> file.toLowerCase().endsWith("docx"))
    				         .collect(Collectors.toList());
    		
    	}
    	
    	// Ordenando lista de arquivos
    	Collections.sort(listFiles, collator);

    	System.out.println("Tamanho inicial da lista de nomes: "    + listNomes.size());
    	System.out.println("Tamanho da lista de arquivos: " + listFiles.size()); System.out.println();
    	
    	// Removendo da lista de nomes os formulários preenchidos
    	for (String arquivo: listFiles) {
    		
    		arquivo = arquivo.replace(".docx", "");
    		
    		if (listNomes.contains(arquivo))
    			listNomes.remove(arquivo);
    		else
    			System.out.println(arquivo);
    		
    	}
    	
    	// Removendo duplicados incorporados no arquivo
    	listNomes.remove("Carina Marinho Fugaca");
    	listNomes.remove("Eliana Borges Xavier");
    	listNomes.remove("Elissandra Coelho do Nascimento");
    	listNomes.remove("Eugenia Borges Xavier");
    	listNomes.remove("Flaviele Nascimento Marinho");
    	listNomes.remove("Geovane Marinho de Aguiar");
    	listNomes.remove("Geovane Marinho de Aguiar");
    	listNomes.remove("Jose Torres Curico Filho");
    	listNomes.remove("Kaiky Machado da Costa");
    	listNomes.remove("Karen Christynne Costa de Souza");
    	listNomes.remove("Keure Brasil da Silva");
    	listNomes.remove("Ledeilson Gabriel da Silva");
    	listNomes.remove("Maria Estela Amazonas da Silva");
    	listNomes.remove("Maynara Costa de Souza");
    	listNomes.remove("Rafaela Ferreira Batista");
    	listNomes.remove("Tacila Santos Silva");
    	
    	System.out.println("-> Formulários pendentes:\n");
    	
    	for (String nome: listNomes)
    		System.out.println(nome); System.out.println();
    	
    	System.out.println("Tamanho final da lista de nomes: " + listNomes.size());
    	
	}
	
	public static void main1(String[] args) throws Exception {
		
		File inputDir  = new File("/Windows/Felipe's Files/Google Drive/COMPEC/05. PSI 2023/Isenção/Daiana/Indeferidos");
		File outputDir = new File("/Windows/pdf-daiana"); outputDir.mkdirs();
		
		List<File> filtered = PhillFileUtils.filterByExtension(inputDir, "docx"); Collections.sort(filtered);
		
		// Preparando filtros de exportação pra PDF
    	final Map<String, Object> filterData = new HashMap<>();
    	filterData.put("ExportFormFields", false);

    	final Map<String, Object> customProperties = new HashMap<>();
    	customProperties.put("Overwrite", true);
    	customProperties.put("FilterData", filterData);
		
		LibreOfficePDFExporter.toPDF(filtered, outputDir, customProperties, true);
		
	}
	
	public static void main(String[] args) throws Exception {
		
		// Diretórios
		File inputDir      = new File("/Windows/Felipe's Files/Google Drive/COMPEC/05. PSI 2023/Isenção/Preliminar/Deferidos");
		File docxOutputDir = new File("/Windows", "docx-defs");
		//File pdfOutputDir  = new File("/Windows", "pdf-nilba" );
		
		// Criando diretórios
		docxOutputDir.mkdirs(); // pdfOutputDir.mkdirs();
		
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
			
		}
		
		//LibreOfficePDFExporter.toPDF(exported, pdfOutputDir, customProperties, true);
		
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
					
					r.setFontFamily("Arial");
						
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