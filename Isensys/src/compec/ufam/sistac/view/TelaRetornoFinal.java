package compec.ufam.sistac.view;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.awt.*;
import javax.swing.*;
import com.phill.libs.*;
import com.phill.libs.ui.*;
import com.phill.libs.files.PhillFileUtils;
import com.phill.libs.i18n.PropertyBundle;

import compec.ufam.sistac.exception.*;
import compec.ufam.sistac.io.*;
import compec.ufam.sistac.pdf.*;
import compec.ufam.sistac.model.*;
import net.sf.jasperreports.engine.*;

/** Classe que controla a view de processamento Retorno Final
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.0, 12/04/2021 */
public class TelaRetornoFinal extends JFrame {

	// Serial
	private static final long serialVersionUID = 3675211848533443138L;
	
	// Carregando bundle de idiomas
	private final static PropertyBundle bundle = new PropertyBundle("i18n/tela-retorno-final", null);
	
	// Declaração de atributos gráficos
	private final ImageIcon loadingIcon = new ImageIcon(ResourceManager.getResource("img/loader.gif"));
	
	
	
	private static final String MSG_LOAD_FILE = "Processando Arquivo";
	private static final String MSG_LOAD_PDF  = "Gerando Visualização";
	
	private static final int BSF_READ = 0, TXT_READ = 1, XLSX_READ = 2, PDF_EXPORT = 3;
	
	private JTextField textRetorno,textErros,textCabecalho;
	private File retornoSistac,retornoExcel,compilacao;
	private ListaRetornos listaRetornos;
	private JLabel labelStatus;
	private JTextField textCompilacao;
	private JLabel textDeferidos;
	private JLabel textIndeferidos;
	private JLabel textTotal;
	private JPanel panelResults;

	public static void main(String[] args) {
		new TelaRetornoFinal();
	}
	
