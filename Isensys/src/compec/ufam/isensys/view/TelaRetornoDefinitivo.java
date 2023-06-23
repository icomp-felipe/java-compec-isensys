package compec.ufam.isensys.view;

import java.io.*;
import java.awt.*;
import javax.swing.*;

import java.util.*;
import java.util.List;
import java.util.stream.*;

import com.phill.libs.*;
import com.phill.libs.ui.*;
import com.phill.libs.i18n.*;
import com.phill.libs.files.*;
import com.phill.libs.mfvapi.*;

import compec.ufam.isensys.io.*;
import compec.ufam.isensys.pdf.*;
import compec.ufam.isensys.model.*;
import compec.ufam.isensys.constants.*;
import compec.ufam.isensys.model.retorno.*;

import org.apache.commons.text.similarity.*;

/** Classe que controla a view de processamento de Retorno Definitivo.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.8, 22/JUN/2023 */
public class TelaRetornoDefinitivo extends JFrame {

	// Serial
	private static final long serialVersionUID = 3675211848533443138L;
	
	// Carregando bundle de idiomas
	private final static PropertyBundle bundle = new PropertyBundle("i18n/tela-retorno-definitivo", null);
	private final static String windowTitle = bundle.getString("final-window-title");
	
	// Declaração de atributos gráficos
	private final Color padrao, yellow = new Color(0xE9EF84);
	private final ImageIcon loadingIcon = new ImageIcon(ResourceManager.getResource("img/loader.gif"));
	
	private final JLabel textCNPJ, textNomeFantasia, textRazaoSocial;
	
	private final JTextField textCompilacao, textSaida;
	private final JButton buttonCompilacaoClear, buttonCompilacaoSelect, buttonSaidaClear, buttonSaidaSelect;
	private final JPanel panelResults;
	private final JLabel  textDeferidos, textIndeferidos, textTotal;
	
	private final JTextField textRetorno, textErros;
	private final JButton buttonRetornoSelect, buttonErrosSelect;
	
	private final JTextField textCabecalho;
	private final JLabel labelStatus;
	private final JButton buttonCabecalhoClear, buttonReport;
	
	// Configurações do sistema
	private Configs configs;
	
	// Atributos dinâmicos
	private List<File> retornosProcessados;
	private File retornoSistac, retornoExcel, compilacao, previousCompilacao;
	private ListaRetornos listaRetornos, listaRecursos;
	private int[] previousCount, currentCount;

	// MFV API
	private final MandatoryFieldsManager fieldValidator;
	private final MandatoryFieldsLogger  fieldLogger;

