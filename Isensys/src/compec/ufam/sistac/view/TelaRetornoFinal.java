package compec.ufam.sistac.view;

import java.io.*;
import java.awt.*;
import javax.swing.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.phill.libs.*;
import com.phill.libs.ui.*;
import com.phill.libs.i18n.*;
import com.phill.libs.files.*;
import com.phill.libs.mfvapi.*;

import compec.ufam.sistac.io.*;
import compec.ufam.sistac.pdf.*;
import compec.ufam.sistac.model.*;

/** Classe que controla a view de processamento Retorno Final
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.0, 18/04/2021 */
public class TelaRetornoFinal extends JFrame {

	// Serial
	private static final long serialVersionUID = 3675211848533443138L;
	
	// Carregando bundle de idiomas
	private final static PropertyBundle bundle = new PropertyBundle("i18n/tela-retorno-final", null);
	private final static String windowTitle = bundle.getString("final-window-title");
	
	// Declaração de atributos gráficos
	private final ImageIcon loadingIcon = new ImageIcon(ResourceManager.getResource("img/loader.gif"));
	
	private final JTextField textCompilacao;
	private final JButton buttonCompilacaoReload, buttonCompilacaoClear, buttonCompilacaoSelect;
	private final JPanel panelResults;
	private final JLabel  textDeferidos, textIndeferidos, textTotal;
	
	private final JTextField textRetorno, textErros;
	private final JButton buttonRetornoSelect, buttonErrosSelect;
	
	private final JTextField textCabecalho;
	private final JLabel labelStatus;
	private final JButton buttonExport;
	
	// Atributos dinâmicos
	private File retornoSistac, retornoExcel, compilacao, previousCompilacao;
	private ListaRetornos listaRetornos;

	// MFV API
	private final MandatoryFieldsManager fieldValidator;
	private final MandatoryFieldsLogger  fieldLogger;
	
	public TelaRetornoFinal() {
		
		// Recuperando o título da janela
		setTitle(windowTitle);
		
		// Inicializando atributos gráficos
		GraphicsHelper instance = GraphicsHelper.getInstance();
		GraphicsHelper.setFrameIcon(this,"icon/isensys-icon.png");
		Dimension dimension = new Dimension(500,385);
		
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
		
		buttonCompilacaoReload = new JButton(reloadIcon);
		buttonCompilacaoReload.setToolTipText(bundle.getString("hint-button-compilacao-reload"));
		buttonCompilacaoReload.addActionListener((event) -> actionCompileReload());
		buttonCompilacaoReload.setBounds(355, 30, 30, 25);
		panelPreliminar.add(buttonCompilacaoReload);
		
		buttonCompilacaoClear = new JButton(clearIcon);
		buttonCompilacaoClear.setToolTipText(bundle.getString("hint-button-compilacao-clear"));
		buttonCompilacaoClear.addActionListener((event) -> actionCompileClear());
		buttonCompilacaoClear.setBounds(395, 30, 30, 25);
		panelPreliminar.add(buttonCompilacaoClear);
		
		buttonCompilacaoSelect = new JButton(searchIcon);
		buttonCompilacaoSelect.setToolTipText(bundle.getString("hint-button-compilacao-select"));
		buttonCompilacaoSelect.addActionListener((event) -> actionCompileSelect());
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
		
		JLabel labelDeferidos = new JLabel(bundle.getString("final-label-deferidos"));
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
		
		JLabel labelIndeferidos = new JLabel(bundle.getString("final-label-indeferidos"));
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
		
		JLabel labelTotal = new JLabel(bundle.getString("final-label-total"));
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
		textRetorno.setBounds(125, 30, 297, 25);
		panelInputFile.add(textRetorno);
		
		buttonRetornoSelect = new JButton(searchIcon);
		buttonRetornoSelect.setToolTipText(bundle.getString("hint-button-retorno-select"));
		buttonRetornoSelect.addActionListener((event) -> actionRetornoSelect());
		buttonRetornoSelect.setEnabled(false);
		buttonRetornoSelect.setBounds(434, 30, 30, 25);
		panelInputFile.add(buttonRetornoSelect);
		
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
		textErros.setBounds(125, 65, 297, 25);
		panelInputFile.add(textErros);
		
		buttonErrosSelect = new JButton(searchIcon);
		buttonErrosSelect.setToolTipText(bundle.getString("hint-button-erros-select"));
		buttonErrosSelect.addActionListener((event) -> actionErrosSelect());
		buttonErrosSelect.setEnabled(false);
		buttonErrosSelect.setBounds(434, 65, 30, 25);
		panelInputFile.add(buttonErrosSelect);
		
		// Painel 'Edital'
		JPanel panelEdital = new JPanel();
		panelEdital.setOpaque(false);
		panelEdital.setLayout(null);
		panelEdital.setBorder(instance.getTitledBorder(bundle.getString("final-panel-edital")));
		panelEdital.setBounds(12, 240, 476, 65);
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
		labelStatus.setHorizontalAlignment(JLabel.LEFT);
		labelStatus.setFont(fonte);
		labelStatus.setVisible(false);
		labelStatus.setBounds(12, 320, 215, 20);
		painel.add(labelStatus);
		
		JButton buttonSair = new JButton(exitIcon);
		buttonSair.setToolTipText(bundle.getString("hint-button-exit"));
		buttonSair.addActionListener((event) -> dispose());
		buttonSair.setBounds(406, 315, 35, 30);
		painel.add(buttonSair);
		
		buttonExport = new JButton(reportIcon);
		buttonExport.setToolTipText(bundle.getString("hint-button-report"));
		buttonExport.setBounds(453, 315, 35, 30);
		buttonExport.addActionListener((event) -> actionExport());
		painel.add(buttonExport);
		
		// Cadastrando validação de campos
		this.fieldValidator = new MandatoryFieldsManager();
		this.fieldLogger    = new MandatoryFieldsLogger ();
		
		fieldValidator.addPermanent(labelCompilacao, () -> this.compilacao != null, bundle.getString("final-mfv-compilacao"), false);
		fieldValidator.addPermanent(labelCabecalho , () -> !textCabecalho.getText().trim().isEmpty(), bundle.getString("final-mfv-cabecalho"), false);
		
		// Mostrando a janela
		setSize(dimension);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		
	}

