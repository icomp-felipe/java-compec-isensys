package compec.ufam.sistac.view;

import java.io.*;
import java.awt.*;
import javax.swing.*;

import com.phill.libs.*;
import com.phill.libs.ui.*;
import com.phill.libs.i18n.*;
import com.phill.libs.files.*;
import com.phill.libs.mfvapi.*;

import compec.ufam.sistac.io.*;
import compec.ufam.sistac.pdf.*;
import compec.ufam.sistac.model.*;

import compec.ufam.sistac.exception.*;

/** Classe que controla a view de processamento de Retorno Preliminar.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.0, 18/04/2021 */
public class TelaRetornoPreliminar extends JFrame {

	// Serial
	private static final long serialVersionUID = -4825877950903636399L;
	
	// Carregando bundle de idiomas
	private final static PropertyBundle bundle = new PropertyBundle("i18n/tela-retorno-preliminar", null);
	private final static String windowTitle = bundle.getString("prelim-window-title");
	
	// Declaração de atributos gráficos
	private final ImageIcon loadingIcon = new ImageIcon(ResourceManager.getResource("img/loader.gif"));
	
	
	
	
	
	
	
	// Mensagens de processamento (exibidas no rodapé) 
	private static final String MSG_LOAD_FILE = "Processando Arquivo";
	private static final String MSG_LOAD_PDF  = "Gerando Visualização";
	
	// Alguns componentes de texto da tela
	private final JTextField textCompilacao,textRetorno,textErros,textCabecalho;
	private final JLabel labelStatus;
	
	private final JLabel  textDeferidos, textIndeferidos, textTotal;
	
	private final JPanel panelResults;
	
	// Recursos de processamento
	private File retornoSistac,retornoExcel,compilacao,sugestao;
	private ListaRetornos listaRetornos;

	/******************* Bloco do Método Principal ******************************/
	
	public static void main(String[] args) {
		new TelaRetornoPreliminar();
	}
	
