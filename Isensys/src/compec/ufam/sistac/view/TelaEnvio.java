package compec.ufam.sistac.view;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

import com.phill.libs.*;
import com.phill.libs.ui.*;
import com.phill.libs.i18n.*;
import com.phill.libs.files.*;
import com.phill.libs.mfvapi.*;

import compec.ufam.sistac.io.*;
import compec.ufam.sistac.model.*;

/** Implementa a tela de processamento do arquivo de solicitações de isenção.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.0, 18/04/2021 */
public class TelaEnvio extends JFrame {

	// Serial
	private static final long serialVersionUID = -1766759262038217449L;
	
	// Carregando bundle de idiomas
	private final static PropertyBundle bundle = new PropertyBundle("i18n/tela-envio", null);
	
	// Declaração de atributos gráficos
	private final JTextField textInputName, textOutputEdital, textOutputFolder;
	private final JSpinner spinnerOutputSequencia;
	private final JButton buttonInputReload, buttonInputClear, buttonInputSelect;
	private final JLabel labelInputStatus, labelInputOK, labelInputError, labelInputTotal;
	private final ImageIcon loadingIcon = new ImageIcon(ResourceManager.getResource("img/loader.gif"));
	
	// Atributos dinâmicos
	private ParseResult resultList;
	private File inputFile, outputDir;
	
	// MFV API
	private final MandatoryFieldsManager fieldValidator;
	private final MandatoryFieldsLogger  fieldLogger;
	
	public static final int INDEXES[] = new int[]{1,2,3,4,5,6,7,8,9};
	
