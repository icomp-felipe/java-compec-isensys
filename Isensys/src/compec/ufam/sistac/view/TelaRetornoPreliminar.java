package compec.ufam.sistac.view;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import com.phill.libs.*;
import com.phill.libs.ui.*;

import compec.ufam.sistac.exception.*;
import compec.ufam.sistac.io.*;
import compec.ufam.sistac.pdf.*;
import compec.ufam.sistac.model.*;
import com.phill.libs.files.PhillFileUtils;

/** Classe que controla a view de processamento Retorno Preliminar
 *  @author Felipe André
 *  @version 2.50, 08/07/2018 */
public class TelaRetornoPreliminar extends JFrame {

	/****************** Bloco de Inicialização de Variáveis ********************/
	
	// Sempre coloco isso pra não dar warning :)
	private static final long serialVersionUID = 1L;
	
	// Mensagens de processamento (exibidas no rodapé) 
	private static final String MSG_LOAD_FILE = "Processando Arquivo";
	private static final String MSG_LOAD_PDF  = "Gerando Visualização";
	
	// Alguns componentes de texto da tela
	private final JTextField textCompilacao,textRetornoSistac,textRetornoExcel,textCabecalho;
	private final JLabel labelInfo;
	
	// Recursos de processamento
	private File retornoSistac,retornoExcel,compilacao,sugestao;
	private ListaRetornos listaRetornos;

	/******************* Bloco do Método Principal ******************************/
	
	public static void main(String[] args) {
		new TelaRetornoPreliminar();
	}
	