	/** Construtor da classe inicializando a view */
	public TelaRetornoPreliminar() {

		// Setando título da janela
		setTitle(windowTitle);
		
		// Inicializando atributos gráficos
		GraphicsHelper instance = GraphicsHelper.getInstance();
		GraphicsHelper.setFrameIcon(this,"icon/isensys-icon.png");
		Dimension dimension = new Dimension(500,380);
		
		JPanel painel = new JPaintedPanel("img/prelim-screen.jpg",dimension);
		painel.setLayout(null);
		setContentPane(painel);
		
		// Recuperando ícones
		Icon clearIcon  = ResourceManager.getIcon("icon/clear.png" ,20,20);
		Icon searchIcon = ResourceManager.getIcon("icon/search.png",20,20);
		Icon exitIcon   = ResourceManager.getIcon("icon/exit.png",25,25);
		Icon reportIcon = ResourceManager.getIcon("icon/report.png",25,25);
		
		// Recuperando fontes e cores
		Font  fonte = instance.getFont ();
		Color color = instance.getColor();
				
		// Painel 'Arquivos de Entrada'
		JPanel panelInputFile = new JPanel();
		panelInputFile.setOpaque(false);
		panelInputFile.setLayout(null);
		panelInputFile.setBorder(instance.getTitledBorder(bundle.getString("prelim-panel-input-file")));
		panelInputFile.setBounds(12, 10, 476, 160);
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
		textRetorno.setBounds(125, 30, 260, 25);
		panelInputFile.add(textRetorno);
		
		JButton buttonRetornoSelect = new JButton(searchIcon);
		buttonRetornoSelect.setToolTipText(bundle.getString("hint-button-retorno-select"));
		buttonRetornoSelect.addActionListener((event) -> selecionaArquivoSistac());
		buttonRetornoSelect.setBounds(395, 30, 30, 25);
		panelInputFile.add(buttonRetornoSelect);
		
		JButton buttonRetornoClear = new JButton(clearIcon);
		buttonRetornoClear.setToolTipText(bundle.getString("hint-button-retorno-clear"));
		buttonRetornoClear.setBounds(435, 30, 30, 25);
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
		textErros.setBounds(125, 65, 260, 25);
		panelInputFile.add(textErros);
		
		JButton buttonErrosSelect = new JButton(searchIcon);
		buttonErrosSelect.setToolTipText(bundle.getString("hint-button-erros-select"));
		buttonErrosSelect.addActionListener((event) -> selecionaArquivoExcel());
		buttonErrosSelect.setBounds(395, 65, 30, 25);
		panelInputFile.add(buttonErrosSelect);
		
		JButton buttonErrosClear = new JButton(clearIcon);
		buttonErrosClear.setToolTipText(bundle.getString("hint-button-erros-clear"));
		buttonErrosClear.setBounds(435, 65, 30, 25);
		panelInputFile.add(buttonErrosClear);
		
		// Painel 'Análise do Arquivo'
		panelResults = new JPanel();
		panelResults.setOpaque(false);
		panelResults.setVisible(false);
		panelResults.setLayout(null);
		panelResults.setBorder(instance.getTitledBorder(bundle.getString("prelim-panel-results")));
		panelResults.setBounds(12, 95, 453, 55);
		panelInputFile.add(panelResults);
		
		JLabel labelDeferidos = new JLabel(bundle.getString("prelim-label-deferidos"));
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
		
		JLabel labelIndeferidos = new JLabel(bundle.getString("prelim-label-indeferidos"));
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
		
		JLabel labelTotal = new JLabel(bundle.getString("prelim-label-total"));
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
		
		// Painel 'Edital'
		JPanel panelEdital = new JPanel();
		panelEdital.setOpaque(false);
		panelEdital.setLayout(null);
		panelEdital.setBorder(instance.getTitledBorder(bundle.getString("prelim-panel-edital")));
		panelEdital.setBounds(12, 170, 476, 65);
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
		textCabecalho.setBounds(95, 30, 330, 25);
		panelEdital.add(textCabecalho);
		
		JButton buttonCabecalhoClear = new JButton(clearIcon);
		buttonCabecalhoClear.setToolTipText(bundle.getString("hint-button-cabecalho-clear"));
		buttonCabecalhoClear.setBounds(435, 30, 30, 25);
		panelEdital.add(buttonCabecalhoClear);
		
		// Painel 'Resultado Final'
		JPanel panelFinal = new JPanel();
		panelFinal.setOpaque(false);
		panelFinal.setLayout(null);
		panelFinal.setBorder(instance.getTitledBorder(bundle.getString("prelim-panel-final")));
		panelFinal.setBounds(12, 235, 476, 65);
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
		textCompilacao.setBounds(105, 30, 280, 25);
		panelFinal.add(textCompilacao);
		
		JButton buttonCompilacaoSelect = new JButton(searchIcon);
		buttonCompilacaoSelect.setToolTipText(bundle.getString("hint-button-compilacao-select"));
		buttonCompilacaoSelect.addActionListener((event) -> selecionaCompilacao());
		buttonCompilacaoSelect.setBounds(395, 30, 30, 25);
		panelFinal.add(buttonCompilacaoSelect);
		
		JButton buttonCompilacaoClear = new JButton(clearIcon);
		buttonCompilacaoClear.setToolTipText(bundle.getString("hint-button-compilacao-clear"));
		buttonCompilacaoClear.setBounds(435, 30, 30, 25);
		panelFinal.add(buttonCompilacaoClear);
		
		// Fundo da janela
		labelStatus = new JLabel(loadingIcon);
		labelStatus.setHorizontalAlignment(JLabel.LEFT);
		labelStatus.setFont(fonte);
		labelStatus.setVisible(false);
		labelStatus.setBounds(12, 315, 215, 20);
		painel.add(labelStatus);
		
		JButton buttonSair = new JButton(exitIcon);
		buttonSair.setToolTipText(bundle.getString("hint-button-exit"));
		buttonSair.addActionListener((event) -> dispose());
		buttonSair.setBounds(406, 310, 35, 30);
		painel.add(buttonSair);
		
		JButton buttonReport = new JButton(reportIcon);
		buttonReport.setToolTipText(bundle.getString("hint-button-report"));
		buttonReport.setBounds(453, 310, 35, 30);
		buttonReport.addActionListener((event) -> gerarVisualizacao());
		painel.add(buttonReport);
		
		// Mostrando a janela
		setSize(dimension);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		
	}
	
	/************* Bloco de Tratamento de Eventos de Botão *********************/

	/** Carrega o arquivo de exportação do sistac para o sistema */
	private void selecionaArquivoSistac() {
		
		inicializaLista();
		
		try {
			
			// Abrindo a GUI de seleção de arquivo
			retornoSistac = PhillFileUtils.loadFile("Selecione o arquivo de texto Sistac", Constants.FileFormat.SISTAC_RETV, PhillFileUtils.OPEN_DIALOG, sugestao);
			
			// Atualizando o arquivo de sugestão (último diretório selecionado)
			sugestao = new File(retornoSistac.getParent());
			
			// Atualizando o nome do arquivo no textfield de seleção
			textRetorno.setText(retornoSistac.getName());
			
			// Atualizando as informações da tela e executando a leitura do arquivo
			updateInfo(MSG_LOAD_FILE);
			new Thread(this::readTXT).start();
			
		}
		catch (NullPointerException exception) { }
		catch (Exception exception) { AlertDialog.error("Não foi possível carregar o arquivo Sistac!"); }
	}
	