	public TelaEnvio() {
		
		// Recuperando o título da janela
		setTitle(bundle.getString("envio-window-title"));
		
		// Inicializando atributos gráficos
		GraphicsHelper instance = GraphicsHelper.getInstance();
		GraphicsHelper.setFrameIcon(this,"icon/isensys-icon.png");
		Dimension dimension = new Dimension(500,300);
		
		JPanel painel = new JPaintedPanel("img/envio-screen.jpg",dimension);
		painel.setLayout(null);
		setContentPane(painel);
		
		// Recuperando ícones
		Icon clearIcon  = ResourceManager.getIcon("icon/clear.png" ,20,20);
		Icon reloadIcon = ResourceManager.getIcon("icon/reload.png",20,20);
		Icon searchIcon = ResourceManager.getIcon("icon/search.png",20,20);
		Icon exitIcon   = ResourceManager.getIcon("icon/exit.png"  ,25,25);
		Icon exportIcon = ResourceManager.getIcon("icon/save.png"  ,25,25);
		
		// Recuperando fontes e cores
		Font  fonte = instance.getFont ();
		Color color = instance.getColor();
		
		// Painel 'Arquivo de Entrada'
		JPanel painelEntrada = new JPanel();
		painelEntrada.setOpaque(false);
		painelEntrada.setBorder(instance.getTitledBorder(bundle.getString("envio-input-panel")));
		painelEntrada.setBounds(12, 10, 476, 105);
		painelEntrada.setLayout(null);
		painel.add(painelEntrada);
		
		JLabel labelInputName = new JLabel(bundle.getString("envio-label-input-name"));
		labelInputName.setFont(fonte);
		labelInputName.setBounds(10, 30, 50, 20);
		painelEntrada.add(labelInputName);
		
		textInputName = new JTextField();
		textInputName.setToolTipText(bundle.getString("hint-text-input-name"));
		textInputName.setEditable(false);
		textInputName.setForeground(color);
		textInputName.setFont(fonte);
		textInputName.setBounds(65, 30, 280, 25);
		painelEntrada.add(textInputName);
		
		buttonInputReload = new JButton(reloadIcon);
		buttonInputReload.addActionListener((event) -> actionInputReload());
		buttonInputReload.setToolTipText(bundle.getString("hint-button-input-reload"));
		buttonInputReload.setBounds(355, 30, 30, 25);
		painelEntrada.add(buttonInputReload);
		
		buttonInputClear = new JButton(clearIcon);
		buttonInputClear.addActionListener((event) -> actionInputClear());
		buttonInputClear.setToolTipText(bundle.getString("hint-button-input-clear"));
		buttonInputClear.setBounds(395, 30, 30, 25);
		painelEntrada.add(buttonInputClear);
		
		buttonInputSelect = new JButton(searchIcon);
		buttonInputSelect.setToolTipText(bundle.getString("hint-button-input-select"));
		buttonInputSelect.addActionListener((event) -> actionInputSelect());
		buttonInputSelect.setBounds(435, 30, 30, 25);
		painelEntrada.add(buttonInputSelect);
		
		labelInputStatus = new JLabel();
		labelInputStatus.setFont(fonte);
		labelInputStatus.setBounds(10, 70, 130, 20);
		painelEntrada.add(labelInputStatus);
		
		labelInputOK = new JLabel();
		labelInputOK.setHorizontalAlignment(JLabel.CENTER);
		labelInputOK.setBounds(150, 70, 80, 20);
		labelInputOK.setForeground(new Color(0x0D6B12));
		labelInputOK.setFont(fonte);
		painelEntrada.add(labelInputOK);
		
		labelInputError = new JLabel();
		labelInputError.setHorizontalAlignment(JLabel.CENTER);
		labelInputError.setBounds(240, 70, 100, 20);
		labelInputError.setForeground(new Color(0xBC1742));
		labelInputError.setFont(fonte);
		painelEntrada.add(labelInputError);
		
		labelInputTotal = new JLabel();
		labelInputTotal.setHorizontalAlignment(JLabel.CENTER);
		labelInputTotal.setBounds(350, 70, 105, 20);
		labelInputTotal.setForeground(color);
		labelInputTotal.setFont(fonte);
		painelEntrada.add(labelInputTotal);
		
		// Painel 'Arquivos de Saída'
		JPanel painelSaida = new JPanel();
		painelSaida.setOpaque(false);
		painelSaida.setBorder(instance.getTitledBorder(bundle.getString("envio-output-panel")));
		painelSaida.setBounds(12, 115, 476, 105);
		painelSaida.setLayout(null);
		painel.add(painelSaida);
		
		JLabel labelOutputEdital = new JLabel(bundle.getString("envio-label-output-edital"));
		labelOutputEdital.setHorizontalAlignment(JLabel.RIGHT);
		labelOutputEdital.setFont(fonte);
		labelOutputEdital.setBounds(10, 30, 110, 20);
		painelSaida.add(labelOutputEdital);
		
		textOutputEdital = new JTextFieldBounded(6);
		textOutputEdital.setToolTipText(bundle.getString("hint-text-output-edital"));
		textOutputEdital.setForeground(color);
		textOutputEdital.setFont(fonte);
		textOutputEdital.setBounds(125, 30, 170, 25);
		painelSaida.add(textOutputEdital);
		
		JLabel labelOutputSequencia = new JLabel(bundle.getString("envio-label-output-sequencia"));
		labelOutputSequencia.setHorizontalAlignment(JLabel.RIGHT);
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
		
		DefaultFormatter spinnerFormatter = (DefaultFormatter) spinnerField.getFormatter();
		spinnerFormatter.setCommitsOnValidEdit(true);
		
		spinnerField.setFont(fonte);
		spinnerField.setForeground(color);
		spinnerField.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel labelOutputFolder = new JLabel(bundle.getString("envio-label-output-folder"));
		labelOutputFolder.setHorizontalAlignment(JLabel.RIGHT);
		labelOutputFolder.setFont(fonte);
		labelOutputFolder.setBounds(10, 65, 110, 20);
		painelSaida.add(labelOutputFolder);
		
		textOutputFolder = new JTextField();
		textOutputFolder.setToolTipText(bundle.getString("hint-text-output-name"));
		textOutputFolder.setFont(fonte);
		textOutputFolder.setForeground(color);
		textOutputFolder.setEditable(false);
		textOutputFolder.setBounds(125, 65, 260, 25);
		painelSaida.add(textOutputFolder);
		
		JButton buttonOutputSelect = new JButton(searchIcon);
		buttonOutputSelect.setToolTipText(bundle.getString("hint-button-output-select"));
		buttonOutputSelect.addActionListener((event) -> actionOutputSelect());
		buttonOutputSelect.setBounds(394, 65, 30, 25);
		painelSaida.add(buttonOutputSelect);
		
		JButton buttonOutputClear = new JButton(clearIcon);
		buttonOutputClear.setToolTipText(bundle.getString("hint-button-output-clear"));
		buttonOutputClear.addActionListener((event) -> actionOutputClear());
		buttonOutputClear.setBounds(434, 65, 30, 25);
		painelSaida.add(buttonOutputClear);
		
		// Fundo da janela
		JButton buttonExit = new JButton(exitIcon);
		buttonExit.setToolTipText(bundle.getString("hint-button-exit"));
		buttonExit.addActionListener((event) -> dispose());
		buttonExit.setBounds(406, 232, 35, 30);
		painel.add(buttonExit);
		
		JButton buttonExport = new JButton(exportIcon);
		buttonExport.setToolTipText(bundle.getString("hint-button-export"));
		buttonExport.addActionListener((event) -> actionExport());
		buttonExport.setBounds(453, 232, 35, 30);
		painel.add(buttonExport);

		// Cadastrando validação de campos
		this.fieldValidator = new MandatoryFieldsManager();
		this.fieldLogger    = new MandatoryFieldsLogger ();
		
		fieldValidator.addPermanent(labelInputName   , () -> this.inputFile != null, bundle.getString("envio-mfv-input"), false);
		fieldValidator.addPermanent(labelOutputEdital, () -> {
																final String text = textOutputEdital.getText().trim();
																return text.length() == 6 && StringUtils.isAlphanumericStringOnly(text);
															 }, bundle.getString("envio-mfv-edital"), false);
		fieldValidator.addPermanent(labelOutputFolder, () -> this.outputDir != null, bundle.getString("envio-mfv-output"), false);
		
		// Mostrando a janela
		setSize(dimension);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		
	}