	public TelaRetornoFinal() {
		
		// Recuperando o título da janela
		setTitle(bundle.getString("final-window-title"));
		
		// Inicializando atributos gráficos
		GraphicsHelper instance = GraphicsHelper.getInstance();
		GraphicsHelper.setFrameIcon(this,"icon/isensys-icon.png");
		Dimension dimension = new Dimension(500,400);
		
		JPanel painel = new JPaintedPanel("img/final-screen.jpg", dimension);
		painel.setLayout(null);
		setContentPane(painel);
		
		// Recuperando ícones
		Icon clearIcon  = ResourceManager.getIcon("icon/clear.png" ,20,20);
		Icon reloadIcon = ResourceManager.getIcon("icon/reload.png",20,20);
		Icon searchIcon = ResourceManager.getIcon("icon/search.png",20,20);
		Icon exitIcon   = ResourceManager.getIcon("icon/exit.png"  ,25,25);
		Icon reportIcon = ResourceManager.getIcon("icon/report.png",25,25);
		
		// Recuperando fontes e cores
		Font  fonte = instance.getFont ();
		Color color = instance.getColor();
		
		// Painel 'Retorno Preliminar'
		JPanel panelPreliminar = new JPanel();
		panelPreliminar.setOpaque(false);
		panelPreliminar.setLayout(null);
		panelPreliminar.setBorder(instance.getTitledBorder(bundle.getString("final-panel-prelim")));
		panelPreliminar.setBounds(12, 10, 476, 125);
		painel.add(panelPreliminar);
		
		JLabel labelCompilacao = new JLabel(bundle.getString("final-label-compilacao"));
		labelCompilacao.setHorizontalAlignment(JLabel.RIGHT);
		labelCompilacao.setFont(fonte);
		labelCompilacao.setBounds(10, 30, 90, 20);
		panelPreliminar.add(labelCompilacao);
		
		textCompilacao = new JTextField();
		textCompilacao.setToolTipText(bundle.getString("hint-text-compilacao"));
		textCompilacao.setForeground(color);
		textCompilacao.setFont(fonte);
		textCompilacao.setEditable(false);
		textCompilacao.setBounds(105, 30, 238, 25);
		panelPreliminar.add(textCompilacao);
		
		JButton buttonCompilacaoReload = new JButton(reloadIcon);
		buttonCompilacaoReload.setToolTipText(bundle.getString("hint-button-compilacao-reload"));
		buttonCompilacaoReload.setBounds(355, 30, 30, 25);
		panelPreliminar.add(buttonCompilacaoReload);
		
		JButton buttonCompilacaoClear = new JButton(clearIcon);
		buttonCompilacaoClear.setToolTipText(bundle.getString("hint-button-compilacao-clear"));
		buttonCompilacaoClear.setBounds(395, 30, 30, 25);
		panelPreliminar.add(buttonCompilacaoClear);
		
		JButton buttonCompilacaoSelect = new JButton(searchIcon);
		buttonCompilacaoSelect.setToolTipText(bundle.getString("hint-button-compilacao-select"));
		buttonCompilacaoSelect.addActionListener((event) -> selecionaCompilacao());
		buttonCompilacaoSelect.setBounds(435, 30, 30, 25);
		panelPreliminar.add(buttonCompilacaoSelect);
		
		// Painel 'Análise do Arquivo'
		panelResults = new JPanel();
		panelResults.setOpaque(false);
		panelResults.setVisible(false);
		panelResults.setLayout(null);
		panelResults.setBorder(instance.getTitledBorder(bundle.getString("final-panel-results")));
		panelResults.setBounds(12, 60, 453, 55);
		panelPreliminar.add(panelResults);
		
		JLabel labelDeferidos = new JLabel("Deferidos:");
		labelDeferidos.setHorizontalAlignment(JLabel.RIGHT);
		labelDeferidos.setFont(fonte);
		labelDeferidos.setBounds(15, 25, 80, 20);
		panelResults.add(labelDeferidos);
		
		textDeferidos = new JLabel();
		textDeferidos.setHorizontalAlignment(JLabel.CENTER);
		textDeferidos.setForeground(new Color(0x0D6B12));
		textDeferidos.setFont(fonte);
		textDeferidos.setBounds(100, 25, 45, 20);
		panelResults.add(textDeferidos);
		
		JLabel labelIndeferidos = new JLabel("Indeferidos:");
		labelIndeferidos.setHorizontalAlignment(JLabel.RIGHT);
		labelIndeferidos.setFont(fonte);
		labelIndeferidos.setBounds(175, 25, 90, 20);
		panelResults.add(labelIndeferidos);
		
		textIndeferidos = new JLabel();
		textIndeferidos.setHorizontalAlignment(JLabel.CENTER);
		textIndeferidos.setForeground(new Color(0xBC1742));
		textIndeferidos.setFont(fonte);
		textIndeferidos.setBounds(270, 25, 45, 20);
		panelResults.add(textIndeferidos);
		
		JLabel labelTotal = new JLabel("Total:");
		labelTotal.setHorizontalAlignment(JLabel.RIGHT);
		labelTotal.setFont(fonte);
		labelTotal.setBounds(350, 25, 40, 20);
		panelResults.add(labelTotal);
		
		textTotal = new JLabel();
		textTotal.setHorizontalAlignment(JLabel.CENTER);
		textTotal.setForeground(color);
		textTotal.setFont(fonte);
		textTotal.setBounds(395, 25, 45, 20);
		panelResults.add(textTotal);
		
		// Painel 'Arquivos de Entrada'
		JPanel panelInputFile = new JPanel();
		panelInputFile.setOpaque(false);
		panelInputFile.setLayout(null);
		panelInputFile.setBorder(instance.getTitledBorder(bundle.getString("final-panel-input-file")));
		panelInputFile.setBounds(12, 135, 476, 105);
		painel.add(panelInputFile);
		
		JLabel labelRetorno = new JLabel(bundle.getString("final-label-retorno"));
		labelRetorno.setHorizontalAlignment(JLabel.RIGHT);
		labelRetorno.setFont(fonte);
		labelRetorno.setBounds(10, 30, 110, 20);
		panelInputFile.add(labelRetorno);
		
		textRetorno = new JTextField();
		textRetorno.setToolTipText(bundle.getString("hint-text-retorno"));
		textRetorno.setForeground(color);
		textRetorno.setFont(fonte);
		textRetorno.setEditable(false);
		textRetorno.setBounds(125, 30, 257, 25);
		panelInputFile.add(textRetorno);
		
		JButton buttonRetornoSelect = new JButton(searchIcon);
		buttonRetornoSelect.setToolTipText(bundle.getString("hint-button-retorno-select"));
		buttonRetornoSelect.addActionListener((event) -> selecionaArquivoSistac());
		buttonRetornoSelect.setBounds(394, 30, 30, 25);
		panelInputFile.add(buttonRetornoSelect);
		
		JButton buttonReturnClear = new JButton(clearIcon);
		buttonReturnClear.setToolTipText(bundle.getString("hint-button-retorno-clear"));
		buttonReturnClear.setBounds(434, 30, 30, 25);
		panelInputFile.add(buttonReturnClear);
		
		JLabel labelErros = new JLabel(bundle.getString("final-label-erros"));
		labelErros.setHorizontalAlignment(JLabel.RIGHT);
		labelErros.setFont(fonte);
		labelErros.setBounds(10, 65, 110, 20);
		panelInputFile.add(labelErros);
		
		textErros = new JTextField();
		textErros.setToolTipText(bundle.getString("hint-text-erros"));
		textErros.setForeground(color);
		textErros.setFont(fonte);
		textErros.setEditable(false);
		textErros.setBounds(125, 65, 257, 25);
		panelInputFile.add(textErros);
		
		JButton buttonErrosSelect = new JButton(searchIcon);
		buttonErrosSelect.setToolTipText(bundle.getString("hint-button-erros-select"));
		buttonErrosSelect.addActionListener((event) -> selecionaArquivoExcel());
		buttonErrosSelect.setBounds(394, 65, 30, 25);
		panelInputFile.add(buttonErrosSelect);
		
		JButton buttonErrosClear = new JButton(clearIcon);
		buttonErrosClear.setToolTipText(bundle.getString("hint-button-erros-clear"));
		buttonErrosClear.setBounds(434, 65, 30, 25);
		panelInputFile.add(buttonErrosClear);
		
		// Painel 'Edital'
		JPanel panelEdital = new JPanel();
		panelEdital.setOpaque(false);
		panelEdital.setLayout(null);
		panelEdital.setBorder(instance.getTitledBorder(bundle.getString("final-panel-edital")));
		panelEdital.setBounds(12, 255, 476, 65);
		painel.add(panelEdital);
		
		JLabel labelCabecalho = new JLabel(bundle.getString("final-label-cabecalho"));
		labelCabecalho.setHorizontalAlignment(JLabel.RIGHT);
		labelCabecalho.setFont(fonte);
		labelCabecalho.setBounds(10, 30, 80, 20);
		panelEdital.add(labelCabecalho);
		
		textCabecalho = new JTextField();
		textCabecalho.setToolTipText(bundle.getString("hint-text-cabecalho"));
		textCabecalho.setForeground(color);
		textCabecalho.setFont(fonte);
		textCabecalho.setBounds(95, 30, 369, 25);
		panelEdital.add(textCabecalho);
		
		// Fundo da janela
		labelStatus = new JLabel(loadingIcon);
		labelStatus.setFont(fonte);
		labelStatus.setVisible(false);
		labelStatus.setBounds(12, 331, 214, 20);
		painel.add(labelStatus);
		
		JButton buttonSair = new JButton(exitIcon);
		buttonSair.setToolTipText(bundle.getString("hint-button-exit"));
		buttonSair.addActionListener((event) -> dispose());
		buttonSair.setBounds(406, 331, 35, 30);
		painel.add(buttonSair);
		
		JButton buttonExport = new JButton(reportIcon);
		buttonExport.setToolTipText(bundle.getString("hint-button-report"));
		buttonExport.setBounds(453, 331, 35, 30);
		painel.add(buttonExport);
		buttonExport.addActionListener((event) -> gerarVisualizacao());
		
		// Mostrando a janela
		setSize(dimension);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		
	}

	
	private void selecionaArquivoSistac() {
		
		try {
			
			verificaLista();
			
			retornoSistac = PhillFileUtils.loadFile("Selecione o arquivo de texto Sistac", Constants.FileFormat.SISTAC_RETV, PhillFileUtils.OPEN_DIALOG, null);
			textRetorno.setText(retornoSistac.getName());
			
			updateInfo(MSG_LOAD_FILE);
			executeJob(TXT_READ);
			
		}
		catch (BlankFieldException exception) { AlertDialog.error(exception.getMessage()); }
		catch (NullPointerException exception) { }
		catch (Exception exception) { AlertDialog.error("Não foi possível carregar o arquivo Sistac!"); }
	}
	
