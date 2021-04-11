package compec.ufam.sistac.view;

import java.io.*;
import java.awt.*;
import javax.swing.*;

import com.phill.libs.*;
import com.phill.libs.ui.*;
import com.phill.libs.i18n.*;
import com.phill.libs.files.*;

import compec.ufam.sistac.io.*;
import compec.ufam.sistac.model.*;
import compec.ufam.sistac.exception.*;

public class TelaEnvio extends JFrame {

	private static final long serialVersionUID = -1766759262038217449L;
	
	// Carregando bundle de idiomas
	private final static PropertyBundle bundle = new PropertyBundle("i18n/titles", null);
	
	

	public static final int INDEXES[] = new int[]{1,2,3,4,5,6,7,8,9};
	
	private JTextField textInputName;
	private JTextField textSaidaSistac;

	private final Color gr_dk = new Color(0x0d6b12);
	private final Color rd_dk = new Color(0xbc1742);
	
	private static final boolean PANEL_LOADING = true;
	private static final boolean PANEL_RESULTS = false;
	
	private JButton buttonInputSelect;
	private JButton botaoSaidaSistac;
	
	private JLabel labelInputStatus;
	private JPanel painelSituacoes;
	
	private JLabel textOK,textErro,textTotal;
	
	private ParseResult listaResultados;
	private JButton botaoSair;
	private JButton botaoExportar;
	private JTextField textEdital;
	private JTextField textSequencia;
	
	private File arquivoEntrada, dirSaida;
	
	private JButton buttonInputRefresh;
	private JButton buttonInputClear;

	private ImageIcon loading = new ImageIcon(ResourceManager.getResource("img/loader.gif"));
	
	public static void main(String[] args) {
		new TelaEnvio();
	}

