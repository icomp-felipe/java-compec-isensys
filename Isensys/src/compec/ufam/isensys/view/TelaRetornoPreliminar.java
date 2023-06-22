package compec.ufam.isensys.view;

import java.io.*;
import java.awt.*;
import javax.swing.*;

import java.util.List;
import java.util.Map;
import java.util.stream.*;

import com.phill.libs.*;
import com.phill.libs.ui.*;
import com.phill.libs.i18n.*;
import com.phill.libs.files.*;
import com.phill.libs.mfvapi.*;

import compec.ufam.isensys.io.*;
import compec.ufam.isensys.model.*;
import compec.ufam.isensys.model.retorno.*;
import compec.ufam.isensys.constants.*;
import compec.ufam.isensys.pdf.*;

/** Classe que controla a view de processamento de Retorno Preliminar.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.8, 21/JUN/2023 */
public class TelaRetornoPreliminar extends JFrame {

	// Serial
	private static final long serialVersionUID = -4825877950903636399L;
	
	// Carregando bundle de idiomas
	private final static PropertyBundle bundle = new PropertyBundle("i18n/tela-retorno-preliminar", null);
	private final static String windowTitle = bundle.getString("prelim-window-title");
	
	// Declaração de atributos gráficos
	private final Color padrao, yellow = new Color(0xE9EF84);
	private final ImageIcon loadingIcon = new ImageIcon(ResourceManager.getResource("img/loader.gif"));
	
	private final JLabel textCNPJ, textNomeFantasia, textRazaoSocial;
	
	private final JTextField textRetorno, textErros;
	private final JButton buttonRetornoSelect, buttonRetornoClear, buttonErrosSelect, buttonErrosClear;
	
	private final JPanel panelResults;
	private final JLabel textDeferidos, textIndeferidos, textTotal;
	
	private final JTextField textCabecalho;
	private final JButton buttonCabecalhoClear;
	
	private final JTextField textCompilacao;
	private final JButton buttonCompilacaoSelect, buttonCompilacaoClear;
	
	private final JLabel labelStatus;
	private final JButton buttonReport;
	
	// Configurações do sistema
	private Configs configs;
	
	// Atributos dinâmicos
	private File lastFileSelected;
	private File retornoSistac, retornoExcel, compilacao;
	private ListaRetornos listaRetornos, lastListaRetornos;
	
	// MFV API
	private final MandatoryFieldsManager fieldValidator;
	private final MandatoryFieldsLogger  fieldLogger;

	/******************* Bloco do Método Principal ******************************/
	
