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
	private JTextField textOutputFolder;

	private JButton buttonInputSelect;
	
	private JLabel labelInputStatus;
	
	private JLabel labelInputOK,labelInputError,labelInputTotal;
	
	private ParseResult listaResultados;
	private JTextField textOutputEdital;
	
	private File arquivoEntrada, dirSaida;
	
	private JButton buttonInputReload;
	private JButton buttonInputClear;

	private ImageIcon loading = new ImageIcon(ResourceManager.getResource("img/loader.gif"));
	private JSpinner spinnerOutputSequencia;
	
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
		buttonInputSelect.addActionListener((event) -> actionInputSelect());
		
		buttonInputReload = new JButton(reloadIcon);
		buttonInputReload.addActionListener((event) -> actionInputReload());
		buttonInputReload.setToolTipText("Recarrega o arquivo atual");
		buttonInputReload.setBounds(355, 30, 30, 25);
		painelEntrada.add(buttonInputReload);
		
		buttonInputClear = new JButton(clearIcon);
		buttonInputClear.addActionListener((event) -> actionInputClear());
		buttonInputClear.setToolTipText("Busca o arquivo de entrada");
		buttonInputClear.setBounds(395, 30, 30, 25);
		painelEntrada.add(buttonInputClear);
		buttonInputSelect.setBounds(435, 30, 30, 25);
		painelEntrada.add(buttonInputSelect);
		
		labelInputStatus = new JLabel();
		labelInputStatus.setFont(fonte);
		labelInputStatus.setBounds(10, 70, 130, 20);
		painelEntrada.add(labelInputStatus);
		
		labelInputOK = new JLabel();
		labelInputOK.setHorizontalAlignment(SwingConstants.CENTER);
		labelInputOK.setBounds(150, 70, 80, 20);
		painelEntrada.add(labelInputOK);
		labelInputOK.setForeground(new Color(0x0D6B12));
		labelInputOK.setFont(fonte);
		
		labelInputError = new JLabel();
		labelInputError.setHorizontalAlignment(SwingConstants.CENTER);
		labelInputError.setBounds(240, 70, 100, 20);
		painelEntrada.add(labelInputError);
		labelInputError.setForeground(new Color(0xBC1742));
		labelInputError.setFont(fonte);
		
		labelInputTotal = new JLabel();
		labelInputTotal.setHorizontalAlignment(SwingConstants.CENTER);
		labelInputTotal.setBounds(350, 70, 105, 20);
		painelEntrada.add(labelInputTotal);
		labelInputTotal.setForeground(color);
		labelInputTotal.setFont(fonte);
		
		JPanel painelSaida = new JPanel();
		painelSaida.setOpaque(false);
		painelSaida.setBorder(instance.getTitledBorder("Arquivos de Saída"));
		painelSaida.setBounds(12, 115, 476, 105);
		painel.add(painelSaida);
		painelSaida.setLayout(null);
		
		JLabel labelOutputEdital = new JLabel("Num do Edital:");
		labelOutputEdital.setHorizontalAlignment(SwingConstants.RIGHT);
		labelOutputEdital.setFont(fonte);
		labelOutputEdital.setBounds(10, 30, 110, 20);
		painelSaida.add(labelOutputEdital);
		
		textOutputEdital = new JTextField();
		textOutputEdital.setForeground(color);
		textOutputEdital.setFont(fonte);
		textOutputEdital.setColumns(10);
		textOutputEdital.setBounds(125, 30, 170, 25);
		painelSaida.add(textOutputEdital);
		
		JLabel labelOutputSequencia = new JLabel("Sequência:");
		labelOutputSequencia.setHorizontalAlignment(SwingConstants.RIGHT);
		labelOutputSequencia.setFont(fonte);
		labelOutputSequencia.setBounds(300, 30, 85, 20);
		painelSaida.add(labelOutputSequencia);
		
		spinnerOutputSequencia = new JSpinner();
		spinnerOutputSequencia.setBounds(394, 30, 70, 25);
		spinnerOutputSequencia.setValue(1);
		painelSaida.add(spinnerOutputSequencia);
		
		// Define 1 como número mínimo do JSpinner  
		((SpinnerNumberModel) spinnerOutputSequencia.getModel()).setMinimum(1);
		
		JSpinner.NumberEditor ne_spinnerOutputSequencia = new JSpinner.NumberEditor(spinnerOutputSequencia);
		ne_spinnerOutputSequencia.getFormat().setGroupingUsed(false);
		
		JFormattedTextField spinnerField  = (JFormattedTextField) ne_spinnerOutputSequencia.getComponent(0);
		
				spinnerOutputSequencia.setEditor(ne_spinnerOutputSequencia);
		
		JLabel labelOutputFolder = new JLabel("Pasta de Saída:");
		labelOutputFolder.setHorizontalAlignment(SwingConstants.RIGHT);
		labelOutputFolder.setFont(fonte);
		labelOutputFolder.setBounds(10, 65, 110, 20);
		painelSaida.add(labelOutputFolder);
		
		textOutputFolder = new JTextField();
		textOutputFolder.setFont(fonte);
		textOutputFolder.setForeground(color);
		textOutputFolder.setEditable(false);
		textOutputFolder.setColumns(10);
		textOutputFolder.setBounds(125, 65, 260, 25);
		painelSaida.add(textOutputFolder);
		
		JButton buttonOutputSelect = new JButton(searchIcon);
		buttonOutputSelect.setToolTipText("Escolher aonde será salvo o arquivo de importação para o Sistac");
		buttonOutputSelect.addActionListener((event) -> actionOutputSelect());
		buttonOutputSelect.setBounds(394, 65, 30, 25);
		painelSaida.add(buttonOutputSelect);
		
		JButton buttonExit = new JButton(exitIcon);
		buttonExit.setToolTipText("Sai do sistema");
		buttonExit.addActionListener((event) -> dispose());
		buttonExit.setBounds(406, 232, 35, 30);
		painel.add(buttonExit);
		DefaultFormatter spinnerFormatter = (DefaultFormatter) spinnerField.getFormatter();
		
		JButton buttonOutputClear = new JButton(clearIcon);
		buttonOutputClear.addActionListener((event) -> actionOutputClear());
		buttonOutputClear.setToolTipText("Busca o arquivo de entrada");
		buttonOutputClear.setBounds(434, 65, 30, 25);
		painelSaida.add(buttonOutputClear);
		
		JButton buttonExport = new JButton(exportIcon);
		buttonExport.setToolTipText("Exporta o(s) arquivo(s)");
		buttonExport.addActionListener((event) -> actionExport());
		buttonExport.setBounds(453, 232, 35, 30);
		painel.add(buttonExport);

		spinnerField.setFont(fonte);
		spinnerField.setForeground(color);
		spinnerField.setHorizontalAlignment(SwingConstants.CENTER);

		spinnerFormatter.setCommitsOnValidEdit(true);
		
		setVisible(true);
		
	}

	/********************** Tratamento de Eventos de Botões *******************************/
	
	/** Reprocessa o arquivo de entrada */
	private void actionInputReload() {
		
		if (this.arquivoEntrada != null) {
			
			// Atualizando a view
			setInputProcessing();
			
			// Processando o arquivo
			Thread thread_loader = new Thread(() -> threadParser());
											
			thread_loader.setName(bundle.getString("envio-input-reload-thread"));
			thread_loader.start();
			
		}
		
	}

	/** Limpa o painel 'Arquivo de Entrada' */
	private void actionInputClear() {
		
		// Faz algo somente se algum arquivo foi selecionado
		if (this.arquivoEntrada != null) {
			
			// Montando janela de diálogo
			final String title   = bundle.getString("envio-input-clear-title");
			final String message = bundle.getString("envio-input-clear-dialog");
			
			// Exibe o diálogo de confirmação
			final int choice = AlertDialog.dialog(title, message);
			
			// Limpa os campos se o usuário escolheu 'OK'
			if (choice == AlertDialog.OK_OPTION) {
				
				this.arquivoEntrada  = null;
				this.listaResultados = null;
				
				textInputName.setText(null);
				labelInputStatus.setVisible(false);
				
				labelInputOK   .setVisible(false);
				labelInputError.setVisible(false);
				labelInputTotal.setVisible(false);
				
			}
			
		}
		
	}
	
	/** Carrega o arquivo de entrada de dados e atualiza as informações da janela */
	private void actionInputSelect() {
		
		// Recuperando título da janela
		final String title = bundle.getString("envio-input-select-title");
		
		// Recuperando o arquivo de entrada
		this.arquivoEntrada  = PhillFileUtils.loadFile(title, Constants.FileFormat.SISTAC_INPUT, PhillFileUtils.OPEN_DIALOG, null);
		
		// Faz algo somente se algum arquivo foi selecionado
		if (this.arquivoEntrada != null) {
			
			// Atualizando a view
			textInputName.setText(arquivoEntrada.getName());
			setInputProcessing();
			
			// Processando o arquivo
			Thread thread_loader = new Thread(() -> threadParser());
								
			thread_loader.setName(bundle.getString("envio-input-select-thread"));
			thread_loader.start();
			
		}
		
	}
	
	/** Seleciona o diretório de saída dos arquivos gerados */
	private void actionOutputSelect() {
		
		// Recuperando título da janela
		final String title = bundle.getString("envio-output-select-title");
		
		// Recuperando o diretório de saída
		this.dirSaida = PhillFileUtils.loadDir(title, PhillFileUtils.OPEN_DIALOG, null);
		
		// Atualizando a view
		if (dirSaida != null)
			textOutputFolder.setText(dirSaida.getAbsolutePath());
		
	}
	
	/** Limpa o diretório de saída */
	private void actionOutputClear() {
		
		// Faz algo somente se algum diretório foi selecionado
		if (this.dirSaida != null) {
					
			// Montando janela de diálogo
			final String title   = bundle.getString("envio-output-clear-title");
			final String message = bundle.getString("envio-output-clear-dialog");
					
			// Exibe o diálogo de confirmação
			final int choice = AlertDialog.dialog(title, message);
					
			// Limpa o campo 'Pasta de Saída' se o usuário escolheu 'OK'
			if (choice == AlertDialog.OK_OPTION) {
				
				this.dirSaida = null;
				textOutputFolder.setText(null);
				
			}
			
		}
		
	}
	
	/** Exporta os arquivos de envio do sistac + planilha de erros (se houver) no formato Excel. */
	private void actionExport() {
		
		try {
			
			//////////////// AAAAQQQQQUIIIIIIIIIIIIIIII
			dependenciaSistac();
			
			// Recuperando edital e sequência
			final String edital    = textOutputEdital.getText().trim();
			final String sequencia = String.format("%03d", spinnerOutputSequencia.getValue());
			
			// Criando arquivo de saída - Sistac
			final File saidaSistac = SistacFile.getSistacExportName(this.arquivoEntrada, edital, sequencia);
			SistacFile.generate(listaResultados.getListaCandidatos(), saidaSistac);
			
			// Criando arquivo de saída - Excel (apenas se houveram erros no processamento)
			if (this.listaResultados.getListaExcecoes().size() > 0) {
				
				final String filename = String.format("errors-%s-%s.xlsx", edital, sequencia);
				final File saidaExcel = new File(this.arquivoEntrada, filename);
				
				ExcelSheetWriter.write(listaResultados.getListaExcecoes(), saidaExcel);
				
			}
			
			// Mostrando status na view
			AlertDialog.info( bundle.getString("envio-export-title" ),
							  bundle.getString("envio-export-dialog"));
			
		} catch (Exception exception) {
			
			exception.printStackTrace();
			
			// Mostrando status na view
			AlertDialog.error( bundle.getString("envio-export-title" ),
							   bundle.getString("envio-export-error"));
			
		}
		
	}
	
	/************************* Utility Methods Section ************************************/
	
	/** Método de atualização de UI relacionado aos métodos <method>actionInputReload</method> e <method>actionInputSelect</method>. */
	private void setInputProcessing() {
		
		// Atualizando a view
		labelInputStatus.setIcon(loading);
		labelInputStatus.setText(bundle.getString("envio-input-processing"));
		labelInputStatus.setVisible(true);
				
		labelInputOK   .setVisible(false);
		labelInputError.setVisible(false);
		labelInputTotal.setVisible(false);
				
		buttonInputClear .setEnabled(false);
		buttonInputReload.setEnabled(false);
		buttonInputSelect.setEnabled(false);
		
	}
	
	/** Atualiza os totais de candidatos processados */
	private void updateStatistics() {
		
		// Recuperando dados
		final int sizeERR = listaResultados.getListaExcecoes  ().size();
		final int sizeOK  = listaResultados.getListaCandidatos().size();
		final int sizeALL = (sizeOK + sizeERR);
		
		// Recuperando strings i18n
		final String status    = bundle.getString("envio-statistics-status");
		final String stats_ok  = bundle.getString("envio-statistics-ok");
		final String stats_err = bundle.getString("envio-statistics-error");
		final String stats_tot = bundle.getString("envio-statistics-total");
		
		SwingUtilities.invokeLater(() -> {
			
			// Mudando texto do label de status
			labelInputStatus.setIcon(null);
			labelInputStatus.setText(status);
			
			// Atualizando estatísticas
			labelInputOK   .setText(sizeOK  + " " + stats_ok );
			labelInputError.setText(sizeERR + " " + stats_err);
			labelInputTotal.setText(sizeALL + " " + stats_tot);
			
			// Exibindo estatísticas
			labelInputOK   .setVisible(true);
			labelInputError.setVisible(true);
			labelInputTotal.setVisible(true);
			
		});
		
	}
	
	/***************************** Threaded Methods Section *******************************/
	
	/** Carrega os dados de entrada para o sistema */
	private void threadParser() {
		
		try {
			
			// Seleciona o tipo de leitor de acordo com a extensão do arquivo de entrada
			if (arquivoEntrada.getName().endsWith("xlsx"))
				listaResultados = ExcelSheetReader.read(arquivoEntrada, INDEXES);
			else
				listaResultados = CSVSheetReader.read(arquivoEntrada, INDEXES);
		
			// Só dorme um pouco pra mostrar progresso na view
			Thread.sleep(2000);
			
			// Atualiza a view com estatísticas do processamento
			updateStatistics();
			
		}
		catch (Exception exception) {
			
			exception.printStackTrace(); labelInputStatus .setVisible(false);
			
			// Mostrando status na view
			AlertDialog.error( bundle.getString("envio-parser-title" ),
							   bundle.getString("envio-parser-error"));
			
		}
		finally {
			
			// Desbloqueia os botões
			SwingUtilities.invokeLater(() -> {
				
				buttonInputClear .setEnabled(true);
				buttonInputReload.setEnabled(true);
				buttonInputSelect.setEnabled(true);
				
			});
			
		}
		
	}
	
	
	
	
	
	/** Verifica se as dependências para a montagem do nome do arquivo sistac estão satisfeitas 
	 * @throws FileNotSelectedException */
	private void dependenciaSistac() throws BlankFieldException, FileNotSelectedException {
		
		if (textOutputEdital.getText().trim().isEmpty())
			throw new BlankFieldException("Informe o Edital!");
		
		if (spinnerOutputSequencia.getValue().toString().trim().isEmpty())
			throw new BlankFieldException("Informe a Sequência!");
		
		if (dirSaida == null)
			throw new BlankFieldException("Selecione a pasta de saída");
		
		if (listaResultados == null)
			throw new FileNotSelectedException("Selecione o arquivo de entrada de dados!");
		
	}
	
}