	public TelaEnvio() {
		//super(bundle.getString("envio-window-title"));
		
		GraphicsHelper instance = GraphicsHelper.getInstance();
		GraphicsHelper.setFrameIcon(this,"icon/isensys-icon.png");
		
		Font  fonte = instance.getFont ();
		Color color = instance.getColor();
		Dimension dimension = new Dimension(500,340);
		
		JPanel painel = new JPaintedPanel("img/envio-screen.jpg",dimension);
		setContentPane(painel);
		
		setSize(dimension);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		painel.setLayout(null);
		
		Icon searchIcon  = ResourceManager.getIcon("icon/search.png",20,20);
		Icon reloadIcon  = ResourceManager.getIcon("icon/reload.png",20,20);
		Icon exitIcon    = ResourceManager.getIcon("icon/exit.png",25,25);
		Icon exportIcon  = ResourceManager.getIcon("icon/save.png",25,25);
		Icon clearIcon  = ResourceManager.getIcon("icon/clear.png",20,20);
		
		JPanel painelEntrada = new JPanel();
		painelEntrada.setOpaque(false);
		painelEntrada.setBorder(instance.getTitledBorder("Arquivo de Entrada"));
		painelEntrada.setBounds(12, 10, 476, 105);
		painel.add(painelEntrada);
		painelEntrada.setLayout(null);
		
		JLabel labelInputName = new JLabel("Nome:");
		labelInputName.setFont(fonte);
		labelInputName.setBounds(10, 30, 50, 20);
		painelEntrada.add(labelInputName);
		
		textInputName = new JTextField();
		textInputName.setEditable(false);
		textInputName.setForeground(color);
		textInputName.setFont(fonte);
		textInputName.setBounds(65, 30, 280, 25);
		painelEntrada.add(textInputName);
		textInputName.setColumns(10);
		
		buttonInputSelect = new JButton(searchIcon);
		buttonInputSelect.setToolTipText("Busca o arquivo de entrada");
		buttonInputSelect.addActionListener((event) -> carregaArquivoEntrada());
		
		buttonInputRefresh = new JButton(reloadIcon);
		buttonInputRefresh.addActionListener((event) -> functionReload());
		buttonInputRefresh.setToolTipText("Recarrega o arquivo atual");
		buttonInputRefresh.setBounds(355, 30, 30, 25);
		painelEntrada.add(buttonInputRefresh);
		
		buttonInputClear = new JButton(clearIcon);
		buttonInputClear.addActionListener((event) -> functionInputClear());
		buttonInputClear.setToolTipText("Busca o arquivo de entrada");
		buttonInputClear.setBounds(395, 30, 30, 25);
		painelEntrada.add(buttonInputClear);
		buttonInputSelect.setBounds(435, 30, 30, 25);
		painelEntrada.add(buttonInputSelect);
		
		labelInputStatus = new JLabel();
		labelInputStatus.setFont(fonte);
		labelInputStatus.setBounds(10, 70, 120, 20);
		painelEntrada.add(labelInputStatus);
		
		textOK = new JLabel();
		textOK.setBounds(150, 70, 80, 20);
		painelEntrada.add(textOK);
		textOK.setForeground(gr_dk);
		textOK.setFont(fonte);
		
		textErro = new JLabel();
		textErro.setBounds(240, 70, 100, 20);
		painelEntrada.add(textErro);
		textErro.setForeground(rd_dk);
		textErro.setFont(fonte);
		
		textTotal = new JLabel();
		textTotal.setBounds(350, 70, 105, 20);
		painelEntrada.add(textTotal);
		textTotal.setForeground(color);
		textTotal.setFont(fonte);
		
		JPanel painelSaida = new JPanel();
		painelSaida.setOpaque(false);
		painelSaida.setBorder(instance.getTitledBorder("Arquivos de Saída"));
		painelSaida.setBounds(12, 115, 476, 130);
		painel.add(painelSaida);
		painelSaida.setLayout(null);
		
		JLabel labelEdital = new JLabel("Num do Edital:");
		labelEdital.setFont(fonte);
		labelEdital.setBounds(12, 30, 110, 15);
		painelSaida.add(labelEdital);
		
		textEdital = new JTextField();
		textEdital.setForeground(color);
		textEdital.setFont(fonte);
		textEdital.setColumns(10);
		textEdital.setBounds(130, 28, 100, 20);
		painelSaida.add(textEdital);
		
		JLabel labelSequencia = new JLabel("Sequência:");
		labelSequencia.setFont(fonte);
		labelSequencia.setBounds(241, 27, 90, 20);
		painelSaida.add(labelSequencia);
		
		textSequencia = new JTextField();
		textSequencia.setForeground(color);
		textSequencia.setFont(fonte);
		textSequencia.setColumns(10);
		textSequencia.setBounds(334, 28, 90, 20);
		painelSaida.add(textSequencia);
		
		JLabel labelSaidaSistac = new JLabel("Pasta de Saída:");
		labelSaidaSistac.setFont(fonte);
		labelSaidaSistac.setBounds(12, 62, 118, 15);
		painelSaida.add(labelSaidaSistac);
		
		textSaidaSistac = new JTextField();
		textSaidaSistac.setFont(fonte);
		textSaidaSistac.setForeground(color);
		textSaidaSistac.setEditable(false);
		textSaidaSistac.setColumns(10);
		textSaidaSistac.setBounds(130, 60, 252, 25);
		painelSaida.add(textSaidaSistac);
		
		botaoSaidaSistac = new JButton(searchIcon);
		botaoSaidaSistac.setToolTipText("Escolher aonde será salvo o arquivo de importação para o Sistac");
		botaoSaidaSistac.addActionListener((event) -> selecionaSaidaSistac());
		botaoSaidaSistac.setBounds(394, 60, 30, 25);
		painelSaida.add(botaoSaidaSistac);
		
		botaoExportar = new JButton(exportIcon);
		botaoExportar.setToolTipText("Exporta o(s) arquivo(s)");
		botaoExportar.addActionListener((event) -> exportarArquivos());
		botaoExportar.setBounds(410, 266, 35, 30);
		painel.add(botaoExportar);
		
		botaoSair = new JButton(exitIcon);
		botaoSair.setToolTipText("Sai do sistema");
		botaoSair.addActionListener((event) -> dispose());
		botaoSair.setBounds(363, 266, 35, 30);
		painel.add(botaoSair);
		
		painelSituacoes = new JPanel();
		painelSituacoes.setBounds(22, 255, 416, 41);
		painel.add(painelSituacoes);
		painelSituacoes.setOpaque(false);
		painelSituacoes.setVisible(false);
		painelSituacoes.setLayout(null);
		
		JLabel labelSituacoes = new JLabel("Solicitações:");
		labelSituacoes.setFont(fonte);
		labelSituacoes.setBounds(0, 12, 102, 15);
		painelSituacoes.add(labelSituacoes);
		
		setVisible(true);
		
	}

	/** Carrega o arquivo de entrada de dados e atualiza as informações da janela */
	private void carregaArquivoEntrada() {
		
		// Recuperando o arquivo de entrada
		this.arquivoEntrada  = PhillFileUtils.loadFile("Selecione o arquivo de entrada", Constants.FileFormat.SISTAC_INPUT, PhillFileUtils.OPEN_DIALOG, null);
		
		// Só prossigo se algum arquivo foi selecionado
		if (this.arquivoEntrada != null) {
			
			// Atualizando a view
			textInputName.setText(arquivoEntrada.getName());
			
			// Lendo o CSV
			csv_loader();
			
		}
		
	}
	
	private void functionReload() {
		
		if (this.arquivoEntrada != null)
			csv_loader();
		
	}
	
	/** Recarrega o arquivo de entrada */
	private void csv_loader() {
		
		// Atualizando a view
		labelInputStatus.setIcon(loading);
		labelInputStatus.setText("Processando");
		labelInputStatus.setVisible(true);
		
		textOK   .setVisible(false);
		textErro .setVisible(false);
		textTotal.setVisible(false);
		
		buttonInputClear  .setEnabled(false);
		buttonInputRefresh.setEnabled(false);
		buttonInputSelect .setEnabled(false);
		
		// Carregando o arquivo
		Thread thread_loader = new Thread(() -> thread_csv_loader());
							
		thread_loader.setName("TelaEnvio.class - Thread do leitor de CSV");
		thread_loader.start();
		
	}
	