	/** Construtor da classe inicializando a view */
	public TelaRetornoPreliminar() {

		// Setando título da janela
		setTitle(windowTitle);
		
		// Inicializando atributos gráficos
		GraphicsHelper instance = GraphicsHelper.getInstance();
		GraphicsHelper.setFrameIcon(this,"icon/isensys-icon.png");
		ESCDispose.register(this);
		
		Dimension dimension = new Dimension(670,485);
		
		JPanel painel = new JPaintedPanel("img/prelim-screen.jpg",dimension);
		painel.setLayout(null);
		setContentPane(painel);
		
		// Recuperando ícones
		Icon clearIcon  = ResourceManager.getIcon("icon/clear.png" ,20,20);
		Icon searchIcon = ResourceManager.getIcon("icon/search.png",20,20);
		Icon reportIcon = ResourceManager.getIcon("icon/report.png",25,25);
		
		// Recuperando fontes e cores
		Font  fonte = instance.getFont ();
		Color color = this.padrao = instance.getColor();
		
		// Painel 'Dados da Instituição'
		JPanel panelInstituicao = new JPanel();
		panelInstituicao.setOpaque(false);
		panelInstituicao.setLayout(null);
		panelInstituicao.setBorder(instance.getTitledBorder(bundle.getString("prelim-panel-instituicao")));
		panelInstituicao.setBounds(12, 10, 645, 105);
		painel.add(panelInstituicao);
		
		JLabel labelCNPJ = new JLabel(bundle.getString("prelim-label-cnpj"));
		labelCNPJ.setHorizontalAlignment(JLabel.RIGHT);
		labelCNPJ.setFont(fonte);
		labelCNPJ.setBounds(10, 25, 115, 20);
		panelInstituicao.add(labelCNPJ);
		
		textCNPJ = new JLabel();
		textCNPJ.setFont(fonte);
		textCNPJ.setForeground(color);
		textCNPJ.setBounds(130, 25, 145, 20);
		panelInstituicao.add(textCNPJ);
		
		JLabel labelNomeFantasia = new JLabel(bundle.getString("prelim-label-nome-fantasia"));
		labelNomeFantasia.setHorizontalAlignment(JLabel.RIGHT);
		labelNomeFantasia.setFont(fonte);
		labelNomeFantasia.setBounds(10, 50, 115, 20);
		panelInstituicao.add(labelNomeFantasia);
		
		textNomeFantasia = new JLabel();
		textNomeFantasia.setFont(fonte);
		textNomeFantasia.setForeground(color);
		textNomeFantasia.setBounds(130, 50, 334, 20);
		panelInstituicao.add(textNomeFantasia);
		
		JLabel labelRazaoSocial = new JLabel(bundle.getString("prelim-label-razao-social"));
		labelRazaoSocial.setHorizontalAlignment(JLabel.RIGHT);
		labelRazaoSocial.setFont(fonte);
		labelRazaoSocial.setBounds(10, 75, 115, 20);
		panelInstituicao.add(labelRazaoSocial);
		
		textRazaoSocial = new JLabel();
		textRazaoSocial.setFont(fonte);
		textRazaoSocial.setForeground(color);
		textRazaoSocial.setBounds(130, 75, 334, 20);
		panelInstituicao.add(textRazaoSocial);
				
		// Painel 'Arquivos de Entrada'
		JPanel panelInputFile = new JPanel();
		panelInputFile.setOpaque(false);
		panelInputFile.setLayout(null);
		panelInputFile.setBorder(instance.getTitledBorder(bundle.getString("prelim-panel-input-file")));
		panelInputFile.setBounds(12, 115, 645, 160);
		painel.add(panelInputFile);
		
		JLabel labelRetorno = new JLabel(bundle.getString("prelim-label-retorno"));
		labelRetorno.setHorizontalAlignment(JLabel.RIGHT);
		labelRetorno.setFont(fonte);
		labelRetorno.setBounds(10, 30, 110, 20);
		panelInputFile.add(labelRetorno);
		
		textRetorno = new JTextField();
		textRetorno.setToolTipText(bundle.getString("hint-text-retorno"));
		textRetorno.setForeground(color);
		textRetorno.setFont(fonte);
		textRetorno.setEditable(false);
		textRetorno.setBounds(125, 30, 430, 25);
		panelInputFile.add(textRetorno);
		
		buttonRetornoSelect = new JButton(searchIcon);
		buttonRetornoSelect.setToolTipText(bundle.getString("hint-button-retorno-select"));
		buttonRetornoSelect.addActionListener((event) -> actionRetornoSelect());
		buttonRetornoSelect.setBounds(565, 30, 30, 25);
		panelInputFile.add(buttonRetornoSelect);
		
		buttonRetornoClear = new JButton(clearIcon);
		buttonRetornoClear.setToolTipText(bundle.getString("hint-button-retorno-clear"));
		buttonRetornoClear.addActionListener((event) -> actionRetornoClear());
		buttonRetornoClear.setBounds(605, 30, 30, 25);
		panelInputFile.add(buttonRetornoClear);
		
		JLabel labelErros = new JLabel(bundle.getString("prelim-label-erros"));
		labelErros.setHorizontalAlignment(JLabel.RIGHT);
		labelErros.setFont(fonte);
		labelErros.setBounds(10, 65, 110, 20);
		panelInputFile.add(labelErros);
		
		textErros = new JTextField();
		textErros.setToolTipText(bundle.getString("hint-text-erros"));
		textErros.setForeground(color);
		textErros.setFont(fonte);
		textErros.setEditable(false);
		textErros.setBounds(125, 65, 430, 25);
		panelInputFile.add(textErros);
		
		buttonErrosSelect = new JButton(searchIcon);
		buttonErrosSelect.setToolTipText(bundle.getString("hint-button-erros-select"));
		buttonErrosSelect.addActionListener((event) -> actionErrosSelect());
		buttonErrosSelect.setEnabled(false);
		buttonErrosSelect.setBounds(565, 65, 30, 25);
		panelInputFile.add(buttonErrosSelect);
		
		buttonErrosClear = new JButton(clearIcon);
		buttonErrosClear.setToolTipText(bundle.getString("hint-button-erros-clear"));
		buttonErrosClear.addActionListener((event) -> actionErrosClear());
		buttonErrosClear.setEnabled(false);
		buttonErrosClear.setBounds(605, 65, 30, 25);
		panelInputFile.add(buttonErrosClear);
		
		// Painel 'Análise do Arquivo'
		panelResults = new JPanel();
		panelResults.setOpaque(false);
		panelResults.setVisible(false);
		panelResults.setLayout(null);
		panelResults.setBorder(instance.getTitledBorder(bundle.getString("prelim-panel-results")));
		panelResults.setBounds(10, 95, 625, 55);
		panelInputFile.add(panelResults);
		
		JLabel labelDeferidos = new JLabel(bundle.getString("prelim-label-deferidos"));
		labelDeferidos.setHorizontalAlignment(JLabel.RIGHT);
		labelDeferidos.setFont(fonte);
		labelDeferidos.setBounds(65, 25, 80, 20);
		panelResults.add(labelDeferidos);
		
		textDeferidos = new JLabel();
		textDeferidos.setHorizontalAlignment(JLabel.CENTER);
		textDeferidos.setForeground(new Color(0x0D6B12));
		textDeferidos.setFont(fonte);
		textDeferidos.setBounds(150, 25, 45, 20);
		panelResults.add(textDeferidos);
		
		JLabel labelIndeferidos = new JLabel(bundle.getString("prelim-label-indeferidos"));
		labelIndeferidos.setHorizontalAlignment(JLabel.RIGHT);
		labelIndeferidos.setFont(fonte);
		labelIndeferidos.setBounds(265, 25, 90, 20);
		panelResults.add(labelIndeferidos);
		
		textIndeferidos = new JLabel();
		textIndeferidos.setHorizontalAlignment(JLabel.CENTER);
		textIndeferidos.setForeground(new Color(0xBC1742));
		textIndeferidos.setFont(fonte);
		textIndeferidos.setBounds(360, 25, 45, 20);
		panelResults.add(textIndeferidos);
		
		JLabel labelTotal = new JLabel(bundle.getString("prelim-label-total"));
		labelTotal.setHorizontalAlignment(JLabel.RIGHT);
		labelTotal.setFont(fonte);
		labelTotal.setBounds(480, 25, 40, 20);
		panelResults.add(labelTotal);
		
		textTotal = new JLabel();
		textTotal.setHorizontalAlignment(JLabel.CENTER);
		textTotal.setForeground(color);
		textTotal.setFont(fonte);
		textTotal.setBounds(525, 25, 45, 20);
		panelResults.add(textTotal);
		
		// Painel 'Edital'
		JPanel panelEdital = new JPanel();
		panelEdital.setOpaque(false);
		panelEdital.setLayout(null);
		panelEdital.setBorder(instance.getTitledBorder(bundle.getString("prelim-panel-edital")));
		panelEdital.setBounds(12, 275, 645, 65);
		painel.add(panelEdital);
		
		JLabel labelCabecalho = new JLabel(bundle.getString("prelim-label-cabecalho"));
		labelCabecalho.setHorizontalAlignment(JLabel.RIGHT);
		labelCabecalho.setFont(fonte);
		labelCabecalho.setBounds(10, 30, 80, 20);
		panelEdital.add(labelCabecalho);
		
		textCabecalho = new JTextField();
		textCabecalho.setToolTipText(bundle.getString("hint-text-cabecalho"));
		textCabecalho.setForeground(color);
		textCabecalho.setFont(fonte);
		textCabecalho.setBounds(95, 30, 500, 25);
		panelEdital.add(textCabecalho);
		
		buttonCabecalhoClear = new JButton(clearIcon);
		buttonCabecalhoClear.setToolTipText(bundle.getString("hint-button-cabecalho-clear"));
		buttonCabecalhoClear.addActionListener((event) -> actionHeaderClear());
		buttonCabecalhoClear.setBounds(605, 30, 30, 25);
		panelEdital.add(buttonCabecalhoClear);
		
		// Painel 'Resultado Final'
		JPanel panelFinal = new JPanel();
		panelFinal.setOpaque(false);
		panelFinal.setLayout(null);
		panelFinal.setBorder(instance.getTitledBorder(bundle.getString("prelim-panel-final")));
		panelFinal.setBounds(12, 340, 645, 65);
		painel.add(panelFinal);
		
		JLabel labelCompilacao = new JLabel(bundle.getString("prelim-label-compilacao"));
		labelCompilacao.setHorizontalAlignment(JLabel.RIGHT);
		labelCompilacao.setFont(fonte);
		labelCompilacao.setBounds(10, 30, 90, 20);
		panelFinal.add(labelCompilacao);
		
		textCompilacao = new JTextField();
		textCompilacao.setToolTipText(bundle.getString("hint-text-compilacao"));
		textCompilacao.setForeground(color);
		textCompilacao.setFont(fonte);
		textCompilacao.setEditable(false);
		textCompilacao.setBounds(105, 30, 450, 25);
		panelFinal.add(textCompilacao);
		
		buttonCompilacaoSelect = new JButton(searchIcon);
		buttonCompilacaoSelect.setToolTipText(bundle.getString("hint-button-compilacao-select"));
		buttonCompilacaoSelect.addActionListener((event) -> actionCompileSelect());
		buttonCompilacaoSelect.setBounds(565, 30, 30, 25);
		panelFinal.add(buttonCompilacaoSelect);
		
		buttonCompilacaoClear = new JButton(clearIcon);
		buttonCompilacaoClear.setToolTipText(bundle.getString("hint-button-compilacao-clear"));
		buttonCompilacaoClear.addActionListener((event) -> actionCompileClear());
		buttonCompilacaoClear.setBounds(605, 30, 30, 25);
		panelFinal.add(buttonCompilacaoClear);
		
		// Fundo da janela
		labelStatus = new JLabel(loadingIcon);
		labelStatus.setHorizontalAlignment(JLabel.LEFT);
		labelStatus.setFont(fonte);
		labelStatus.setVisible(false);
		labelStatus.setBounds(12, 420, 215, 20);
		painel.add(labelStatus);
		
		buttonReport = new JButton(reportIcon);
		buttonReport.setToolTipText(bundle.getString("hint-button-report"));
		buttonReport.setBounds(620, 415, 35, 30);
		buttonReport.addActionListener((event) -> actionExport());
		painel.add(buttonReport);
		
		// Cadastrando validação de campos
		this.fieldValidator = new MandatoryFieldsManager();
		this.fieldLogger    = new MandatoryFieldsLogger ();
		
		fieldValidator.addPermanent(labelRetorno   , () -> this.retornoSistac != null        , bundle.getString("prelim-mfv-retorno"   ), false);
		fieldValidator.addPermanent(labelCabecalho , () -> !textCabecalho.getText().isBlank(), bundle.getString("prelim-mfv-cabecalho" ), false);
		fieldValidator.addPermanent(labelCompilacao, () -> this.compilacao != null           , bundle.getString("prelim-mfv-compilacao"), false);
		
		// Mostrando a janela
		setSize(dimension);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);