	public TelaRetornoDefinitivo() {
		
		// Setando título da janela
		setTitle(windowTitle);
		
		// Inicializando atributos gráficos
		GraphicsHelper instance = GraphicsHelper.getInstance();
		GraphicsHelper.setFrameIcon(this,"icon/isensys-icon.png");
		ESCDispose.register(this);
		
		Dimension dimension = new Dimension(670, 555);
		
		JPanel painel = new JPaintedPanel("img/final-screen.jpg", dimension);
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
		panelInstituicao.setBorder(instance.getTitledBorder(bundle.getString("final-panel-instituicao")));
		panelInstituicao.setBounds(12, 10, 645, 105);
		painel.add(panelInstituicao);
				
		JLabel labelCNPJ = new JLabel(bundle.getString("final-label-cnpj"));
		labelCNPJ.setHorizontalAlignment(JLabel.RIGHT);
		labelCNPJ.setFont(fonte);
		labelCNPJ.setBounds(10, 25, 115, 20);
		panelInstituicao.add(labelCNPJ);
				
		textCNPJ = new JLabel();
		textCNPJ.setFont(fonte);
		textCNPJ.setForeground(color);
		textCNPJ.setBounds(130, 25, 145, 20);
		panelInstituicao.add(textCNPJ);
				
		JLabel labelNomeFantasia = new JLabel(bundle.getString("final-label-nome-fantasia"));
		labelNomeFantasia.setHorizontalAlignment(JLabel.RIGHT);
		labelNomeFantasia.setFont(fonte);
		labelNomeFantasia.setBounds(10, 50, 115, 20);
		panelInstituicao.add(labelNomeFantasia);
				
		textNomeFantasia = new JLabel();
		textNomeFantasia.setFont(fonte);
		textNomeFantasia.setForeground(color);
		textNomeFantasia.setBounds(130, 50, 334, 20);
		panelInstituicao.add(textNomeFantasia);
				
		JLabel labelRazaoSocial = new JLabel(bundle.getString("final-label-razao-social"));
		labelRazaoSocial.setHorizontalAlignment(JLabel.RIGHT);
		labelRazaoSocial.setFont(fonte);
		labelRazaoSocial.setBounds(10, 75, 115, 20);
		panelInstituicao.add(labelRazaoSocial);
				
		textRazaoSocial = new JLabel();
		textRazaoSocial.setFont(fonte);
		textRazaoSocial.setForeground(color);
		textRazaoSocial.setBounds(130, 75, 334, 20);
		panelInstituicao.add(textRazaoSocial);
		
		// Painel 'Retorno Preliminar'
		JPanel panelPreliminar = new JPanel();
		panelPreliminar.setOpaque(false);
		panelPreliminar.setLayout(null);
		panelPreliminar.setBorder(instance.getTitledBorder(bundle.getString("final-panel-prelim")));
		panelPreliminar.setBounds(12, 115, 645, 125);
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
		textCompilacao.setBounds(105, 30, 455, 25);
		panelPreliminar.add(textCompilacao);
		
		buttonCompilacaoClear = new JButton(clearIcon);
		buttonCompilacaoClear.setToolTipText(bundle.getString("hint-button-compilacao-clear"));
		buttonCompilacaoClear.addActionListener((event) -> actionCompileClear());
		buttonCompilacaoClear.setBounds(570, 30, 30, 25);
		panelPreliminar.add(buttonCompilacaoClear);
		
		buttonCompilacaoSelect = new JButton(searchIcon);
		buttonCompilacaoSelect.setToolTipText(bundle.getString("hint-button-compilacao-select"));
		buttonCompilacaoSelect.addActionListener((event) -> actionCompileSelect());
		buttonCompilacaoSelect.setBounds(605, 30, 30, 25);
		panelPreliminar.add(buttonCompilacaoSelect);
		
		// Painel 'Análise do Arquivo'
		panelResults = new JPanel();
		panelResults.setOpaque(false);
		panelResults.setVisible(false);
		panelResults.setLayout(null);
		panelResults.setBorder(instance.getTitledBorder(bundle.getString("final-panel-results")));
		panelResults.setBounds(12, 60, 625, 55);
		panelPreliminar.add(panelResults);
		
		JLabel labelDeferidos = new JLabel(bundle.getString("final-label-deferidos"));
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
		
		JLabel labelIndeferidos = new JLabel(bundle.getString("final-label-indeferidos"));
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
		
		JLabel labelTotal = new JLabel(bundle.getString("final-label-total"));
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
		
		// Painel 'Arquivos de Entrada'
		JPanel panelInputFile = new JPanel();
		panelInputFile.setOpaque(false);
		panelInputFile.setLayout(null);
		panelInputFile.setBorder(instance.getTitledBorder(bundle.getString("final-panel-input-file")));
		panelInputFile.setBounds(12, 240, 645, 105);
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
		textRetorno.setBounds(125, 30, 470, 25);
		panelInputFile.add(textRetorno);
		
		buttonRetornoSelect = new JButton(searchIcon);
		buttonRetornoSelect.setToolTipText(bundle.getString("hint-button-retorno-select"));
		buttonRetornoSelect.addActionListener((event) -> actionRetornoSelect());
		buttonRetornoSelect.setEnabled(false);
		buttonRetornoSelect.setBounds(605, 30, 30, 25);
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
		textErros.setBounds(125, 65, 470, 25);
		panelInputFile.add(textErros);
		
		buttonErrosSelect = new JButton(searchIcon);
		buttonErrosSelect.setToolTipText(bundle.getString("hint-button-erros-select"));
		buttonErrosSelect.addActionListener((event) -> actionErrosSelect());
		buttonErrosSelect.setEnabled(false);
		buttonErrosSelect.setBounds(605, 65, 30, 25);
		panelInputFile.add(buttonErrosSelect);
		
		// Painel 'Edital'
		JPanel panelEdital = new JPanel();
		panelEdital.setOpaque(false);
		panelEdital.setLayout(null);
		panelEdital.setBorder(instance.getTitledBorder(bundle.getString("final-panel-edital")));
		panelEdital.setBounds(12, 345, 645, 65);
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
		textCabecalho.setBounds(95, 30, 500, 25);
		panelEdital.add(textCabecalho);
		
		buttonCabecalhoClear = new JButton(clearIcon);
		buttonCabecalhoClear.setToolTipText(bundle.getString("hint-button-cabecalho-clear"));
		buttonCabecalhoClear.addActionListener((event) -> actionHeaderClear());
		buttonCabecalhoClear.setBounds(605, 30, 30, 25);
		panelEdital.add(buttonCabecalhoClear);
		
		// Painel 'Arquivos de Saída'
		JPanel panelSaida = new JPanel();
		panelSaida.setOpaque(false);
		panelSaida.setLayout(null);
		panelSaida.setBorder(instance.getTitledBorder("Arquivos de Saída"));
		panelSaida.setBounds(12, 410, 645, 65);
		painel.add(panelSaida);
		
		JLabel labelSaida = new JLabel("Diretório:");
		labelSaida.setHorizontalAlignment(JLabel.RIGHT);
		labelSaida.setFont(fonte);
		labelSaida.setBounds(10, 30, 80, 20);
		panelSaida.add(labelSaida);
		
		textSaida = new JTextField();
		textSaida.setEditable(false);
		textSaida.setToolTipText(bundle.getString("hint-text-cabecalho"));
		textSaida.setForeground(color);
		textSaida.setFont(fonte);
		textSaida.setBounds(105, 30, 455, 25);
		panelSaida.add(textSaida);
		
		buttonSaidaClear = new JButton(clearIcon);
		buttonSaidaClear.setToolTipText(bundle.getString("hint-button-cabecalho-clear"));
		//buttonSaidaClear.addActionListener((event) -> actionHeaderClear());
		buttonSaidaClear.setBounds(570, 30, 30, 25);
		panelSaida.add(buttonSaidaClear);
		
		buttonSaidaSelect = new JButton(searchIcon);
		buttonSaidaSelect.setToolTipText(bundle.getString("hint-button-compilacao-select"));
		//buttonSaidaSelect.addActionListener((event) -> actionCompileSelect());
		buttonSaidaSelect.setBounds(605, 30, 30, 25);
		panelSaida.add(buttonSaidaSelect);
		
		// Fundo da janela
		labelStatus = new JLabel(loadingIcon);
		labelStatus.setHorizontalAlignment(JLabel.LEFT);
		labelStatus.setFont(fonte);
		labelStatus.setVisible(false);
		labelStatus.setBounds(12, 425, 215, 20);
		painel.add(labelStatus);
		
		buttonReport = new JButton(reportIcon);
		buttonReport.setToolTipText(bundle.getString("hint-button-report"));
		buttonReport.setBounds(620, 420, 35, 30);
		buttonReport.addActionListener((event) -> actionExport());
		painel.add(buttonReport);
		
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
		
		// Só exibe a view se o arquivo de configuração foi lido com sucesso
		if (loadInstituicao()) setVisible(true); else return;
	}