	private void functionInputClear() {
		
		// If a playlist was previously downloaded, a clear dialog is shown
					
		String title   = bundle.getString("envio-input-clear-title");
		String message = bundle.getString("envio-input-clear-dialog");
					
		int choice     = AlertDialog.dialog(title, message);
					
		// Breaks here when EXIT or CANCEL is selected
		if (choice != AlertDialog.OK_OPTION)
			return;
		
		this.arquivoEntrada  = null;
		this.listaResultados = null;
		
		textInputName.setText(null);
		labelInputStatus.setVisible(false);
		
		textOK   .setVisible(false);
		textErro .setVisible(false);
		textTotal.setVisible(false);
		
	}
	
	/** Carrega os dados de entrada para o sistema */
	private void thread_csv_loader() {
		
		try {
			
			// Seleciona o tipo de leitor de acordo com a extensão do arquivo de entrada
			if (arquivoEntrada.getName().endsWith("xlsx"))
				listaResultados = ExcelSheetReader.read(arquivoEntrada, INDEXES);
			else
				listaResultados = CSVSheetReader.read(arquivoEntrada, INDEXES);
		
			Thread.sleep(2000);
			
			Runnable job = () -> updateStatistics();
			SwingUtilities.invokeLater(job);
			
		}
		catch (Exception exception) {
			exception.printStackTrace();
			AlertDialog.error("Falha ao processar arquivo!\nVerifique se ele está no formato correto.");
			textInputName.setText (null );
			labelInputStatus.setVisible(false);
		}
		
	}
	
	/** Atualiza os totais de candidatos processados */
	private void updateStatistics() {
		
		int sizeERR = listaResultados.getListaExcecoes  ().size();
		int sizeOK  = listaResultados.getListaCandidatos().size();
		int sizeALL = (sizeOK + sizeERR);
		
		buttonInputClear  .setEnabled(true);
		buttonInputRefresh.setEnabled(true);
		buttonInputSelect .setEnabled(true);
		
		labelInputStatus.setIcon(null);
		labelInputStatus.setText("Solicitações:");
		
		textOK   .setVisible(true);
		textErro .setVisible(true);
		textTotal.setVisible(true);
		
		textOK   .setText(sizeOK  + " (OK)"   );
		textErro .setText(sizeERR + " (ERRO)" );
		textTotal.setText(sizeALL + " (TOTAL)");
		
	}
	
	/** Seleciona o diretório de saída dos arquivos gerados */
	private void selecionaSaidaSistac() {
		
		dirSaida = PhillFileUtils.loadDir("Selecione o arquivo de saída", PhillFileUtils.OPEN_DIALOG, null);
			
		if (dirSaida != null)
			textSaidaSistac.setText(dirSaida.getAbsolutePath());
		
	}
	
	/** Verifica se as dependências para a montagem do nome do arquivo sistac estão satisfeitas */
	private void dependenciaSistac() throws BlankFieldException {
		
		if (textEdital.getText().trim().isEmpty())
			throw new BlankFieldException("Informe o Edital!");
		
		if (textSequencia.getText().trim().isEmpty())
			throw new BlankFieldException("Informe a Sequência!");
		
		if (dirSaida == null)
			throw new BlankFieldException("Selecione a pasta de saída");
		
	}
	
	/** Exporta os arquivos de envio do sistac e/ou a planilha de erros no formato excel.
	 *  Obs.: a exportação da planilha é opcional quando não há erros de processamento */
	private void exportarArquivos() {
		
		try {
			
			dependenciaSistac();
			dependenciaExportacao();
			
			String edital = textEdital.getText().trim();
			String sequencia = textSequencia.getText().trim();
			
			File saidaSistac = getSistacFile(edital, sequencia);
			File saidaExcel  = getExcelFile (edital, sequencia);
			
			ExcelSheetWriter.write(listaResultados.getListaExcecoes(), saidaExcel);
			SistacFile.generate(listaResultados.getListaCandidatos(), saidaSistac);
			
			AlertDialog.info("Arquivo(s) exportado(s) com sucesso!");
			
		} catch (Exception exception) {
			AlertDialog.error(exception.getMessage());
		}
		
	}
	
	private File getSistacFile(final String edital, final String sequencia) {
		
		return SistacFile.getSistacExportName(this.arquivoEntrada, edital, sequencia);
	}
	
	private File getExcelFile(final String edital, final String sequencia) {
		
		String filename = String.format("errors-%s-%s.xlsx", edital, sequencia);
		
		return new File(this.arquivoEntrada, filename);
	}
	
	/** Verifica se todas os requisitos para a exportação foram devidamente satisfeitos */
	private void dependenciaExportacao() throws FileNotSelectedException {
		
		if (listaResultados == null)
			throw new FileNotSelectedException("Selecione o arquivo de entrada de dados!");
		
	}
}