		// Só exibe a view se o arquivo de configuração foi lido com sucesso
		if (loadInstituicao()) setVisible(true); else return;
	}
	
	/********************** Tratamento de Eventos de Botões *******************************/
	
	/** Carrega o arquivo de retorno do Sistac e atualiza as informações da janela. */
	private void actionRetornoSelect() {
		
		// Recuperando título da janela
		final String title = bundle.getString("prelim-retorno-select-title");
						
		// Recuperando o arquivo de retorno
		final File selected = PhillFileUtils.loadFile(this, title, Constants.FileFormat.SISTAC_RETV, PhillFileUtils.OPEN_DIALOG, this.lastFileSelected, null);
						
		// Faz algo somente se algum arquivo foi selecionado
		if (selected != null) {
			
			// Atualizando último arquivo selecionado
			this.lastFileSelected = selected;
			
			// Se já existe um arquivo de retorno previamente selecionado, um diálogo de sobrescrever é exibido
			if (this.retornoSistac != null) {
				
				// Recuperando strings do diálogo
				final String dtitle = bundle.getString("prelim-retorno-select-dtitle");
				final String dialog = bundle.getString("prelim-retorno-select-dialog");
				
				// Exibe o diálogo de confirmação
				final int choice = AlertDialog.dialog(this, dtitle, dialog);
				
				// Limpa os campos do arquivo de erro apenas se o usuário escolheu 'OK'
				if (choice == AlertDialog.OK_OPTION) {
					
					this.compilacao   = null;
					this.retornoExcel = null;
					
					textCompilacao.setText(null);
					textErros     .setText(null);
					
				}
				else return;
				
			}
			
			// Realiza uma série de verificações de integridade no arquivo de retorno,
			// caso alguma falhe, este método é quebrado aqui.
			if (!retornoDependencies(selected)) return;

			// Atualizando arquivo interno
			this.retornoSistac = selected;
							
			// Atualizando a view
			textRetorno.setText(retornoSistac.getName());
			setInputProcessing(true);
				
			// Processando o arquivo
			Thread thread_sistac = new Thread(() -> threadSistac());
				
			thread_sistac.setName(bundle.getString("prelim-retorno-select-thread"));
			thread_sistac.start();
							
		}
			
	}
	
	/** Limpa a seleção do arquivo de retorno do Sistac. */
	private void actionRetornoClear() {
		
		// Faz algo somente se algum arquivo de retorno já foi previamente selecionado 
		if (this.retornoSistac != null) {
			
			// Recuperando strings do diálogo
			final String dtitle = bundle.getString("prelim-retorno-clear-dtitle");
			final String dialog = bundle.getString("prelim-retorno-clear-dialog");
						
			// Exibe o diálogo de confirmação
			final int choice = AlertDialog.dialog(this, dtitle, dialog);
									
			// Limpa o campo se o usuário escolheu 'OK'
			if (choice == AlertDialog.OK_OPTION) {
				
				// Limpando atributos
				this.listaRetornos = null;
				this.retornoSistac = null;
				this.retornoExcel  = null;
				this.compilacao    = null;
				
				// Limpando campos de texto
				textRetorno   .setText(null);
				textErros     .setText(null);
				textCompilacao.setText(null);
				
				// Bloquenado botões relacionados à planilha de erros
				buttonErrosSelect.setEnabled(false);
				buttonErrosClear .setEnabled(false);
				
				// Ocultando painel de processamento
				panelResults.setVisible(false);
				
				// Recarregando dados institucionais
				loadInstituicao();
				
			}
			
		}
		
	}
	
	/** Carrega o arquivo de erros e atualiza as informações da janela. */
	private void actionErrosSelect() {
		
		// Recuperando título da janela
		final String title = bundle.getString("prelim-erros-select-title");
		
		// Preparando o nome do arquivo de sugestão
		final File suggestion = new Edital(this.retornoSistac).getErrorFilename(null);
		
		// Recuperando o arquivo de retorno
		final File selected = PhillFileUtils.loadFile(this, title, Constants.FileFormat.XLSX, PhillFileUtils.OPEN_DIALOG, this.lastFileSelected, suggestion);
							
		// Faz algo somente se algum arquivo foi selecionado
		if (selected != null) {
					
			// Atualizando último arquivo selecionado
			this.lastFileSelected = selected;
			
			// Se já existe um arquivo de erros previamente selecionado, um diálogo de sobrescrever é exibido
			if (this.retornoExcel != null) {
							
				// Recuperando strings do diálogo
				final String dtitle = bundle.getString("prelim-erros-select-dtitle");
				final String dialog = bundle.getString("prelim-erros-select-dialog");
							
				// Exibe o diálogo de confirmação
				final int choice = AlertDialog.dialog(this, dtitle, dialog);

				// Recupera o estado anterior se o usuário selecionou 'OK'
				if (choice == AlertDialog.OK_OPTION)
					this.listaRetornos = this.lastListaRetornos;
				else return;
							
			}
			
			// Realiza uma série de verificações de integridade na planilha de erros,
			// caso alguma falhe, este método é quebrado aqui.
			if (!errosDependencies(selected)) return;
			
			// Salvando estado anterior ao processamento dos erros
			this.lastListaRetornos = this.listaRetornos.clone();
			
			// Salvando arquivo
			this.retornoExcel = selected;
			
			// Atualizando a view
			textErros.setText(retornoExcel.getName());
			setInputProcessing(true);
	
			// Processando o arquivo
			Thread thread_erros = new Thread(() -> threadErros());
			
			thread_erros.setName(bundle.getString("prelim-erros-select-thread"));
			thread_erros.start();
			
		}
		
	}
	
	/** Limpa a seleção do arquivo de erros e reverte o processamento ao estado anterior. */
	private void actionErrosClear() {
		
		// Faz algo somente se algum arquivo de erros foi previamente selecionado
		if (this.retornoExcel != null) {
			
			// Recuperando strings do diálogo
			final String dtitle = bundle.getString("prelim-erros-clear-dtitle");
			final String dialog = bundle.getString("prelim-erros-clear-dialog");
						
			// Exibe o diálogo de confirmação
			final int choice = AlertDialog.dialog(this, dtitle, dialog);
									
			// Prossegue apenas se o usuário escolheu 'OK'
			if (choice == AlertDialog.OK_OPTION) {
				
				// Limpando campos
				this.retornoExcel = null;
				textErros.setText(null);
				
				// Recuperando estado anterior
				this.listaRetornos = this.lastListaRetornos;
				
				// Atualizando estatísticas
				updateStatistics();
				
			}
			
		}
		
	}
	
	/** Limpa o texto do cabeçalho. */
	private void actionHeaderClear() {
		
		// Recuperando texto de cabeçalho
		final String cabecalho = textCabecalho.getText();
		
		// Exibe um diálogo de confirmação caso haja algum texto neste campo
		if (!cabecalho.isBlank()) {
			
			// Recuperando strings do diálogo
			final String dtitle = bundle.getString("prelim-header-clear-dtitle");
			final String dialog = bundle.getString("prelim-header-clear-dialog");
			
			// Exibe o diálogo de confirmação
			final int choice = AlertDialog.dialog(this, dtitle, dialog);
						
			// Limpa o campo se o usuário escolheu 'OK'
			if (choice == AlertDialog.OK_OPTION)
				textCabecalho.setText(null);
			
		}
		
		// Recuperando foco
		textCabecalho.requestFocus();
		
	}
	
	/** Seleciona o arquivo de compilação. */
	private void actionCompileSelect() {
		
		// Recuperando título da janela
		final String title = bundle.getString("prelim-compile-select-title");
		
		// Preparando o nome do arquivo de sugestão
		final File suggestion = new Edital(this.retornoSistac).getCompilationFilename();
								
		// Recuperando o arquivo de retorno
		final File selected = PhillFileUtils.loadFile(this, title, Constants.FileFormat.BSF, PhillFileUtils.SAVE_DIALOG, this.lastFileSelected, suggestion);
								
		// Faz algo somente se algum arquivo foi selecionado
		if (selected != null) {
			
			// Atualizando último arquivo selecionado
			this.lastFileSelected = selected;
			
			// Tratamento de sobrescrição de arquivo
			if (selected.exists()) {
				
				// Recuperando strings do diálogo
				final String dtitle = bundle.getString("prelim-compile-select-dtitle");
				final String dialog = bundle.getString("prelim-compile-select-dialog");
				
				// Exibe o diálogo de confirmação
				final int choice = AlertDialog.dialog(this, dtitle, dialog);
							
				// Sai aqui se o usuário não selecionou 'OK'
				if (choice != AlertDialog.OK_OPTION) return;
				
			}
			
			// Atualizando campos
			this.compilacao = selected;
			textCompilacao.setText(selected.getName());
			
		}
		
	}
	
	/** Limpa a seleção do arquivo de compilação */
	private void actionCompileClear() {
		
		// Faz algo somente se algum arquivo foi selecionado
		if (this.compilacao != null) {
			
			// Recuperando strings do diálogo
			final String title   = bundle.getString("prelim-compile-clear-title");
			final String message = bundle.getString("prelim-compile-clear-dialog");
					
			// Exibe o diálogo de confirmação
			final int choice = AlertDialog.dialog(this, title, message);
						
			// Limpa os campos se o usuário escolheu 'OK'
			if (choice == AlertDialog.OK_OPTION) {
				
				this.compilacao = null;
				textCompilacao.setText(null);
				
			}
			
		}
		
	}
	
	/** Gera o edital de resultado preliminar. */
	private void actionExport() {
		
		// Realizando validação dos campos antes de prosseguir
		fieldValidator.validate(fieldLogger);
					
		// Só prossigo se todas os campos foram devidamente preenchidos
		if (fieldLogger.hasErrors()) {
						
			AlertDialog.error(this, bundle.getString("prelim-export-title"), fieldLogger.getErrorString());
			fieldLogger.clear(); return;
			
		}
		
		// Processando o edital
		Thread thread_export = new Thread(() -> threadExport());
										
		thread_export.setName(bundle.getString("prelim-export-thread"));
		thread_export.start();
		
	}
	
	/************************* Utility Methods Section ************************************/
	
	/** Realiza uma série de verificações de integridade nos dados contidos no nome de arquivo da planilha.
	 *  @param planilha - planilha de erros de processamento
	 *  @return 'true' caso todas as verificações sejam satisfeitas ou 'false' caso contrário.
	 *  @since 3.0, 22/04/2021 */
	private boolean errosDependencies(final File planilha) {
			
		Edital retorno = new Edital(retornoSistac);
		Edital erros   = new Edital(planilha);
			
		// Caso algum dos dados seja diferente, uma tela de erro é exibida e o processamento é interrompido
		if (!retorno.equals(erros)) {
			
			AlertDialog.error(this, bundle.getString("prelim-erros-dependencies-title" ),
	                                bundle.getString("prelim-erros-dependencies-dialog"));
			
			return false;
		}
		
		return true;
	}
	
	/** Carrega as configurações do sistema do arquivo em disco.
	 *  @return 'true' se o arquivo foi lido;<br>'false' caso alguma falha tenha ocorrido na leitura.
	 *  @since 3.1, 26/04/2021 */
	private boolean loadInstituicao() {
		
		// Recuperando configurações do sistema
		try {

			this.configs = SystemConfigs.retrieve();
			final Instituicao instituicao = configs.getInstituicao();
			
			// Atualizando a view
			loadInstituicao(instituicao, this.padrao);
			
		}
		catch (Exception exception) {
			
			final String title  = bundle.getString("prelim-load-instituicao-title");
			final String dialog = bundle.getString("prelim-load-instituicao-dialog");
			
			AlertDialog.error(this, title, dialog);	return false;
			
		}
		
		return true;
	}
	
	/** Atualiza a instituicao interna + interface gráfica.
	 *  @param instituicao - dados institucionais
	 *  @param color - cor para pintar os campos de texto referentes aos dados institucionais
	 *  @since 3.0, 22/04/2021 */
	private void loadInstituicao(final Instituicao instituicao, final Color color) {
		
		// Atualizando a instituição das configurações (apenas em memória)
		this.configs.setInstituicao(instituicao);
		
		// Atualizando a view
		textCNPJ.setText      (StringUtils.BR.formataCNPJ(instituicao.getCNPJ()));
		textCNPJ.setForeground(color);
				
		textNomeFantasia.setText       (instituicao.getNomeFantasia());
		textNomeFantasia.setToolTipText(instituicao.getNomeFantasia());
		textNomeFantasia.setForeground (color);
				
		textRazaoSocial.setText       (instituicao.getRazaoSocial());
		textRazaoSocial.setToolTipText(instituicao.getRazaoSocial());
		textRazaoSocial.setForeground (color);
		
	}
	
	/** Realiza uma série de verificações de integridade nos dados institucionais do sistema e do arquivo de retorno do Sistac.
	 *  @param retorno - arquivo de retorno do Sistac
	 *  @return 'true' caso todas as verificações sejam satisfeitas ou 'false' caso contrário.
	 *  @since 3.0, 22/04/2021 */
	private boolean retornoDependencies(final File retorno) {
		
		try {

			// Recuperando dados institucionais do arquivo de retorno
			final Instituicao instituicao = CSVSheetReader.getInstituicao(retorno);
			
			// Comparando dados institucionais do sistema com os carregados do arquivo de retorno 
			final String compare  = this.configs.getInstituicao().compare(instituicao);
			
			// Se os dados são diferentes...
			if (compare != null) {
				
				// O usuário é questionado se deseja prosseguir utilizando os dados institucionais do RETORNO...
				final String wtitle  = bundle.getString         ("prelim-retorno-dependencies-wtitle"          );
				final String wdialog = bundle.getFormattedString("prelim-retorno-dependencies-wdialog", compare);
				
				int wchoice = AlertDialog.dialog(this, wtitle, wdialog);
				
				// Caso deseje prosseguir...
				if (wchoice == AlertDialog.OK_OPTION) {
					
					// Os dados institucionais do RETORNO são validados
					final String validate = instituicao.validate();
					
					// Se há alguma inconsistência nos dados...
					if (validate != null) {
						
						// O usuário é questionado se deseja prosseguir utilizando os dados institucionais do SISTEMA...
						final String etitle  = bundle.getString         ("prelim-retorno-dependencies-etitle"           );
						final String edialog = bundle.getFormattedString("prelim-retorno-dependencies-edialog", validate);
									
						int echoice = AlertDialog.dialog(this, etitle, edialog);
						
						// Caso não deseje prosseguir, o carregamento é cancelado
						if (echoice != AlertDialog.OK_OPTION) return false;
							
					}
					
					// Se não há inconsistência nos dados, estes passam a ser o padrão nesta instância.
					else
						loadInstituicao(instituicao, this.yellow);
				}
					
			}
					
		}
		catch (IOException exception) {
						
			AlertDialog.error(this, bundle.getString("prelim-thread-sistac-title" ),
			                        bundle.getString("prelim-thread-sistac-error"));
			return false;
						
		}
		
		return true;
	}
	
	/** Método de atualização de UI relacionado aos métodos <method>actionRetornoSelect</method> e <method>actionErrosSelect</method>. */
	private void setInputProcessing(final boolean isProcessing) {
		
		if (isProcessing) {
			
			// Atualizando a view
			labelStatus.setText(bundle.getString("prelim-input-processing"));
			labelStatus.setVisible(true);
			
			panelResults.setVisible(false);

			// Bloqueando os botões do painel 'Arquivos de Entrada'
			buttonRetornoSelect.setEnabled(false);
			buttonRetornoClear .setEnabled(false);
			buttonErrosSelect  .setEnabled(false);
			buttonErrosClear   .setEnabled(false);
			
			// Bloqueando botão de exportar
			buttonReport.setEnabled(false);
			
		}
		else {
			
			// Desbloqueia os botões
			SwingUtilities.invokeLater(() -> {
				
				// Desbloqueando os botões do painel 'Arquivos de Entrada'
				buttonRetornoSelect.setEnabled(true);
				buttonRetornoClear .setEnabled(true);
				buttonErrosSelect  .setEnabled( this.retornoSistac != null );
				buttonErrosClear   .setEnabled( this.retornoSistac != null );
				
				// Desbloqueando botão 'Exportar'
				buttonReport.setEnabled(true);
				
			});
			
		}
		
	}
	
	/** Controla a visualização de alguns campos e botões durante a geração do edital. */
	private void setExportProcessing(final boolean isProcessing) {
		
		SwingUtilities.invokeLater(() -> {
			
			// Controlando visualização dos botões e campos de texto
			buttonRetornoSelect.setEnabled( !isProcessing );
			buttonRetornoClear .setEnabled( !isProcessing );
			
			textCabecalho       .setEditable( !isProcessing );
			buttonCabecalhoClear.setEnabled ( !isProcessing );
			
			buttonCompilacaoSelect.setEnabled( !isProcessing );
			buttonCompilacaoClear .setEnabled( !isProcessing );
			
			buttonReport.setEnabled( !isProcessing );
			
			labelStatus.setVisible( isProcessing );
			
			if (isProcessing) {
				
				buttonErrosSelect  .setEnabled(false);
				buttonErrosClear   .setEnabled(false);
				
				
				labelStatus.setText(bundle.getString("prelim-export-processing"));
				
			}
			else {
				
				buttonErrosSelect.setEnabled( this.retornoSistac != null );
				buttonErrosClear .setEnabled( this.retornoSistac != null );
				
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
	
	/** Processa o(s) arquivo(s) de retorno do Sistac. */
	private void threadSistac() {
		
		try {
			
			// Inicializando lista de retornos
			this.listaRetornos = new ListaRetornos();
			
			// Recuperando dados de edital do nome do arquivo retorno selecionado 
			Edital edital = new Edital(retornoSistac);
			
			// Loop que busca lẽ todos os arquivos subsequentes com base na sequência do primeiro arquivo de retorno selecionado
			for (File atual = retornoSistac; atual.exists(); atual = edital.getNextRetornoFile(retornoSistac)) {
				
				// Processa a lista de retornos do Sistac
				CSVSheetReader.readRetorno(atual, listaRetornos, null, true);
				
			}
			
			// Atualiza a view com estatísticas do processamento
			updateStatistics();
			
		}
		catch (Exception exception) {
			
			exception.printStackTrace();
			
			// Atualizando a view em caso de erro
			SwingUtilities.invokeLater(() -> labelStatus.setVisible(false));
			AlertDialog.error(this, bundle.getString("prelim-thread-sistac-title" ),
					                bundle.getString("prelim-thread-sistac-error"));
			
		}
		finally {
			
			// Desbloqueando campos
			setInputProcessing(false);
			
		}
		
	}
	
	/** Processa o arquivo de erros do Excel mesclando os resultados de retorno do sistac. */
	private void threadErros() {
		
		try {
			
			// Processa a lista de erros
			ExcelSheetReader.readErros(retornoExcel, listaRetornos, null, true);
			
			// Só dorme um pouco pra mostrar progresso na view
			Thread.sleep(500L);
						
			// Atualiza a view com estatísticas do processamento
			updateStatistics();
			
		}
		catch (Exception exception) {
			
			exception.printStackTrace();
			
			// Atualizando a view em caso de erro
			SwingUtilities.invokeLater(() -> labelStatus.setVisible(false));
			AlertDialog.error(this, bundle.getString("final-thread-erros-title" ),
					                bundle.getString("final-thread-erros-error"));
			
		}
		finally {
			
			// Desbloqueando campos
			setInputProcessing(false);
			
		}
		
	}
	
	/** Gera a visualização do edital. */
	private void threadExport() {
		
		try {
			
			// Bloqueando botões e campos de texto
			setExportProcessing(true);
		
			// Recuperando cabeçalho
			final String cabecalho = textCabecalho.getText().trim();
			
			// Adicionando dados extras ao arquivo de compilação
			listaRetornos.setInstituicao(configs.getInstituicao());
			listaRetornos.setEdital     (new Edital(retornoSistac));
			listaRetornos.setCabecalho  (cabecalho);
			
			// Ordenando dados
			listaRetornos.sort();
			
			// Gerando visualização
			PDFExport.export(listaRetornos, cabecalho, windowTitle, Resultado.PRELIMINAR);
			Compilation.save(listaRetornos, compilacao);
			
		}
		catch (Exception exception) {
			
			exception.printStackTrace();
			
			// Atualizando a view em caso de erro
			SwingUtilities.invokeLater(() -> labelStatus.setVisible(false));
			AlertDialog.error(this, bundle.getString("prelim-thread-export-title" ),
					                bundle.getString("prelim-thread-export-error"));
			
		}
		finally {
			
			// Desbloqueando botões e campos de texto
			setExportProcessing(false);
			
		}
		
	}
}