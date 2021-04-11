package compec.ufam.sistac.view;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.DefaultFormatter;

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

	private JButton buttonInputSelect;
	private JButton botaoSaidaSistac;
	
	private JLabel labelInputStatus;
	
	private JLabel textOK,textErro,textTotal;
	
	private ParseResult listaResultados;
	private JButton botaoSair;
	private JButton botaoExportar;
	private JTextField textEdital;
	
	private File arquivoEntrada, dirSaida;
	
	private JButton buttonInputRefresh;
	private JButton buttonInputClear;

	private ImageIcon loading = new ImageIcon(ResourceManager.getResource("img/loader.gif"));
	private JSpinner spinnerSequencia;
	
	public static void main(String[] args) {
		new TelaEnvio();
	}

	public TelaEnvio() {
		//super(bundle.getString("envio-window-title"));
		
		GraphicsHelper instance = GraphicsHelper.getInstance();
		GraphicsHelper.setFrameIcon(this,"icon/isensys-icon.png");
		
		Font  fonte = instance.getFont ();
		Color color = instance.getColor();
		Dimension dimension = new Dimension(500,300);
		
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
		textOK.setHorizontalAlignment(SwingConstants.CENTER);
		textOK.setBounds(150, 70, 80, 20);
		painelEntrada.add(textOK);
		textOK.setForeground(new Color(0x0D6B12));
		textOK.setFont(fonte);
		
		textErro = new JLabel();
		textErro.setHorizontalAlignment(SwingConstants.CENTER);
		textErro.setBounds(240, 70, 100, 20);
		painelEntrada.add(textErro);
		textErro.setForeground(new Color(0xBC1742));
		textErro.setFont(fonte);
		
		textTotal = new JLabel();
		textTotal.setHorizontalAlignment(SwingConstants.CENTER);
		textTotal.setBounds(350, 70, 105, 20);
		painelEntrada.add(textTotal);
		textTotal.setForeground(color);
		textTotal.setFont(fonte);
		
		JPanel painelSaida = new JPanel();
		painelSaida.setOpaque(false);
		painelSaida.setBorder(instance.getTitledBorder("Arquivos de Saída"));
		painelSaida.setBounds(12, 115, 476, 105);
		painel.add(painelSaida);
		painelSaida.setLayout(null);
		
		JLabel labelEdital = new JLabel("Num do Edital:");
		labelEdital.setHorizontalAlignment(SwingConstants.RIGHT);
		labelEdital.setFont(fonte);
		labelEdital.setBounds(10, 30, 110, 20);
		painelSaida.add(labelEdital);
		
		textEdital = new JTextField();
		textEdital.setForeground(color);
		textEdital.setFont(fonte);
		textEdital.setColumns(10);
		textEdital.setBounds(125, 30, 170, 25);
		painelSaida.add(textEdital);
		
		JLabel labelSequencia = new JLabel("Sequência:");
		labelSequencia.setHorizontalAlignment(SwingConstants.RIGHT);
		labelSequencia.setFont(fonte);
		labelSequencia.setBounds(300, 30, 85, 20);
		painelSaida.add(labelSequencia);
		
		JLabel labelSaidaSistac = new JLabel("Pasta de Saída:");
		labelSaidaSistac.setHorizontalAlignment(SwingConstants.RIGHT);
		labelSaidaSistac.setFont(fonte);
		labelSaidaSistac.setBounds(10, 65, 110, 20);
		painelSaida.add(labelSaidaSistac);
		
		textSaidaSistac = new JTextField();
		textSaidaSistac.setFont(fonte);
		textSaidaSistac.setForeground(color);
		textSaidaSistac.setEditable(false);
		textSaidaSistac.setColumns(10);
		textSaidaSistac.setBounds(125, 65, 260, 25);
		painelSaida.add(textSaidaSistac);
		
		botaoSaidaSistac = new JButton(searchIcon);
		botaoSaidaSistac.setToolTipText("Escolher aonde será salvo o arquivo de importação para o Sistac");
		botaoSaidaSistac.addActionListener((event) -> selecionaSaidaSistac());
		botaoSaidaSistac.setBounds(394, 65, 30, 25);
		painelSaida.add(botaoSaidaSistac);
		
		botaoExportar = new JButton(exportIcon);
		botaoExportar.setToolTipText("Exporta o(s) arquivo(s)");
		botaoExportar.addActionListener((event) -> exportarArquivos());
		botaoExportar.setBounds(453, 232, 35, 30);
		painel.add(botaoExportar);
		
		botaoSair = new JButton(exitIcon);
		botaoSair.setToolTipText("Sai do sistema");
		botaoSair.addActionListener((event) -> dispose());
		botaoSair.setBounds(406, 232, 35, 30);
		painel.add(botaoSair);
		
		spinnerSequencia = new JSpinner();
		spinnerSequencia.setBounds(394, 30, 70, 25);
		spinnerSequencia.setValue(1);
		painelSaida.add(spinnerSequencia);
		
		JSpinner.NumberEditor ne_spinnerAno = new JSpinner.NumberEditor(spinnerSequencia);
		ne_spinnerAno.getFormat().setGroupingUsed(false);

		JFormattedTextField spinnerField  = (JFormattedTextField) ne_spinnerAno.getComponent(0);
		DefaultFormatter spinnerFormatter = (DefaultFormatter) spinnerField.getFormatter();

		spinnerSequencia.setEditor(ne_spinnerAno);
		
		JButton buttonInputClear_1 = new JButton(clearIcon);
		buttonInputClear_1.setToolTipText("Busca o arquivo de entrada");
		buttonInputClear_1.setBounds(434, 65, 30, 25);
		painelSaida.add(buttonInputClear_1);

		spinnerField.setFont(fonte);
		spinnerField.setForeground(color);
		spinnerField.setHorizontalAlignment(SwingConstants.CENTER);

		spinnerFormatter.setCommitsOnValidEdit(true);
		
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
		
		if (spinnerSequencia.getValue().toString().trim().isEmpty())
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
			String sequencia = spinnerSequencia.getValue().toString().trim();
			
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