	private void selecionaArquivoExcel() {
		
		try {
			
			verificaLista();
			
			retornoExcel = PhillFileUtils.loadFile("Selecione a planilha", Constants.FileFormat.XLSX, PhillFileUtils.OPEN_DIALOG, null);
			textErros.setText(retornoExcel.getName());
			
			updateInfo(MSG_LOAD_FILE);
			executeJob(XLSX_READ);
			
		}
		catch (BlankFieldException exception) { AlertDialog.error(exception.getMessage()); }
		catch (NullPointerException exception) { }
		catch (Exception exception) { AlertDialog.error("Não foi possível carregar o arquivo Excel!"); }
	}
	
	private void selecionaCompilacao() {
		
		try {
			
			compilacao = PhillFileUtils.loadFile("Selecione o arquivo de compilação", Constants.FileFormat.BSF, PhillFileUtils.OPEN_DIALOG, null);
			textCompilacao.setText(compilacao.getName());
			
			panelResults.setVisible(true);
			
			updateInfo(MSG_LOAD_FILE);
			executeJob(BSF_READ);
			
		}
		catch (NullPointerException exception) { }
		catch (Exception exception) { AlertDialog.error("Não foi possível carregar a compilação!"); }
		
	}
	
	private void verificaLista() throws BlankFieldException {
		
		if (listaRetornos == null)
			throw new BlankFieldException("Selecione primeiro o arquivo de compilação!");
		
	}
	