	/********************** Tratamento de Eventos de Botões *******************************/
	
	/** Reprocessa o arquivo de entrada */
	private void actionCompileReload() {
		
		// Faz algo somente se algum arquivo foi selecionado
		if (this.compilacao != null) {
			
			// Atualizando a view
			setCompileProcessing(true);
			
			// Processando o arquivo
			Thread thread_retriever = new Thread(() -> threadRetriever());
						
			thread_retriever.setName(bundle.getString("final-compile-reload-thread"));
			thread_retriever.start();
			
		}
		
	}
	
	/** Limpa o painel 'Resultado Preliminar' */
	private void actionCompileClear() {
		
		// Faz algo somente se algum arquivo foi selecionado
		if (this.compilacao != null) {
			
			// Montando janela de diálogo
			final String title   = bundle.getString("final-compile-clear-title");
			final String message = bundle.getString("final-compile-clear-dialog");
			
			// Exibe o diálogo de confirmação
			final int choice = AlertDialog.dialog(title, message);
			
			// Limpa os campos se o usuário escolheu 'OK'
			if (choice == AlertDialog.OK_OPTION) {
				
				// Limpando atributos
				this.compilacao = null;
				this.listaRetornos = null;
				this.retornoSistac = null;
				this.retornoExcel  = null;
				
				// Limpando campos de texto
				textCompilacao.setText(null);
				textRetorno   .setText(null);
				textErros     .setText(null);
				
				// Ocultando painel de processamento
				panelResults.setVisible(false);
				
				// Limpando dados do painel 'Arquivos de Entrada'
				buttonRetornoSelect.setEnabled(false);
				buttonErrosSelect  .setEnabled(false);
				
			}
			
		}
		
	}
	