	/********************** Tratamento de Eventos de Botões *******************************/
	
	/** Limpa o painel 'Resultado Preliminar' */
	private void actionCompileClear() {
		
		// Faz algo somente se algum arquivo foi selecionado
		if (this.compilacao != null) {
			
			// Montando janela de diálogo
			final String title   = bundle.getString("final-compile-clear-title");
			final String message = bundle.getString("final-compile-clear-dialog");
			
			// Exibe o diálogo de confirmação
			final int choice = AlertDialog.dialog(this, title, message);
			
			// Limpa os campos se o usuário escolheu 'OK'
			if (choice == AlertDialog.OK_OPTION) {
				
				// Limpando atributos
				this.compilacao    = null;
				this.listaRetornos = null;
				this.listaRecursos = null;
				this.retornoSistac = null;
				this.retornoExcel  = null;
				this.previousCount = new int[2];
				this.currentCount  = new int[2];
				this.retornosProcessados = null;
				
				// Limpando campos de texto
				textCompilacao.setText(null);
				textRetorno   .setText(null);
				textErros     .setText(null);
				textCabecalho .setText(null);
				
				// Ocultando painel de processamento
				panelResults.setVisible(false);
				
				// Limpando dados do painel 'Arquivos de Entrada'
				buttonRetornoSelect.setEnabled(false);
				buttonErrosSelect  .setEnabled(false);
				
				// Recarregando dados institucionais
				loadInstituicao();
				
			}
			
		}
		
	}
	
