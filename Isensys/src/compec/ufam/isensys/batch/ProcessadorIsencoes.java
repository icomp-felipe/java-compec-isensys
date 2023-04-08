package compec.ufam.isensys.batch;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.*;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.poi.openxml4j.opc.*;
import org.apache.poi.xwpf.usermodel.*;

import com.phill.libs.StringUtils;
import com.phill.libs.br.*;
import com.phill.libs.files.*;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;
import com.pdf.export.LibreOfficePDFExporter;

/** Realiza uma série de validações nos formulários de isenção do PSI/UFAM.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 1.0, 27/MAR/2023 */
public class ProcessadorIsencoes {

	private static class Candidato {
		
		private String nome, cpf;
		
		public Candidato(final String row) {
			
			final String[] splitted = row.split(",");
			
			this.nome = StringUtils.BR.normaliza(StringUtils.wipeMultipleSpaces(splitted[0]));
			
			if (splitted.length > 1)
				this.cpf  = StringUtils.extractNumbers(splitted[1]);
			
		}
		
		public String getNome() {
			return this.nome;
		}

		public String getResume() {
			return String.format("%s-%s\n", this.cpf, this.nome);
		}
		
		@Override
		public boolean equals(Object obj) {
			return this.nome.equals(((Candidato) obj).getNome());
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		
		String solicitacoes = PhillFileUtils.readFileToString(new File("V:\\solicitacoes.csv"));
		String[] candidatos = solicitacoes.split("\n");
		
		String[] deferidos = PhillFileUtils.readFileToString(new File("Z:\\Google Drive\\COMPEC\\05. PSI 2023\\Isenção\\Definitivo\\Processamento\\deferidos.log")).split("\n");
		
		Arrays.sort(candidatos, Collator.getInstance(Locale.of("pt", "BR")));

		List<Candidato> listaCandidatos = new ArrayList<Candidato>(764);
		
		for (String candidato: candidatos)
			listaCandidatos.add(new Candidato(candidato));
		
		List<Candidato> listaDeferidos = new ArrayList<Candidato>(84);
		
		for (String deferido: deferidos)
			listaDeferidos.add(new Candidato(deferido));

		StringBuilder builder = new StringBuilder();
		
		for (Candidato deferido: listaDeferidos)
			if (listaCandidatos.contains(deferido))
				builder.append(listaCandidatos.get(listaCandidatos.indexOf(deferido)).getResume());
		
		final String candsDeferidos = builder.toString().trim();
		
		System.out.println(candsDeferidos);
		FileUtils.write(new File("Z:\\Google Drive\\COMPEC\\05. PSI 2023\\Isenção\\Definitivo\\Processamento\\deferidos-completo.txt"), candsDeferidos, StandardCharsets.UTF_8);
		
	}
	
	public static void main4(String[] args) throws IOException, DocumentException {
		
		File pdfDir = new File("Z:\\Google Drive\\COMPEC\\05. PSI 2023\\Isenção\\Definitivo\\Indeferidos\\PDF");
		
		List<File> pdfs = PhillFileUtils.filterByExtension(pdfDir, "pdf"); Collections.sort(pdfs);
		
		char currentChar = 'A';
		
		PDFMergerUtility merger = new PDFMergerUtility();
		
		System.out.print("merging...");
		
		for (File pdf: pdfs) {
			
			final char firstChar = pdf.getName().charAt(0);
			
			if (firstChar != currentChar) {
				
				merger.setDestinationFileName("R:\\merged\\Nomes Iniciados em " + currentChar + ".pdf");
				merger.mergeDocuments(null);
				
				merger = new PDFMergerUtility();
				merger.addSource(pdf);
				
				currentChar = firstChar;
				
			}
			else
				merger.addSource(pdf);
			
		}
		
		System.out.println("done");
		
		File mergedDir = new File("R:\\merged");
		File optimizedDir = new File("R:\\optimized");
		
		System.out.print("optimizing...");
		
		for (File merged: mergedDir.listFiles()) {
			
			File output = new File(optimizedDir, merged.getName());
			
			PdfReader reader = new PdfReader(new FileInputStream(merged));
			PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(output));
			
			stamper.setFullCompression();
			
			int total = reader.getNumberOfPages() + 1;
			
			for ( int i=1; i<total; i++)
			   reader.setPageContent(i + 1, reader.getPageContent(i + 1));
			
			stamper.close();
			
		}
		
		System.out.println("done");
		
	}
	
	public static void main3(String[] args) throws IOException {
		
    	final List<String> listFiles;
    	final Collator collator = Collator.getInstance(Locale.of("pt", "BR"));
		
		File formsDir = new File("D:\\Felipe's Files\\Downloads\\rec");
		File nomesCSV = new File("D:\\Felipe's Files\\Downloads\\respostas.csv");
		
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
    	listNomes.remove("Kevelane Castro de Oliveira");
    	listNomes.remove("Raquel Salomé de Mendonça");
    	listNomes.remove("Naldiel Conceição Fonseca");
    	
    	System.out.println("\n-> Formulários pendentes:\n");
    	
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
	
	public static void main2(String[] args) throws Exception {
		
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