	/** Carrega o arquivo de entrada de dados e atualiza as informações da janela */
	private void actionCompileSelect() {
		
		// Recuperando título da janela
		final String title = bundle.getString("final-compile-select-title");
		
		// Recuperando o arquivo de entrada
		final File selected  = PhillFileUtils.loadFile(title, Constants.FileFormat.BSF, PhillFileUtils.OPEN_DIALOG, this.previousCompilacao);
		
		// Faz algo somente se algum arquivo foi selecionado
		if (selected != null) {
			
			// Atualizando a última seleção de arquivo
			this.previousCompilacao = selected;
			
			// Se já existe uma compilação previamente selecionada, um diálogo de sobrescrever é exibido
			if (this.compilacao != null) {
				
				// Montando janela de diálogo
				final String dialogTitle   = bundle.getString("final-compile-select-dtitle");
				final String dialogMessage = bundle.getString("final-compile-select-dmessage");
				
				// Exibe o diálogo de confirmação
				final int choice = AlertDialog.dialog(dialogTitle, dialogMessage);
				
				// Limpa os campos se o usuário escolheu 'OK'
				if (choice == AlertDialog.OK_OPTION) {
					
					this.retornoSistac = this.retornoExcel = null;
					
					textRetorno.setText(null);
					textErros  .setText(null);
					
				}
				else return;
				
			}
				
			// Salvando arquivo
			this.compilacao = selected;
				
			// Atualizando a view
			textCompilacao.setText(compilacao.getName());
			setCompileProcessing(true);
				
			// Processando o arquivo
			Thread thread_retriever = new Thread(() -> threadRetriever());
				
			thread_retriever.setName(bundle.getString("final-compile-select-thread"));
			thread_retriever.start();
				
		}
		
	}
	
	/** Carrega o arquivo de retorno do Sistac e atualiza as informações da janela. */
	private void actionRetornoSelect() {
		
		// Faz algo somente se o arquivo de compilação já foi previamente selecionado
		if (this.compilacao != null) {
			
			// Recuperando título da janela
			final String title = bundle.getString("final-retorno-select-title");
						
			// Recuperando o arquivo de retorno
			final File suggestion = (this.retornoExcel != null) ? this.retornoExcel : this.compilacao;
			final File selected = PhillFileUtils.loadFile(title, Constants.FileFormat.SISTAC_RETV, PhillFileUtils.OPEN_DIALOG, suggestion);
						
			// Faz algo somente se algum arquivo foi selecionado
			if (selected != null) {
				
				// Salvando arquivo
				this.retornoSistac = selected;
							
				// Atualizando a view
				textRetorno.setText(retornoSistac.getName());
				setCompileProcessing(true);
				
				// Processando o arquivo
				Thread thread_sistac = new Thread(() -> threadSistac());
				
				thread_sistac.setName(bundle.getString("final-retorno-select-thread"));
				thread_sistac.start();
							
			}
			
		}
		else 
			compileFileErrorDialog();
			
	}
	
	/** Carrega o arquivo de erros e atualiza as informações da janela. */
	private void actionErrosSelect() {
		
		// Faz algo somente se o arquivo de compilação já foi previamente selecionado
		if (this.compilacao != null) {
			
			// Recuperando título da janela
			final String title = bundle.getString("final-erros-select-title");
						
			// Recuperando o arquivo de erros
			final File suggestion = (this.retornoSistac != null) ? this.retornoSistac : this.compilacao;
			final File selected = PhillFileUtils.loadFile(title, Constants.FileFormat.XLSX, PhillFileUtils.OPEN_DIALOG, suggestion);
		
			// Faz algo somente se algum arquivo foi selecionado
			if (selected != null) {
		
				// Salvando arquivo
				this.retornoExcel = selected;
				
				// Atualizando a view
				textErros.setText(retornoExcel.getName());
				setCompileProcessing(true);
		
				// Processando o arquivo
				Thread thread_erros = new Thread(() -> threadErros());
				
				thread_erros.setName(bundle.getString("final-erros-select-thread"));
				thread_erros.start();
							
			}
			
		}
		else 
			compileFileErrorDialog();
		
	}
	
	/** Gera o edital de resultado final. */
	private void actionExport() {
		
		// Realizando validação dos campos antes de prosseguir
		fieldValidator.validate(fieldLogger);
					
		// Só prossigo se todas os campos foram devidamente preenchidos
		if (fieldLogger.hasErrors()) {
						
			AlertDialog.error(bundle.getString("final-export-title"), fieldLogger.getErrorString());
			fieldLogger.clear(); return;
			
		}
		
		// Processando o edital
		Thread thread_export = new Thread(() -> threadExport());
										
		thread_export.setName(bundle.getString("final-export-thread"));
		thread_export.start();
		
		
	}
	