	/** Construtor da classe inicializando a view */
	public TelaRetornoPreliminar() {
		super("IsenSys: Retorno (Preliminar)");
		
		GraphicsHelper instance = GraphicsHelper.getInstance();
		GraphicsHelper.setFrameIcon(this,"icon/isensys-icon.png");
		
		Icon searchIcon  = ResourceManager.getIcon("icon/search.png",20,20);
		Icon exitIcon    = ResourceManager.getIcon("icon/exit.png",25,25);
		Icon exportIcon  = ResourceManager.getIcon("icon/report.png",25,25);
		
		Font  fonte = instance.getFont ();
		Color color = instance.getColor();
		Dimension d = new Dimension(500,380);
		
		JPanel painel = new JPaintedPanel("img/prelim-screen.jpg",d);
		setContentPane(painel);
		
		setSize(d);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		JPanel painelEntrada = new JPanel();
		painelEntrada.setOpaque(false);
		painelEntrada.setLayout(null);
		painelEntrada.setBorder(instance.getTitledBorder("Arquivos de Entrada"));
		painelEntrada.setBounds(12, 10, 476, 160);
		getContentPane().add(painelEntrada);
		
		JLabel labelRetornoSistac = new JLabel("Retorno Sistac:");
		labelRetornoSistac.setFont(fonte);
		labelRetornoSistac.setBounds(10, 30, 110, 20);
		painelEntrada.add(labelRetornoSistac);
		
		textRetornoSistac = new JTextField();
		textRetornoSistac.setForeground(color);
		textRetornoSistac.setFont(fonte);
		textRetornoSistac.setEditable(false);
		textRetornoSistac.setColumns(10);
		textRetornoSistac.setBounds(125, 30, 260, 25);
		painelEntrada.add(textRetornoSistac);
		
		JButton botaoRetornoSistac = new JButton(searchIcon);
		botaoRetornoSistac.setToolTipText("Buscar o arquivo de retorno do Sistac");
		botaoRetornoSistac.addActionListener((event) -> selecionaArquivoSistac());
		botaoRetornoSistac.setBounds(395, 30, 30, 25);
		painelEntrada.add(botaoRetornoSistac);
		
		JLabel labelRetornoExcel = new JLabel("Planilha Erros:");
		labelRetornoExcel.setFont(fonte);
		labelRetornoExcel.setBounds(10, 65, 110, 20);
		painelEntrada.add(labelRetornoExcel);
		
		textRetornoExcel = new JTextField();
		textRetornoExcel.setForeground(color);
		textRetornoExcel.setFont(fonte);
		textRetornoExcel.setEditable(false);
		textRetornoExcel.setColumns(10);
		textRetornoExcel.setBounds(125, 65, 260, 25);
		painelEntrada.add(textRetornoExcel);
		
		JButton botaoRetornoExcel = new JButton(searchIcon);
		botaoRetornoExcel.setToolTipText("Buscar a planilha de erros");
		botaoRetornoExcel.addActionListener((event) -> selecionaArquivoExcel());
		botaoRetornoExcel.setBounds(395, 65, 30, 25);
		painelEntrada.add(botaoRetornoExcel);
		
		JButton botaoRetornoSistac_1 = new JButton((Icon) null);
		botaoRetornoSistac_1.setToolTipText("Buscar o arquivo de retorno do Sistac");
		botaoRetornoSistac_1.setBounds(435, 30, 30, 25);
		painelEntrada.add(botaoRetornoSistac_1);
		
		JButton botaoRetornoSistac_1_1 = new JButton((Icon) null);
		botaoRetornoSistac_1_1.setToolTipText("Buscar o arquivo de retorno do Sistac");
		botaoRetornoSistac_1_1.setBounds(435, 65, 30, 25);
		painelEntrada.add(botaoRetornoSistac_1_1);
		
		JPanel painelSaida = new JPanel();
		painelSaida.setOpaque(false);
		painelSaida.setLayout(null);
		painelSaida.setBorder(instance.getTitledBorder("Edital de Saída"));
		painelSaida.setBounds(12, 170, 476, 65);
		getContentPane().add(painelSaida);
		
		JLabel labelCabecalho = new JLabel("Cabeçalho:");
		labelCabecalho.setFont(fonte);
		labelCabecalho.setBounds(10, 30, 80, 20);
		painelSaida.add(labelCabecalho);
		
		textCabecalho = new JTextField();
		textCabecalho.setToolTipText("Título que fará parte do cabeçalho do edital");
		textCabecalho.setForeground(color);
		textCabecalho.setFont(fonte);
		textCabecalho.setColumns(10);
		textCabecalho.setBounds(95, 30, 330, 25);
		painelSaida.add(textCabecalho);
		
		JButton botaoRetornoSistac_1_1_1 = new JButton((Icon) null);
		botaoRetornoSistac_1_1_1.setToolTipText("Buscar o arquivo de retorno do Sistac");
		botaoRetornoSistac_1_1_1.setBounds(435, 30, 30, 25);
		painelSaida.add(botaoRetornoSistac_1_1_1);
		
		JButton botaoSair = new JButton(exitIcon);
		botaoSair.setToolTipText("Sair do sistema");
		botaoSair.addActionListener((event) -> dispose());
		
		ImageIcon loading = new ImageIcon(ResourceManager.getResource("img/loader.gif"));
		
		labelInfo = new JLabel("Processando Arquivo",loading,SwingConstants.LEFT);
		labelInfo.setFont(fonte);
		labelInfo.setVisible(false);
		labelInfo.setBounds(12, 315, 215, 20);
		getContentPane().add(labelInfo);
		botaoSair.setBounds(406, 310, 35, 30);
		getContentPane().add(botaoSair);
		
		JPanel painelCompilacao = new JPanel();
		painelCompilacao.setOpaque(false);
		painelCompilacao.setLayout(null);
		painelCompilacao.setBorder(instance.getTitledBorder("Compilação"));
		painelCompilacao.setBounds(12, 235, 476, 65);
		getContentPane().add(painelCompilacao);
		
		JLabel labelCompilacao = new JLabel("Arquivo BSF:");
		labelCompilacao.setFont(fonte);
		labelCompilacao.setBounds(10, 30, 90, 20);
		painelCompilacao.add(labelCompilacao);
		
		textCompilacao = new JTextField();
		textCompilacao.setForeground(color);
		textCompilacao.setFont(fonte);
		textCompilacao.setEditable(false);
		textCompilacao.setColumns(10);
		textCompilacao.setBounds(105, 30, 280, 25);
		painelCompilacao.add(textCompilacao);
		
		JButton botaoCompilacao = new JButton(searchIcon);
		botaoCompilacao.setToolTipText("Escolher aonde salvar o arquivo de compilação");
		botaoCompilacao.addActionListener((event) -> selecionaCompilacao());
		botaoCompilacao.setBounds(395, 30, 30, 25);
		painelCompilacao.add(botaoCompilacao);
		
		JButton botaoCompilacao_1 = new JButton((Icon) null);
		botaoCompilacao_1.setToolTipText("Escolher aonde salvar o arquivo de compilação");
		botaoCompilacao_1.setBounds(435, 30, 30, 25);
		painelCompilacao.add(botaoCompilacao_1);
		
		JButton botaoAbrir = new JButton(exportIcon);
		botaoAbrir.setToolTipText("Gerar o edital");
		botaoAbrir.setBounds(453, 310, 35, 30);
		getContentPane().add(botaoAbrir);
		botaoAbrir.addActionListener((event) -> gerarVisualizacao());
		
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
			textRetornoSistac.setText(retornoSistac.getName());
			
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
			textRetornoExcel.setText(retornoExcel.getName());
			
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
		
		labelInfo.setText(message);
		labelInfo.setVisible(visibility);
		labelInfo.repaint();
		
	}
}