	/** Carrega o arquivo de entrada de dados e atualiza as informações da janela */
	private void actionCompileSelect() {
		
		// Recuperando título da janela
		final String title = bundle.getString("final-compile-select-title");
		
		// Recuperando o arquivo de entrada
		final File selected  = PhillFileUtils.loadFile(this, title, Constants.FileFormat.BSF, PhillFileUtils.OPEN_DIALOG, this.previousCompilacao, null);
		
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
				final int choice = AlertDialog.dialog(this, dialogTitle, dialogMessage);
				
				// Sobrescreve dados se o usuário escolheu 'OK'
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
			final File selected = PhillFileUtils.loadFile(this, title, Constants.FileFormat.SISTAC_RETV, PhillFileUtils.OPEN_DIALOG, suggestion, null);
						
			// Faz algo somente se algum arquivo foi selecionado
			if (selected != null) {
				
				// Realiza uma série de verificações de integridade no arquivo de retorno do Sistac,
				// caso alguma falhe, este método é quebrado aqui. 
				if (!retornoDependencies(selected)) return;
				
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
			
			// Preparando o nome do arquivo de sugestão
			final File suggestion = new Edital(this.retornoSistac).getErrorFilename(null);
						
			// Recuperando o arquivo de erros
			final File parent = (this.retornoSistac != null) ? this.retornoSistac : this.compilacao;
			final File selected = PhillFileUtils.loadFile(this, title, Constants.FileFormat.XLSX, PhillFileUtils.OPEN_DIALOG, parent, suggestion);
		
			// Faz algo somente se algum arquivo foi selecionado
			if (selected != null) {
		
				// Realiza uma série de verificações de integridade na planilha de erros,
				// caso alguma falhe, este método é quebrado aqui. 
				if (!errosDependencies(selected)) return;
				
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
	
	/** Limpa o texto do cabeçalho. */
	private void actionHeaderClear() {
		
		// Recuperando texto de cabeçalho
		final String cabecalho = textCabecalho.getText();
		
		// Exibe um diálogo de confirmação caso haja algum texto neste campo
		if (!cabecalho.isBlank()) {
			
			// Recuperando strings do diálogo
			final String dtitle = bundle.getString("final-header-clear-dtitle");
			final String dialog = bundle.getString("final-header-clear-dialog");
			
			// Exibe o diálogo de confirmação
			final int choice = AlertDialog.dialog(this, dtitle, dialog);
						
			// Limpa o campo se o usuário escolheu 'OK'
			if (choice == AlertDialog.OK_OPTION)
				textCabecalho.setText(null);
			
		}
		
		// Recuperando foco
		textCabecalho.requestFocus();
		
	}
	
	/** Gera o edital de resultado final. */
	private void actionExport() {
		
		// Realizando validação dos campos antes de prosseguir
		fieldValidator.validate(fieldLogger);
					
		// Só prossigo se todas os campos foram devidamente preenchidos
		if (fieldLogger.hasErrors()) {
						
			AlertDialog.error(this, bundle.getString("final-export-title"), fieldLogger.getErrorString());
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
		
		AlertDialog.error(this, bundle.getString("final-file-error-dialog-title" ),
		                        bundle.getString("final-file-error-dialog-error"));
		
	}
	
	/** @return Lista dos arquivos processados nesta sessão. */
	private List<ArquivoProcessado> computeFiles() {
		
		List<ArquivoProcessado> listaProcessados = new ArrayList<ArquivoProcessado>();

		// Registrando o arquivo de compilação
		listaProcessados.add(new ArquivoProcessado(compilacao, "Compilação"));
		
		// Registrando o(s) arquivo(s) de retorno do Sistac
		if (this.retornosProcessados != null)
			for (File retorno: this.retornosProcessados)
				listaProcessados.add(new ArquivoProcessado(retorno, "Retorno do Sistac"));
		
		// Registrando a planilha de erros
		if (this.retornoExcel != null)
			listaProcessados.add(new ArquivoProcessado(this.retornoExcel, "Planilha de Erros"));
		
		return listaProcessados;
	}
	
	/** Calcula a lista de distância e similaridades. */
	private List<Similaridade> computeSimilaridades() {
		
		// Lista de distância e similaridades
		List<Similaridade> listaSimilaridades = null;
		
		// Recuperando apenas os candidatos deferidos no recurso 
		List<Retorno> recursosDeferidos = this.listaRecursos.getList().stream().collect(Collectors.groupingBy(Retorno::deferido)).get(true);
		
		// Se 'recursosDeferidos == null' significa que não houve recurso ou ninguém foi deferido
		if (recursosDeferidos != null) {
		
			// Inicializando os algoritmos
			final JaroWinklerDistance   algoDistancia    = new JaroWinklerDistance  ();
			final JaroWinklerSimilarity algoSimilaridade = new JaroWinklerSimilarity();
			
			// Inicializando a lista de distância e similaridades
			listaSimilaridades = new ArrayList<Similaridade>();
			
			// Recuperando a lista geral de solicitações de isenção
			List<Retorno> listaIsencao = this.listaRetornos.getList();
			
			// Para cada candidato com deferimento no recurso...
			for (Retorno deferidoRecurso: recursosDeferidos) {

				// ...recupero sua solicitação original...
				Retorno retornoIsencao = listaIsencao.stream().filter(retorno -> retorno.equals(deferidoRecurso)).findFirst().orElse(null);
				
				// ...e realizo o cálculo de distância com os nomes informados
				Similaridade similaridade = new Similaridade(retornoIsencao.getNomeAnterior(), deferidoRecurso.getNome(), algoDistancia, algoSimilaridade);
				listaSimilaridades.add(similaridade);
				
			}

			// Após o preenchimento da lista, realizo a ordenação de acordo com a similaridade
			Comparator<Similaridade> comparator = (sim1, sim2) -> Double.compare(sim1.getSimilaridade(), sim2.getSimilaridade());
			
			Collections.sort(listaSimilaridades, comparator);
			
		}
			
		return listaSimilaridades;
		
	}
	
	/** Realiza uma série de verificações de integridade nos dados contidos no nome de arquivo da planilha.
	 *  @param planilha - planilha de erros de processamento
	 *  @return 'true' caso todas as verificações sejam satisfeitas ou 'false' caso contrário.
	 *  @since 3.0, 22/04/2021 */
	private boolean errosDependencies(final File planilha) {
			
		final Edital retorno = new Edital(this.retornoSistac);
		final Edital erros   = new Edital(planilha);
			
		// Caso algum dos dados seja diferente, uma tela de erro é exibida e o processamento é interrompido
		if (!retorno.equals(erros)) {
			
			AlertDialog.error(this, bundle.getString("final-erros-dependencies-title" ),
	                                bundle.getString("final-erros-dependencies-dialog"));
			
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
			
			final String title  = bundle.getString("final-load-instituicao-title");
			final String dialog = bundle.getString("final-load-instituicao-dialog");
			
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
		
		SwingUtilities.invokeLater(() -> {
		
			// Atualizando a view
			textCNPJ.setText      (StringUtils.BR.formataCNPJ(instituicao.getCNPJ()));
			textCNPJ.setForeground(color);
				
			textNomeFantasia.setText       (instituicao.getNomeFantasia());
			textNomeFantasia.setToolTipText(instituicao.getNomeFantasia());
			textNomeFantasia.setForeground (color);
				
			textRazaoSocial.setText       (instituicao.getRazaoSocial());
			textRazaoSocial.setToolTipText(instituicao.getRazaoSocial());
			textRazaoSocial.setForeground (color);
			
		});
		
	}
	
	/** Realiza uma série de verificações de integridade nos dados contidos no nome de arquivo de retorno do Sistac.
	 *  @param planilha - arquivo de retorno do Sistac
	 *  @return 'true' caso todas as verificações sejam satisfeitas ou 'false' caso contrário.
	 *  @since 3.0, 22/04/2021 */
	private boolean retornoDependencies(final File planilha) {
		
		final Edital compilacao = listaRetornos.getEdital();
		final Edital retorno    = new Edital(planilha);
		
		// Caso algum dos dados seja diferente, uma tela de erro é exibida e o processamento é interrompido
		if (!compilacao.equalsIgnoreDate(retorno)) {
					
			AlertDialog.error(this, bundle.getString("final-retorno-dependencies-title" ),
		                            bundle.getString("final-retorno-dependencies-dialog"));
					
			return false;
		}
		
		return true;
	}
	
	/** Método de atualização de UI relacionado aos métodos <method>actionCompileReload</method> e <method>actionCompileSelect</method>. */
	private void setCompileProcessing(final boolean isProcessing) {
		
		if (isProcessing) {
			
			// Atualizando a view
			labelStatus.setText(bundle.getString("final-compile-processing"));
			labelStatus.setVisible(true);
			
			panelResults.setVisible(false);

			// Bloqueando os botões do painel 'Resultado Preliminar'
			buttonCompilacaoClear .setEnabled(false);
			buttonCompilacaoSelect.setEnabled(false);
			
			// Bloqueando os botões do painel 'Arquivos de Entrada'
			buttonRetornoSelect.setEnabled(false);
			buttonErrosSelect  .setEnabled(false);
			
			// Bloqueando botão de exportar
			buttonReport.setEnabled(false);
			
		}
		else {
			
			// Desbloqueia os botões
			SwingUtilities.invokeLater(() -> {
				
				// Desbloqueando os botões do painel 'Análise do Arquivo'
				buttonCompilacaoClear .setEnabled(true);
				buttonCompilacaoSelect.setEnabled(true);
				
				// Desbloqueando os botões do painel 'Arquivos de Entrada'
				buttonRetornoSelect.setEnabled( this.retornoSistac == null );
				buttonErrosSelect  .setEnabled( this.retornoExcel  == null );
				
				// Desbloqueando botão 'Exportar'
				buttonReport.setEnabled(true);
				
			});
			
		}
		
	}
	
	/** Controla a visualização de alguns campos e botões durante a geração do edital. */
	private void setExportProcessing(final boolean isProcessing) {
		
		SwingUtilities.invokeLater(() -> {
			
			// Controlando visualização dos botões do painel 'Resultado Preliminar'
			buttonCompilacaoClear .setEnabled( !isProcessing );
			buttonCompilacaoSelect.setEnabled( !isProcessing );
			
			// Controlando visualização do botão 'Exportar'
			buttonReport.setEnabled( !isProcessing );
			
			// Controlando visualização do texto de cabeçalho
			textCabecalho       .setEditable( !isProcessing );
			buttonCabecalhoClear.setEnabled ( !isProcessing );
			
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
	private void updateStatistics(final boolean saveHistory) {
		
		// Recuperando dados
		Map<Boolean,List<Retorno>> map = listaRetornos.getList().stream().collect(Collectors.groupingBy(Retorno::deferido));
		
		List<Retorno>   deferidos = map.get(true );
		List<Retorno> indeferidos = map.get(false);
		
		// Atualizando os contadores
		this.currentCount[0] = (  deferidos == null) ? 0 : deferidos  .size();
		this.currentCount[1] = (indeferidos == null) ? 0 : indeferidos.size();
		
		// Salva o histórico dos contadores, quando a flag é ativada
		if (saveHistory) {
			
			this.previousCount[0] = this.currentCount[0];
			this.previousCount[1] = this.currentCount[1];
			
		}
		
		// Recuperando dados da instituição
		final Instituicao carregada = listaRetornos.getInstituicao();
		
		// Atualizando dados da instituição (caso seja diferente da configurada no sistema)
		if (!configs.getInstituicao().equals(carregada)) {
			
			loadInstituicao(carregada, yellow);
			AlertDialog.info(this, bundle.getString("final-update-statistics-title"), bundle.getString("final-update-statistics-dialog"));
					
		}
		
		SwingUtilities.invokeLater(() -> {
			
			// Recuperando texto de cabeçalho
			textCabecalho.setText(listaRetornos.getCabecalho());
			
			// Escondendo o label de status
			labelStatus.setVisible(false);
			
			// Atualizando estatísticas
			textDeferidos  .setText(Integer.toString(this.currentCount[0]));
			textIndeferidos.setText(Integer.toString(this.currentCount[1]));
			textTotal      .setText(Integer.toString(this.currentCount[0] + this.currentCount[1]));
			
			// Exibindo estatísticas
			panelResults.setVisible(true);
			
		});
		
	}
	
	/***************************** Threaded Methods Section *******************************/
	
	/** Carrega os dados de compilação do resultado preliminar para o sistema. */
	private void threadRetriever() {
		
		try {
			
			// Recupera a compilação
			this.retornosProcessados = new ArrayList<File>();
			this.listaRetornos = Compilation.retrieve(compilacao);
			this.listaRecursos = new ListaRetornos();
			this.currentCount  = new int[2];
			this.previousCount = new int[2];
			
			// Processa os retornos (função do botão 'Recarregar')
			if (this.retornoSistac != null)
				CSVSheetReader.readRetorno(retornoSistac, listaRetornos, listaRecursos, false);
			
			if (this.retornoExcel != null)
				ExcelSheetReader.readErros(retornoExcel, listaRetornos, listaRecursos, false);
			
			// Só dorme um pouco pra mostrar progresso na view
			Thread.sleep(500L);
			
			// Atualiza a view com estatísticas do processamento
			updateStatistics(true);
			
		}
		catch (Exception exception) {
			
			exception.printStackTrace();
			
			// Atualizando a view em caso de erro
			SwingUtilities.invokeLater(() -> labelStatus.setVisible(false));
			AlertDialog.error(this, bundle.getString("final-thread-retriever-title" ),
					                bundle.getString("final-thread-retriever-error"));
			
		}
		finally {
			
			// Desbloqueando campos
			setCompileProcessing(false);
			
		}
		
	}
	
	/** Processa o(s) arquivo(s) de retorno do Sistac mesclando os resultados com os do edital preliminar. */
	private void threadSistac() {
		
		try {
			
			// Recuperando dados de edital do nome do arquivo retorno selecionado 
			Edital edital = new Edital(retornoSistac);
						
			// Loop que busca lẽ todos os arquivos subsequentes com base na sequência do primeiro arquivo de retorno selecionado
			for (File atual = retornoSistac; atual.exists(); atual = edital.getNextRetornoFile(retornoSistac)) {
				
				// Processa a lista de retornos do Sistac
				CSVSheetReader.readRetorno(atual, listaRetornos, listaRecursos, false);
				
				// Registrando os arquivos processados
				this.retornosProcessados.add(atual);
				
			}
			
			// Só dorme um pouco pra mostrar progresso na view
			Thread.sleep(500L);
			
			// Atualiza a view com estatísticas do processamento
			updateStatistics(false);
			
		}
		catch (Exception exception) {
			
			exception.printStackTrace();
			
			// Atualizando a view em caso de erro
			SwingUtilities.invokeLater(() -> labelStatus.setVisible(false));
			AlertDialog.error(this, bundle.getString("final-thread-sistac-title" ),
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
			ExcelSheetReader.readErros(retornoExcel, listaRetornos, listaRecursos, false);
			
			// Só dorme um pouco pra mostrar progresso na view
			Thread.sleep(500L);
						
			// Atualiza a view com estatísticas do processamento
			updateStatistics(false);
			
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
			
			// Gerando visualização do edital
			PDFEdital.export(listaRetornos, cabecalho, windowTitle, Resultado.DEFINITIVO);
			
			// Calculando e exibindo o relatório de distância e similaridade
			final List<Similaridade> listaSimilaridades = computeSimilaridades();
			
			if (listaSimilaridades != null)
				PDFSimilaridade.export(listaSimilaridades, textCabecalho.getText().trim(), null);
			
			// Montando a lista de arquivos processados
			final List<ArquivoProcessado> listaProcessados = computeFiles();
			
			PDFRetorno.export(cabecalho, currentCount, previousCount, listaRecursos.getList(), listaProcessados);
			
		}
		catch (Exception exception) {
			
			exception.printStackTrace();
			
			// Atualizando a view em caso de erro
			SwingUtilities.invokeLater(() -> labelStatus.setVisible(false));
			AlertDialog.error(this, bundle.getString("final-thread-export-title" ),
					                bundle.getString("final-thread-export-error"));
			
		}
		finally {
			
			// Desbloqueando botões e campos de texto
			setExportProcessing(false);
			
		}
		
	}

}