	/********************** Tratamento de Eventos de Botões *******************************/
	
	/** Reprocessa o arquivo de entrada */
	private void actionInputReload() {
		
		// Faz algo somente se algum arquivo foi selecionado
		if (this.inputFile != null) {
			
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
		if (this.inputFile != null) {
			
			// Montando janela de diálogo
			final String title   = bundle.getString("envio-input-clear-title");
			final String message = bundle.getString("envio-input-clear-dialog");
			
			// Exibe o diálogo de confirmação
			final int choice = AlertDialog.dialog(title, message);
			
			// Limpa os campos se o usuário escolheu 'OK'
			if (choice == AlertDialog.OK_OPTION) {
				
				this.inputFile  = null;
				this.resultList = null;
				
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
		this.inputFile  = PhillFileUtils.loadFile(title, Constants.FileFormat.SISTAC_INPUT, PhillFileUtils.OPEN_DIALOG, null);
		
		// Faz algo somente se algum arquivo foi selecionado
		if (this.inputFile != null) {
			
			// Atualizando a view
			textInputName.setText(inputFile.getName());
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
		this.outputDir = PhillFileUtils.loadDir(title, PhillFileUtils.OPEN_DIALOG, null);
		
		// Atualizando a view
		if (outputDir != null)
			textOutputFolder.setText(outputDir.getAbsolutePath());
		
	}
	
	/** Limpa o diretório de saída */
	private void actionOutputClear() {
		
		// Faz algo somente se algum diretório foi selecionado
		if (this.outputDir != null) {
					
			// Montando janela de diálogo
			final String title   = bundle.getString("envio-output-clear-title");
			final String message = bundle.getString("envio-output-clear-dialog");
					
			// Exibe o diálogo de confirmação
			final int choice = AlertDialog.dialog(title, message);
					
			// Limpa o campo 'Pasta de Saída' se o usuário escolheu 'OK'
			if (choice == AlertDialog.OK_OPTION) {
				
				this.outputDir = null;
				textOutputFolder.setText(null);
				
			}
			
		}
		
	}
	
	/** Exporta os arquivos de envio do sistac + planilha de erros (se houver) no formato Excel. */
	private void actionExport() {
		
		try {

			// Realizando validação dos campos antes de prosseguir
			fieldValidator.validate(fieldLogger);
			
			// Só prossigo se todas os campos foram devidamente preenchidos
			if (fieldLogger.hasErrors()) {
				
				AlertDialog.error(bundle.getString("envio-export-title"), fieldLogger.getErrorString());
				fieldLogger.clear(); return;
				
			}
			
			// Recuperando edital e sequência
			final String edital    = textOutputEdital.getText().trim();
			final String sequencia = String.format("%03d", spinnerOutputSequencia.getValue());
			
			// Criando arquivo de saída - Sistac
			final File saidaSistac = SistacFile.getSistacExportName(this.outputDir, edital, sequencia);
			
			SistacFile.generate(resultList.getListaCandidatos(), saidaSistac);
			
			// Criando arquivo de saída - Excel (apenas se houveram erros no processamento)
			if (this.resultList.getListaExcecoes().size() > 0) {
				
				final String filename = String.format("errors-%s-%s.xlsx", edital, sequencia);
				final File saidaExcel = new File(this.outputDir, filename);
				
				ExcelSheetWriter.write(resultList.getListaExcecoes(), saidaExcel);
				
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
		labelInputStatus.setIcon(loadingIcon);
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
		final int sizeERR = resultList.getListaExcecoes  ().size();
		final int sizeOK  = resultList.getListaCandidatos().size();
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
			if (inputFile.getName().endsWith("xlsx"))
				resultList = ExcelSheetReader.read(inputFile, INDEXES);
			else
				resultList = CSVSheetReader.read(inputFile, INDEXES);
		
			// Só dorme um pouco pra mostrar progresso na view
			Thread.sleep(2000L);
			
			// Atualiza a view com estatísticas do processamento
			updateStatistics();
			
		}
		catch (Exception exception) {
			
			exception.printStackTrace();
			
			// Atualizando a view em caso de erro
			SwingUtilities.invokeLater(() -> labelInputStatus.setVisible(false));
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
	
}
