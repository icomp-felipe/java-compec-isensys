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

import compec.ufam.sistac.exception.*;
import compec.ufam.sistac.io.*;
import compec.ufam.sistac.pdf.*;
import compec.ufam.sistac.model.*;
import net.sf.jasperreports.engine.*;

/** Classe que controla a view de processamento Retorno Final
 *  @author Felipe André
 *  @version 2.50, 09/07/2018 */
public class TelaRetornoFinal extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private static final String MSG_LOAD_FILE = "Processando Arquivo";
	private static final String MSG_LOAD_PDF  = "Gerando Visualização";
	
	private static final int BSF_READ = 0, TXT_READ = 1, XLSX_READ = 2, PDF_EXPORT = 3;
	
	private final Color gr_dk = new Color(0x0d6b12);
	private final Color rd_dk = new Color(0xbc1742);
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
		super("IsenSys: Retorno (Final)");
		
		GraphicsHelper instance = GraphicsHelper.getInstance();
		GraphicsHelper.setFrameIcon(this,"icon/isensys-icon.png");
		
		// Recuperando ícones
		Icon clearIcon  = ResourceManager.getIcon("icon/clear.png" ,20,20);
		Icon reloadIcon = ResourceManager.getIcon("icon/reload.png",20,20);
		Icon searchIcon  = ResourceManager.getIcon("icon/search.png",20,20);
		Icon exitIcon    = ResourceManager.getIcon("icon/exit.png",25,25);
		Icon exportIcon  = ResourceManager.getIcon("icon/report.png",25,25);
		
		Font  fonte = instance.getFont ();
		Color color = instance.getColor();
		Dimension d = new Dimension(500,400);
		
		JPanel painel = new JPaintedPanel("img/final-screen.jpg",d);
		
		setContentPane(painel);
		setSize(d);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		JPanel panelPreliminar = new JPanel();
		panelPreliminar.setOpaque(false);
		panelPreliminar.setLayout(null);
		panelPreliminar.setBorder(instance.getTitledBorder("Compilação"));
		panelPreliminar.setBounds(12, 10, 476, 125);
		getContentPane().add(panelPreliminar);
		
		JLabel labelCompilacao = new JLabel("Compilação:");
		labelCompilacao.setHorizontalAlignment(SwingConstants.RIGHT);
		labelCompilacao.setFont(fonte);
		labelCompilacao.setBounds(10, 30, 90, 20);
		panelPreliminar.add(labelCompilacao);
		
		textCompilacao = new JTextField();
		textCompilacao.setForeground(color);
		textCompilacao.setFont(fonte);
		textCompilacao.setEditable(false);
		textCompilacao.setColumns(10);
		textCompilacao.setBounds(105, 30, 238, 25);
		panelPreliminar.add(textCompilacao);
		
		JButton buttonCompilacaoSelect = new JButton(searchIcon);
		buttonCompilacaoSelect.setToolTipText("Buscar o arquivo de compilação");
		buttonCompilacaoSelect.addActionListener((event) -> selecionaCompilacao());
		
		JButton buttonCompilacaoReload = new JButton(reloadIcon);
		buttonCompilacaoReload.setToolTipText("Buscar o arquivo de compilação");
		buttonCompilacaoReload.setBounds(355, 30, 30, 25);
		panelPreliminar.add(buttonCompilacaoReload);
		
		JButton buttonCompilacaoClear = new JButton(clearIcon);
		buttonCompilacaoClear.setToolTipText("Buscar o arquivo de compilação");
		buttonCompilacaoClear.setBounds(395, 30, 30, 25);
		panelPreliminar.add(buttonCompilacaoClear);
		buttonCompilacaoSelect.setBounds(435, 30, 30, 25);
		panelPreliminar.add(buttonCompilacaoSelect);
		
		panelResults = new JPanel();
		panelResults.setOpaque(false);
		panelResults.setVisible(false);
		panelResults.setBorder(instance.getTitledBorder("Análise da Compilação"));
		panelResults.setBounds(12, 60, 453, 55);
		panelPreliminar.add(panelResults);
		panelResults.setLayout(null);
		
		JLabel labelDeferidos = new JLabel("Deferidos:");
		labelDeferidos.setHorizontalAlignment(SwingConstants.RIGHT);
		labelDeferidos.setFont(fonte);
		labelDeferidos.setBounds(15, 25, 80, 20);
		panelResults.add(labelDeferidos);
		
		textDeferidos = new JLabel("9999");
		textDeferidos.setHorizontalAlignment(SwingConstants.CENTER);
		textDeferidos.setForeground(gr_dk);
		textDeferidos.setFont(fonte);
		textDeferidos.setBounds(100, 25, 45, 20);
		panelResults.add(textDeferidos);
		
		JLabel labelIndeferidos = new JLabel("Indeferidos:");
		labelIndeferidos.setHorizontalAlignment(SwingConstants.RIGHT);
		labelIndeferidos.setFont(fonte);
		labelIndeferidos.setBounds(175, 25, 90, 20);
		panelResults.add(labelIndeferidos);
		
		textIndeferidos = new JLabel("9999");
		textIndeferidos.setHorizontalAlignment(SwingConstants.CENTER);
		textIndeferidos.setForeground(rd_dk);
		textIndeferidos.setFont(fonte);
		textIndeferidos.setBounds(270, 25, 45, 20);
		panelResults.add(textIndeferidos);
		
		JLabel labelTotal = new JLabel("Total:");
		labelTotal.setHorizontalAlignment(SwingConstants.RIGHT);
		labelTotal.setFont(fonte);
		labelTotal.setBounds(350, 25, 40, 20);
		panelResults.add(labelTotal);
		
		textTotal = new JLabel("9999");
		textTotal.setHorizontalAlignment(SwingConstants.CENTER);
		textTotal.setForeground(color);
		textTotal.setFont(fonte);
		textTotal.setBounds(395, 25, 45, 20);
		panelResults.add(textTotal);
		
		JPanel panelInputFile = new JPanel();
		panelInputFile.setOpaque(false);
		panelInputFile.setLayout(null);
		panelInputFile.setBorder(instance.getTitledBorder("Arquivos de Entrada"));
		panelInputFile.setBounds(12, 135, 476, 105);
		getContentPane().add(panelInputFile);
		
		JLabel labelRetorno = new JLabel("Retorno Sistac:");
		labelRetorno.setHorizontalAlignment(SwingConstants.RIGHT);
		labelRetorno.setFont(fonte);
		labelRetorno.setBounds(10, 30, 110, 20);
		panelInputFile.add(labelRetorno);
		
		textRetorno = new JTextField();
		textRetorno.setForeground(color);
		textRetorno.setFont(fonte);
		textRetorno.setEditable(false);
		textRetorno.setColumns(10);
		textRetorno.setBounds(125, 30, 257, 25);
		panelInputFile.add(textRetorno);
		
		JButton buttonRetornoSelect = new JButton(searchIcon);
		buttonRetornoSelect.setToolTipText("Buscar o arquivo de retorno do Sistac");
		buttonRetornoSelect.addActionListener((event) -> selecionaArquivoSistac());
		buttonRetornoSelect.setBounds(394, 30, 30, 25);
		panelInputFile.add(buttonRetornoSelect);
		
		JButton buttonReturnClear = new JButton(clearIcon);
		buttonReturnClear.setToolTipText("Buscar o arquivo de retorno do Sistac");
		buttonReturnClear.setBounds(434, 30, 30, 25);
		panelInputFile.add(buttonReturnClear);
		
		JLabel labelErros = new JLabel("Planilha Erros:");
		labelErros.setHorizontalAlignment(SwingConstants.RIGHT);
		labelErros.setFont(fonte);
		labelErros.setBounds(10, 65, 110, 20);
		panelInputFile.add(labelErros);
		
		textErros = new JTextField();
		textErros.setForeground(color);
		textErros.setFont(fonte);
		textErros.setEditable(false);
		textErros.setColumns(10);
		textErros.setBounds(125, 65, 257, 25);
		panelInputFile.add(textErros);
		
		JButton buttonErrosSelect = new JButton(searchIcon);
		buttonErrosSelect.setToolTipText("Buscar a planilha de erros");
		buttonErrosSelect.addActionListener((event) -> selecionaArquivoExcel());
		buttonErrosSelect.setBounds(394, 65, 30, 25);
		panelInputFile.add(buttonErrosSelect);
		
		JButton buttonErrosClear = new JButton(clearIcon);
		buttonErrosClear.setToolTipText("Buscar o arquivo de retorno do Sistac");
		buttonErrosClear.setBounds(434, 65, 30, 25);
		panelInputFile.add(buttonErrosClear);
		
		JPanel panelSaida = new JPanel();
		panelSaida.setOpaque(false);
		panelSaida.setLayout(null);
		panelSaida.setBorder(instance.getTitledBorder("Edital de Saída"));
		panelSaida.setBounds(12, 255, 476, 65);
		getContentPane().add(panelSaida);
		
		JLabel labelCabecalho = new JLabel("Cabeçalho:");
		labelCabecalho.setHorizontalAlignment(SwingConstants.RIGHT);
		labelCabecalho.setFont(fonte);
		labelCabecalho.setBounds(10, 30, 80, 20);
		panelSaida.add(labelCabecalho);
		
		textCabecalho = new JTextField();
		textCabecalho.setToolTipText("Título que fará parte do cabeçalho do edital");
		textCabecalho.setForeground(color);
		textCabecalho.setFont(fonte);
		textCabecalho.setColumns(10);
		textCabecalho.setBounds(95, 30, 369, 25);
		panelSaida.add(textCabecalho);
		
		JButton buttonSair = new JButton(exitIcon);
		buttonSair.setToolTipText("Sair do sistema");
		buttonSair.addActionListener((event) -> dispose());
		
		ImageIcon loading = new ImageIcon(ResourceManager.getResource("img/loader.gif"));
		
		labelStatus = new JLabel("Processando Arquivo",loading,SwingConstants.LEFT);
		labelStatus.setFont(fonte);
		labelStatus.setVisible(false);
		labelStatus.setBounds(12, 331, 214, 20);
		getContentPane().add(labelStatus);
		buttonSair.setBounds(406, 331, 35, 30);
		getContentPane().add(buttonSair);
		
		JButton buttonExport = new JButton(exportIcon);
		buttonExport.setToolTipText("Gerar o edital");
		buttonExport.setBounds(453, 331, 35, 30);
		getContentPane().add(buttonExport);
		buttonExport.addActionListener((event) -> gerarVisualizacao());
		
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