	/************************* Utility Methods Section ************************************/
	
	/** Mostra uma tela de erro caso o arquivo de compilação não tenha sido selecionado. */
	private void compileFileErrorDialog() {
		
		AlertDialog.error( bundle.getString("final-file-error-dialog-title" ),
		                   bundle.getString("final-file-error-dialog-error"));
		
	}
	
	/** Método de atualização de UI relacionado aos métodos <method>actionCompileReload</method> e <method>actionCompileSelect</method>. */
	private void setCompileProcessing(final boolean isProcessing) {
		
		if (isProcessing) {
			
			// Atualizando a view
			labelStatus.setText(bundle.getString("final-compile-processing"));
			labelStatus.setVisible(true);
			
			panelResults.setVisible(false);

			// Bloqueando os botões do painel 'Resultado Preliminar'
			buttonCompilacaoReload.setEnabled(false);
			buttonCompilacaoClear .setEnabled(false);
			buttonCompilacaoSelect.setEnabled(false);
			
			// Bloqueando os botões do painel 'Arquivos de Entrada'
			buttonRetornoSelect.setEnabled(false);
			buttonErrosSelect  .setEnabled(false);
			
			// Bloqueando botão de exportar
			buttonExport.setEnabled(false);
			
		}
		else {
			
			// Desbloqueia os botões
			SwingUtilities.invokeLater(() -> {
				
				// Desbloqueando os botões do painel 'Análise do Arquivo'
				buttonCompilacaoReload.setEnabled(true);
				buttonCompilacaoClear .setEnabled(true);
				buttonCompilacaoSelect.setEnabled(true);
				
				// Desbloqueando os botões do painel 'Arquivos de Entrada'
				buttonRetornoSelect.setEnabled( this.retornoSistac == null );
				buttonErrosSelect  .setEnabled( this.retornoExcel  == null );
				
				// Desbloqueando botão 'Exportar'
				buttonExport.setEnabled(true);
				
			});
			
		}
		
	}
	
	/** Controla a visualização de alguns campos e botões durante a geração do edital. */
	private void setExportProcessing(final boolean isProcessing) {
		
		SwingUtilities.invokeLater(() -> {
			
			// Controlando visualização dos botões do painel 'Resultado Preliminar'
			buttonCompilacaoReload.setEnabled( !isProcessing );
			buttonCompilacaoClear .setEnabled( !isProcessing );
			buttonCompilacaoSelect.setEnabled( !isProcessing );
			
			// Controlando visualização do botão 'Exportar'
			buttonExport.setEnabled( !isProcessing );
			
			// Controlando visualização do texto de cabeçalho
			textCabecalho.setEditable( !isProcessing );
			
			// Controlando visualização dos botões do painel 'Arquivos de Entrada' e do label de status
			if (isProcessing) {
				
				buttonRetornoSelect.setEnabled(false);
				buttonErrosSelect  .setEnabled(false);
				
				labelStatus.setText(bundle.getString("final-export-processing"));
				labelStatus.setVisible(true);
				
			}
			else {
				
				buttonRetornoSelect.setEnabled( this.retornoSistac == null );
				buttonErrosSelect  .setEnabled( this.retornoExcel  == null );
				
				labelStatus.setVisible(false);
				
			}
			
		});
		
	}
	
	/** Atualiza os totais de candidatos processados. */
	private void updateStatistics() {
		
		// Recuperando dados
		Map<Boolean,List<Retorno>> map = listaRetornos.getList().stream().collect(Collectors.groupingBy(Retorno::deferido));
		
		List<Retorno>   deferidos = map.get(true );
		List<Retorno> indeferidos = map.get(false);
		
		int   deferidosCount = (  deferidos == null) ? 0 : deferidos  .size();
		int indeferidosCount = (indeferidos == null) ? 0 : indeferidos.size();
		
		SwingUtilities.invokeLater(() -> {
			
			// Escondendo o label de status
			labelStatus.setVisible(false);
			
			// Atualizando estatísticas
			textDeferidos  .setText(Integer.toString(deferidosCount  ));
			textIndeferidos.setText(Integer.toString(indeferidosCount));
			textTotal      .setText(Integer.toString(indeferidosCount + deferidosCount));
			
			// Exibindo estatísticas
			panelResults.setVisible(true);
			
		});
		
	}
	