	private void gerarVisualizacao() {
		
		try {
			
			dependenciaVisualizacao();
			
			updateInfo(MSG_LOAD_PDF);
			executeJob(PDF_EXPORT);
			
		}
		catch (BlankFieldException | FileNotSelectedException exception) {
			AlertDialog.error(exception.getMessage());
		}
		
	}
	
	private void dependenciaVisualizacao() throws BlankFieldException,FileNotSelectedException {
		
		if (listaRetornos == null)
			throw new FileNotSelectedException("Selecione ao menos um arquivo de entrada!");
		
		if (textCabecalho.getText().trim().equals(""))
			throw new BlankFieldException("Informe o cabeçalho do edital!");
		
		if (compilacao == null)
			throw new FileNotSelectedException("Selecione o arquivo de compilação.");
		
	}
	
	private void executeJob(int jobID) {
		new EventDispatcher(jobID).start();
	}
	
	private class EventDispatcher extends Thread {

		private final int function;
		
		public EventDispatcher(int function) {
			this.function = function;
		}
		
		@Override
		public void run() {
			
			try { dispatch(); }
			catch (Exception exception) {
				
				switch (function) {
				
					case BSF_READ:
						AlertDialog.error("Falha ao carregar a compilação!");
					break;
				
					case TXT_READ:
						AlertDialog.error("Falha ao carregar o arquivo de retorno do Sistac!");
					break;
					
					case XLSX_READ:
						AlertDialog.error("Falha ao carregar o arquivo de retorno do Excel!");
					break;
					
					case PDF_EXPORT:
						AlertDialog.error("Falha ao gerar visualização!");
					break;
				}
				
			}
			
		}
		
		private void dispatch() throws IOException, JRException, InterruptedException, ClassNotFoundException {
			
			switch (function) {
			
				case BSF_READ:
					
					listaRetornos = Compilation.retrieve(compilacao);
					updateCompilationAnalysis();
					
				break;
			
				case TXT_READ:
					
					SistacFile.readRetorno(listaRetornos, retornoSistac);
					updateCompilationAnalysis();
					
				break;
					
				case XLSX_READ:
					
					ExcelSheetReader.readRetorno(listaRetornos, retornoExcel);
					updateCompilationAnalysis();
					
				break;
					
				case PDF_EXPORT:
					
					String cabecalho  = textCabecalho.getText().trim();
					
					listaRetornos.sort();
					
					PDFExport.export(listaRetornos, cabecalho, Resultado.FINAL);
					
					resetInfo();
					
				break;
			}
			
		}
		
	}
	
	private void updateInfo(String message, boolean visibility) {
		Runnable job = new InfoUpdater(message, visibility);
		SwingUtilities.invokeLater(job);
	}
	
	private void updateCompilationAnalysis() {
		new CompilationAnalysis().start();
	}
	
	private void resetInfo() {
		updateInfo(null, false);
	}
	
	private void updateInfo(String message) {
		updateInfo(message, true);
	}
	
	private class InfoUpdater implements Runnable {

		private String message;
		private boolean visibility;
		
		public InfoUpdater(String message, boolean visibility) {
			this.message = message;
			this.visibility = visibility;
		}
		
		@Override
		public void run() {
			labelStatus.setText(message);
			labelStatus.setVisible(visibility);
			labelStatus.repaint();
		}
		
	}
	
	private class CompilationAnalysis extends Thread {
		
		@Override
		public void run() {
			
			Map<Boolean,List<Retorno>> map = listaRetornos.getList().stream().collect(Collectors.groupingBy(Retorno::deferido));
			
			List<Retorno>   deferidos = map.get(true );
			List<Retorno> indeferidos = map.get(false);
			
			int   deferidosCount = (  deferidos == null) ? 0 : deferidos  .size();
			int indeferidosCount = (indeferidos == null) ? 0 : indeferidos.size();
		
			try { sleep(1500L); }
			catch (InterruptedException exception) { }
			finally { resetInfo(); }
			
			Runnable job = new UpdateCompilationAnalysis(deferidosCount, indeferidosCount);
			SwingUtilities.invokeLater(job);
		}
		
	}
	
	private class UpdateCompilationAnalysis implements Runnable {

		private final int deferidosCount, indeferidosCount, total;
		
		public UpdateCompilationAnalysis(int deferidosCount, int indeferidosCount) {
			this.deferidosCount   = deferidosCount;
			this.indeferidosCount = indeferidosCount;
			this.total = (deferidosCount + indeferidosCount);
		}
		
		@Override
		public void run() {
			textDeferidos.setText(Integer.toString(deferidosCount));
			textIndeferidos.setText(Integer.toString(indeferidosCount));
			textTotal.setText(Integer.toString(total));
			
			resetInfo();
		}
		
	}
}
