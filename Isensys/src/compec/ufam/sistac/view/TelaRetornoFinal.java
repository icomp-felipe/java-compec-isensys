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
	private JTextField textRetornoSistac,textRetornoExcel,textCabecalho;
	private File retornoSistac,retornoExcel,compilacao;
	private ListaRetornos listaRetornos;
	private JLabel labelInfo;
	private JTextField textCompilacao;
	private JLabel textDeferidos;
	private JLabel textIndeferidos;
	private JLabel textTotal;
	private JPanel painelAnalise;

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
		
		JPanel panelCompilacao = new JPanel();
		panelCompilacao.setOpaque(false);
		panelCompilacao.setLayout(null);
		panelCompilacao.setBorder(instance.getTitledBorder("Compilação"));
		panelCompilacao.setBounds(12, 10, 476, 125);
		getContentPane().add(panelCompilacao);
		
		JLabel labelCompilacao = new JLabel("Compilação:");
		labelCompilacao.setHorizontalAlignment(SwingConstants.RIGHT);
		labelCompilacao.setFont(fonte);
		labelCompilacao.setBounds(10, 30, 90, 20);
		panelCompilacao.add(labelCompilacao);
		
		textCompilacao = new JTextField();
		textCompilacao.setForeground(color);
		textCompilacao.setFont(fonte);
		textCompilacao.setEditable(false);
		textCompilacao.setColumns(10);
		textCompilacao.setBounds(105, 30, 238, 25);
		panelCompilacao.add(textCompilacao);
		
		JButton botaoCompilacao = new JButton(searchIcon);
		botaoCompilacao.setToolTipText("Buscar o arquivo de compilação");
		botaoCompilacao.addActionListener((event) -> selecionaCompilacao());
		botaoCompilacao.setBounds(435, 30, 30, 25);
		panelCompilacao.add(botaoCompilacao);
		
		painelAnalise = new JPanel();
		painelAnalise.setOpaque(false);
		painelAnalise.setVisible(false);
		painelAnalise.setBorder(instance.getTitledBorder("Análise da Compilação"));
		painelAnalise.setBounds(12, 60, 453, 55);
		panelCompilacao.add(painelAnalise);
		painelAnalise.setLayout(null);
		
		JLabel labelDeferidos = new JLabel("Deferidos:");
		labelDeferidos.setHorizontalAlignment(SwingConstants.RIGHT);
		labelDeferidos.setFont(fonte);
		labelDeferidos.setBounds(15, 25, 80, 20);
		painelAnalise.add(labelDeferidos);
		
		textDeferidos = new JLabel("9999");
		textDeferidos.setHorizontalAlignment(SwingConstants.CENTER);
		textDeferidos.setForeground(gr_dk);
		textDeferidos.setFont(fonte);
		textDeferidos.setBounds(100, 25, 45, 20);
		painelAnalise.add(textDeferidos);
		
		JLabel labelIndeferidos = new JLabel("Indeferidos:");
		labelIndeferidos.setHorizontalAlignment(SwingConstants.RIGHT);
		labelIndeferidos.setFont(fonte);
		labelIndeferidos.setBounds(175, 25, 90, 20);
		painelAnalise.add(labelIndeferidos);
		
		textIndeferidos = new JLabel("9999");
		textIndeferidos.setHorizontalAlignment(SwingConstants.CENTER);
		textIndeferidos.setForeground(rd_dk);
		textIndeferidos.setFont(fonte);
		textIndeferidos.setBounds(270, 25, 45, 20);
		painelAnalise.add(textIndeferidos);
		
		JLabel labelTotal = new JLabel("Total:");
		labelTotal.setHorizontalAlignment(SwingConstants.RIGHT);
		labelTotal.setFont(fonte);
		labelTotal.setBounds(350, 25, 40, 20);
		painelAnalise.add(labelTotal);
		
		textTotal = new JLabel("9999");
		textTotal.setHorizontalAlignment(SwingConstants.CENTER);
		textTotal.setForeground(color);
		textTotal.setFont(fonte);
		textTotal.setBounds(395, 25, 45, 20);
		painelAnalise.add(textTotal);
		
		JButton botaoCompilacao_1 = new JButton(clearIcon);
		botaoCompilacao_1.setToolTipText("Buscar o arquivo de compilação");
		botaoCompilacao_1.setBounds(395, 30, 30, 25);
		panelCompilacao.add(botaoCompilacao_1);
		
		JButton botaoCompilacao_2 = new JButton(reloadIcon);
		botaoCompilacao_2.setToolTipText("Buscar o arquivo de compilação");
		botaoCompilacao_2.setBounds(355, 30, 30, 25);
		panelCompilacao.add(botaoCompilacao_2);
		
		JPanel painelEntrada = new JPanel();
		painelEntrada.setOpaque(false);
		painelEntrada.setLayout(null);
		painelEntrada.setBorder(instance.getTitledBorder("Arquivos de Entrada"));
		painelEntrada.setBounds(12, 135, 476, 105);
		getContentPane().add(painelEntrada);
		
		JLabel labelRetornoSistac = new JLabel("Retorno Sistac:");
		labelRetornoSistac.setHorizontalAlignment(SwingConstants.RIGHT);
		labelRetornoSistac.setFont(fonte);
		labelRetornoSistac.setBounds(10, 30, 110, 20);
		painelEntrada.add(labelRetornoSistac);
		
		textRetornoSistac = new JTextField();
		textRetornoSistac.setForeground(color);
		textRetornoSistac.setFont(fonte);
		textRetornoSistac.setEditable(false);
		textRetornoSistac.setColumns(10);
		textRetornoSistac.setBounds(125, 30, 257, 25);
		painelEntrada.add(textRetornoSistac);
		
		JButton botaoRetornoSistac = new JButton(searchIcon);
		botaoRetornoSistac.setToolTipText("Buscar o arquivo de retorno do Sistac");
		botaoRetornoSistac.addActionListener((event) -> selecionaArquivoSistac());
		botaoRetornoSistac.setBounds(394, 30, 30, 25);
		painelEntrada.add(botaoRetornoSistac);
		
		JLabel labelRetornoExcel = new JLabel("Planilha Erros:");
		labelRetornoExcel.setHorizontalAlignment(SwingConstants.RIGHT);
		labelRetornoExcel.setFont(fonte);
		labelRetornoExcel.setBounds(10, 65, 110, 20);
		painelEntrada.add(labelRetornoExcel);
		
		textRetornoExcel = new JTextField();
		textRetornoExcel.setForeground(color);
		textRetornoExcel.setFont(fonte);
		textRetornoExcel.setEditable(false);
		textRetornoExcel.setColumns(10);
		textRetornoExcel.setBounds(125, 65, 257, 25);
		painelEntrada.add(textRetornoExcel);
		
		JButton botaoRetornoExcel = new JButton(searchIcon);
		botaoRetornoExcel.setToolTipText("Buscar a planilha de erros");
		botaoRetornoExcel.addActionListener((event) -> selecionaArquivoExcel());
		botaoRetornoExcel.setBounds(394, 65, 30, 25);
		painelEntrada.add(botaoRetornoExcel);
		
		JButton botaoRetornoSistac_1 = new JButton(clearIcon);
		botaoRetornoSistac_1.setToolTipText("Buscar o arquivo de retorno do Sistac");
		botaoRetornoSistac_1.setBounds(434, 30, 30, 25);
		painelEntrada.add(botaoRetornoSistac_1);
		
		JButton botaoRetornoSistac_2 = new JButton(clearIcon);
		botaoRetornoSistac_2.setToolTipText("Buscar o arquivo de retorno do Sistac");
		botaoRetornoSistac_2.setBounds(434, 65, 30, 25);
		painelEntrada.add(botaoRetornoSistac_2);
		
		JPanel painelSaida = new JPanel();
		painelSaida.setOpaque(false);
		painelSaida.setLayout(null);
		painelSaida.setBorder(instance.getTitledBorder("Edital de Saída"));
		painelSaida.setBounds(12, 255, 476, 65);
		getContentPane().add(painelSaida);
		
		JLabel labelCabecalho = new JLabel("Cabeçalho:");
		labelCabecalho.setHorizontalAlignment(SwingConstants.RIGHT);
		labelCabecalho.setFont(fonte);
		labelCabecalho.setBounds(10, 30, 80, 20);
		painelSaida.add(labelCabecalho);
		
		textCabecalho = new JTextField();
		textCabecalho.setToolTipText("Título que fará parte do cabeçalho do edital");
		textCabecalho.setForeground(color);
		textCabecalho.setFont(fonte);
		textCabecalho.setColumns(10);
		textCabecalho.setBounds(95, 30, 369, 25);
		painelSaida.add(textCabecalho);
		
		JButton botaoSair = new JButton(exitIcon);
		botaoSair.setToolTipText("Sair do sistema");
		botaoSair.addActionListener((event) -> dispose());
		
		ImageIcon loading = new ImageIcon(ResourceManager.getResource("img/loader.gif"));
		
		labelInfo = new JLabel("Processando Arquivo",loading,SwingConstants.LEFT);
		labelInfo.setFont(fonte);
		labelInfo.setVisible(false);
		labelInfo.setBounds(12, 331, 214, 20);
		getContentPane().add(labelInfo);
		botaoSair.setBounds(406, 331, 35, 30);
		getContentPane().add(botaoSair);
		
		JButton botaoAbrir = new JButton(exportIcon);
		botaoAbrir.setToolTipText("Gerar o edital");
		botaoAbrir.setBounds(453, 331, 35, 30);
		getContentPane().add(botaoAbrir);
		botaoAbrir.addActionListener((event) -> gerarVisualizacao());
		
		setVisible(true);
		
	}

	
	private void selecionaArquivoSistac() {
		
		try {
			
			verificaLista();
			
			retornoSistac = PhillFileUtils.loadFile("Selecione o arquivo de texto Sistac", Constants.FileFormat.SISTAC_RETV, PhillFileUtils.OPEN_DIALOG, null);
			textRetornoSistac.setText(retornoSistac.getName());
			
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
			textRetornoExcel.setText(retornoExcel.getName());
			
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
			
			painelAnalise.setVisible(true);
			
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
			labelInfo.setText(message);
			labelInfo.setVisible(visibility);
			labelInfo.repaint();
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