	/** Carrega a planilha de erros para o sistema */
	private void selecionaArquivoExcel() {
		
		inicializaLista();
		
		try {
			
			// Abrindo a GUI de seleção de arquivo
			retornoExcel = PhillFileUtils.loadFile("Selecione a planilha", Constants.FileFormat.XLSX, PhillFileUtils.OPEN_DIALOG, sugestao);
			
			// Atualizando o arquivo de sugestão (último diretório selecionado)
			sugestao = new File(retornoExcel.getParent());
			
			// Atualizando o nome do arquivo no textfield de seleção
			textErros.setText(retornoExcel.getName());
			
			// Atualizando as informações da tela e executando a leitura do arquivo
			updateInfo(MSG_LOAD_FILE);
			new Thread(this::readExcel).start();
			
		}
		catch (NullPointerException exception) { exception.printStackTrace(); }
		catch (Exception exception) { AlertDialog.error("Não foi possível carregar o arquivo Excel!"); }
	}
	
	/** Seleciona o arquivo de compilação do sistema */
	private void selecionaCompilacao() {
		
		try {
			
			// Abrindo a GUI de seleção de arquivo
			compilacao = PhillFileUtils.loadFile("Selecione o arquivo de compilação", Constants.FileFormat.BSF, PhillFileUtils.SAVE_DIALOG, sugestao);
			
			// Atualizando o arquivo de sugestão (último diretório selecionado)
			sugestao = new File(compilacao.getParent());
			
			// Atualizando o nome do arquivo no textfield de seleção
			textCompilacao.setText(compilacao.getName());
			
		} catch (NullPointerException exception) { exception.printStackTrace(); }
		
	}
	
	/** Constrói o edital */
	private void gerarVisualizacao() {
		
		try {
			
			dependenciaVisualizacao();				// Verificação de dependências
			updateInfo(MSG_LOAD_PDF);				// Atualização de infos na GUI
			new Thread(this::exportPDF).start();	// Visualização do relatório (edital)
			
		}
		catch (BlankFieldException | FileNotSelectedException exception) {
			AlertDialog.error(exception.getMessage());
		}
		
	}
	
	/************ Bloco de Processamento de Arquivos e Relatórios **************/
	
	/** Carrega a lista de retornos a partir de um arquivo CSV */
	private void readTXT() {
		
		try {
			
			SistacFile.readRetorno(listaRetornos, retornoSistac);
			Thread.sleep(2000);
			
		} catch (Exception exception) {
			exception.printStackTrace();
			AlertDialog.error("Falha ao carregar o arquivo de retorno do Sistac!");
		}
		finally {
			updateInfo(null,false);
		}
		
	}
	
	/** Carrega a planilha de erros */
	private void readExcel() {
		
		try {
			
			ExcelSheetReader.readRetorno(listaRetornos, retornoExcel);
			Thread.sleep(2000);
			
		} catch (Exception exception) {
			exception.printStackTrace();
			AlertDialog.error("Falha ao carregar o arquivo de retorno do Excel!");
		}
		finally {
			updateInfo(null,false);
		}
		
	}
	
	/** Prepara e exibe o edital com o resultado preliminar das solicitações de isenção */
	private void exportPDF() {
		
		try {
			
			// Preparando o cabeçalho do edital
			String cabecalho  = textCabecalho.getText().trim();
		
			// Ordenando a lista de retornos (deferidos primeiro)
			listaRetornos.sort();
		
			// Gerando a visualização do relatório e salvando o arquivo de compilação
			PDFExport.export(listaRetornos, cabecalho, "cu", Resultado.PRELIMINAR);
			Compilation.save(listaRetornos, compilacao);
			
			// Take it easy :)
			Thread.sleep(2000);
			
		} catch (Exception exception) {
			exception.printStackTrace();
			AlertDialog.error("Falha ao gerar visualização!");
		}
		finally {
			updateInfo(null,false);
		}
		
	}
	
	/************** Bloco de Métodos Auxiliares ********************************/	
	
	/** Inicializa a lista de retornos, caso ela não esteja inicializada */
	private void inicializaLista() {
		
		if (listaRetornos == null)
			listaRetornos = new ListaRetornos();
		
	}
	
	/** Verifica se todas as dependências para geração do edital foram satisfeitas */
	private void dependenciaVisualizacao() throws BlankFieldException,FileNotSelectedException {
		
		if (listaRetornos == null)
			throw new FileNotSelectedException("Selecione ao menos um arquivo de entrada!");
		
		if (textCabecalho.getText().trim().equals(""))
			throw new BlankFieldException("Informe o cabeçalho do edital!");
		
		if (compilacao == null)
			throw new FileNotSelectedException("Selecione o arquivo de compilação!");
		
	}
	
	/**************** Métodos para atualização da GUI *************************/
	
	/** Atualiza informações da GUI (redirecionamento) */
	private void updateInfo(String message, boolean visibility) {
		Runnable job = () -> swingInfoUpdate(message, visibility);
		SwingUtilities.invokeLater(job);
	}
	
	/** Atualiza informações da GUI (redirecionamento) */
	private void updateInfo(String message) {
		updateInfo(message, true);
	}
	
	/** Implementação da atualização de infos da GUI */
	private void swingInfoUpdate(String message, boolean visibility) {
		
		labelStatus.setText(message);
		labelStatus.setVisible(visibility);
		labelStatus.repaint();
		
	}
}