	/***************************** Threaded Methods Section *******************************/
	
	/** Carrega os dados de compilação do resultado preliminar para o sistema. */
	private void threadRetriever() {
		
		try {
			
			// Recupera a compilação
			this.listaRetornos = Compilation.retrieve(compilacao);
			
			// Processa os retornos (função do botão 'Recarregar')
			if (this.retornoSistac != null)
				SistacFile.readRetorno(listaRetornos, retornoSistac);
			
			if (this.retornoExcel != null)
				ExcelSheetReader.readRetorno(listaRetornos, retornoExcel);
			
			// Só dorme um pouco pra mostrar progresso na view
			Thread.sleep(2000L);
			
			// Atualiza a view com estatísticas do processamento
			updateStatistics();
			
		}
		catch (Exception exception) {
			
			exception.printStackTrace();
			
			// Atualizando a view em caso de erro
			SwingUtilities.invokeLater(() -> labelStatus.setVisible(false));
			AlertDialog.error( bundle.getString("final-thread-retriever-title" ),
					           bundle.getString("final-thread-retriever-error"));
			
		}
		finally {
			
			// Desbloqueando campos
			setCompileProcessing(false);
			
		}
		
	}
	
	/** Processa o arquivo de retorno do Sistac mesclando os resultados com os do edital preliminar. */
	private void threadSistac() {
		
		try {
			
			// Processa a lista de retornos do Sistac
			SistacFile.readRetorno(listaRetornos, retornoSistac);
			
			// Só dorme um pouco pra mostrar progresso na view
			Thread.sleep(2000L);
						
			// Atualiza a view com estatísticas do processamento
			updateStatistics();
			
		}
		catch (Exception exception) {
			
			exception.printStackTrace();
			
			// Atualizando a view em caso de erro
			SwingUtilities.invokeLater(() -> labelStatus.setVisible(false));
			AlertDialog.error( bundle.getString("final-thread-sistac-title" ),
					           bundle.getString("final-thread-sistac-error"));
			
		}
		finally {
			
			// Desbloqueando campos
			setCompileProcessing(false);
			
		}
		
	}
	
	/** Processa o arquivo de erros do Excel mesclando os resultados com os do edital preliminar. */
	private void threadErros() {
		
		try {
			
			// Processa a lista de erros
			ExcelSheetReader.readRetorno(listaRetornos, retornoExcel);
			
			// Só dorme um pouco pra mostrar progresso na view
			Thread.sleep(2000L);
						
			// Atualiza a view com estatísticas do processamento
			updateStatistics();
			
		}
		catch (Exception exception) {
			
			exception.printStackTrace();
			
			// Atualizando a view em caso de erro
			SwingUtilities.invokeLater(() -> labelStatus.setVisible(false));
			AlertDialog.error( bundle.getString("final-thread-erros-title" ),
					           bundle.getString("final-thread-erros-error"));
			
		}
		finally {
			
			// Desbloqueando campos
			setCompileProcessing(false);
			
		}
		
	}
	
	/** Gera a visualização do edital. */
	private void threadExport() {
		
		try {
			
			// Bloqueando botões e campos de texto
			setExportProcessing(true);
		
			// Recuperando cabeçalho
			final String cabecalho = textCabecalho.getText().trim();
			
			// Ordenando dados
			listaRetornos.sort();
			
			// Gerando visualização
			PDFExport.export(listaRetornos, cabecalho, windowTitle, Resultado.FINAL);
			
		}
		catch (Exception exception) {
			
			exception.printStackTrace();
			
			// Atualizando a view em caso de erro
			SwingUtilities.invokeLater(() -> labelStatus.setVisible(false));
			AlertDialog.error( bundle.getString("final-thread-export-title" ),
					           bundle.getString("final-thread-export-error"));
			
		}
		finally {
			
			// Desbloqueando botões e campos de texto
			setExportProcessing(false);
			
		}
		
	}
	
}