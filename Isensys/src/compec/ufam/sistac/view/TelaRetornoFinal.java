package compec.ufam.sistac.view;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.awt.*;
import javax.swing.*;
import com.phill.libs.*;
import com.phill.libs.ui.*;
import com.phill.libs.exception.*;
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
		
		Icon searchIcon  = ResourceManager.getResizedIcon("icon/search.png",20,20);
		Icon exitIcon    = ResourceManager.getResizedIcon("icon/exit.png",25,25);
		Icon exportIcon  = ResourceManager.getResizedIcon("icon/report.png",25,25);
		
		Font  fonte = instance.getFont ();
		Color color = instance.getColor();
		Dimension d = new Dimension(460,400);
		
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
		panelCompilacao.setBounds(12, 12, 436, 124);
		getContentPane().add(panelCompilacao);
		
		JLabel labelCompilacao = new JLabel("Arquivo BSF:");
		labelCompilacao.setFont(fonte);
		labelCompilacao.setBounds(12, 26, 118, 20);
		panelCompilacao.add(labelCompilacao);
		
		textCompilacao = new JTextField();
		textCompilacao.setForeground(color);
		textCompilacao.setFont(fonte);
		textCompilacao.setEditable(false);
		textCompilacao.setColumns(10);
		textCompilacao.setBounds(130, 25, 252, 25);
		panelCompilacao.add(textCompilacao);
		
		JButton botaoCompilacao = new JButton(searchIcon);
		botaoCompilacao.setToolTipText("Buscar o arquivo de compilação");
		botaoCompilacao.addActionListener((event) -> selecionaCompilacao());
		botaoCompilacao.setBounds(394, 25, 30, 25);
		panelCompilacao.add(botaoCompilacao);
		
		painelAnalise = new JPanel();
		painelAnalise.setOpaque(false);
		painelAnalise.setVisible(false);
		painelAnalise.setBorder(instance.getTitledBorder("Análise da Compilação"));
		painelAnalise.setBounds(12, 57, 412, 55);
		panelCompilacao.add(painelAnalise);
		painelAnalise.setLayout(null);
		
		JLabel labelDeferidos = new JLabel("Deferidos:");
		labelDeferidos.setFont(fonte);
		labelDeferidos.setBounds(12, 28, 81, 15);
		painelAnalise.add(labelDeferidos);
		
		textDeferidos = new JLabel("0");
		textDeferidos.setForeground(gr_dk);
		textDeferidos.setFont(fonte);
		textDeferidos.setBounds(92, 29, 46, 15);
		painelAnalise.add(textDeferidos);
		
		JLabel labelIndeferidos = new JLabel("Indeferidos:");
		labelIndeferidos.setFont(fonte);
		labelIndeferidos.setBounds(150, 28, 96, 15);
		painelAnalise.add(labelIndeferidos);
		
		textIndeferidos = new JLabel("0");
		textIndeferidos.setForeground(rd_dk);
		textIndeferidos.setFont(fonte);
		textIndeferidos.setBounds(244, 28, 46, 15);
		painelAnalise.add(textIndeferidos);
		
		JLabel labelTotal = new JLabel("Total:");
		labelTotal.setFont(fonte);
		labelTotal.setBounds(302, 28, 46, 15);
		painelAnalise.add(labelTotal);
		
		textTotal = new JLabel("0");
		textTotal.setForeground(color);
		textTotal.setFont(fonte);
		textTotal.setBounds(354, 28, 46, 15);
		painelAnalise.add(textTotal);
		
		JPanel painelEntrada = new JPanel();
		painelEntrada.setOpaque(false);
		painelEntrada.setLayout(null);
		painelEntrada.setBorder(instance.getTitledBorder("Arquivos de Entrada"));
		painelEntrada.setBounds(12, 148, 436, 95);
		getContentPane().add(painelEntrada);
		
		JLabel labelRetornoSistac = new JLabel("Retorno Sistac:");
		labelRetornoSistac.setFont(fonte);
		labelRetornoSistac.setBounds(12, 30, 118, 15);
		painelEntrada.add(labelRetornoSistac);
		
		textRetornoSistac = new JTextField();
		textRetornoSistac.setForeground(color);
		textRetornoSistac.setFont(fonte);
		textRetornoSistac.setEditable(false);
		textRetornoSistac.setColumns(10);
		textRetornoSistac.setBounds(130, 29, 252, 25);
		painelEntrada.add(textRetornoSistac);
		
		JButton botaoRetornoSistac = new JButton(searchIcon);
		botaoRetornoSistac.setToolTipText("Buscar o arquivo de retorno do Sistac");
		botaoRetornoSistac.addActionListener((event) -> selecionaArquivoSistac());
		botaoRetornoSistac.setBounds(394, 28, 30, 25);
		painelEntrada.add(botaoRetornoSistac);
		
		JLabel labelRetornoExcel = new JLabel("Planilha Erros:");
		labelRetornoExcel.setFont(fonte);
		labelRetornoExcel.setBounds(12, 59, 118, 15);
		painelEntrada.add(labelRetornoExcel);
		
		textRetornoExcel = new JTextField();
		textRetornoExcel.setForeground(color);
		textRetornoExcel.setFont(fonte);
		textRetornoExcel.setEditable(false);
		textRetornoExcel.setColumns(10);
		textRetornoExcel.setBounds(130, 58, 252, 25);
		painelEntrada.add(textRetornoExcel);
		
		JButton botaoRetornoExcel = new JButton(searchIcon);
		botaoRetornoExcel.setToolTipText("Buscar a planilha de erros");
		botaoRetornoExcel.addActionListener((event) -> selecionaArquivoExcel());
		botaoRetornoExcel.setBounds(394, 57, 30, 25);
		painelEntrada.add(botaoRetornoExcel);
		
		JPanel painelSaida = new JPanel();
		painelSaida.setOpaque(false);
		painelSaida.setLayout(null);
		painelSaida.setBorder(instance.getTitledBorder("Edital de Saída"));
		painelSaida.setBounds(12, 255, 436, 64);
		getContentPane().add(painelSaida);
		
		JLabel labelCabecalho = new JLabel("Cabeçalho:");
		labelCabecalho.setFont(fonte);
		labelCabecalho.setBounds(12, 28, 88, 20);
		painelSaida.add(labelCabecalho);
		
		textCabecalho = new JTextField();
		textCabecalho.setToolTipText("Título que fará parte do cabeçalho do edital");
		textCabecalho.setForeground(color);
		textCabecalho.setFont(fonte);
		textCabecalho.setColumns(10);
		textCabecalho.setBounds(105, 29, 319, 25);
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
		botaoSair.setBounds(366, 331, 35, 30);
		getContentPane().add(botaoSair);
		
		JButton botaoAbrir = new JButton(exportIcon);
		botaoAbrir.setToolTipText("Gerar o edital");
		botaoAbrir.setBounds(413, 331, 35, 30);
		getContentPane().add(botaoAbrir);
		botaoAbrir.addActionListener((event) -> gerarVisualizacao());
		
		setVisible(true);
		
	}

	
	private void selecionaArquivoSistac() {
		
		try {
			
			verificaLista();
			
			retornoSistac = FileChooserHelper.loadFile(this, FileFilters.SISTAC_RETV, "Selecione o arquivo de texto Sistac", false, null);
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
			
			retornoExcel = FileChooserHelper.loadFile(this, FileFilters.XLSX, "Selecione a planilha", false, null);
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
			
			compilacao = FileChooserHelper.loadFile(this, FileFilters.BSF, "Selecione o arquivo de compilação", false, null);
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
