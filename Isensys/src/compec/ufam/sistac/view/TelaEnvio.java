package compec.ufam.sistac.view;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import com.phill.libs.*;
import com.phill.libs.ui.*;
import compec.ufam.sistac.io.*;
import com.phill.libs.exception.*;
import compec.ufam.sistac.model.*;

public class TelaEnvio extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public static final int INDEXES[] = new int[]{1,2,3,4,5,6,7,8,9};
	
	private JTextField textArquivoEntrada;
	private JTextField textSaidaSistac,textSaidaExcel;

	private final Color gr_dk = new Color(0x0d6b12);
	private final Color rd_dk = new Color(0xbc1742);
	
	private static final boolean PANEL_LOADING = true;
	private static final boolean PANEL_RESULTS = false;
	
	private JButton botaoArquivoEntrada;
	private JButton botaoSaidaSistac,botaoSaidaExcel;
	
	private JLabel labelProcessando;
	private JPanel painelSituacoes;
	
	private JLabel textOK,textErro,textTotal;
	
	private ParseResult listaResultados;
	private File saidaSistac, saidaExcel;
	private JButton botaoSair;
	private JButton botaoExportar;
	private JTextField textEdital;
	private JTextField textSequencia;
	
	private File sugestao, arquivoEntrada;
	
	private int sizeERR = 0;
	
	public static void main(String[] args) {
		new TelaEnvio();
	}

	public TelaEnvio() {
		
		super("IsenSys: Exportação");
		
		GraphicsHelper instance = GraphicsHelper.getInstance();
		GraphicsHelper.setFrameIcon(this,"icon/isensys-icon.png");
		
		Font  fonte = instance.getFont ();
		Color color = instance.getColor();
		Dimension d = new Dimension(460,340);
		
		JPanel painel = new JPaintedPanel("img/envio-screen.jpg",d);
		setContentPane(painel);
		
		setSize(d);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		painel.setLayout(null);
		
		Icon searchIcon  = ResourceManager.getResizedIcon("icon/search.png",20,20);
		Icon reloadIcon  = ResourceManager.getResizedIcon("icon/reload.png",20,20);
		Icon exitIcon    = ResourceManager.getResizedIcon("icon/exit.png",25,25);
		Icon exportIcon  = ResourceManager.getResizedIcon("icon/save.png",25,25);
		
		JPanel painelEntrada = new JPanel();
		painelEntrada.setOpaque(false);
		painelEntrada.setBorder(instance.getTitledBorder("Arquivo de Entrada"));
		painelEntrada.setBounds(12, 12, 436, 113);
		painel.add(painelEntrada);
		painelEntrada.setLayout(null);
		
		JLabel labelArquivoEntrada = new JLabel("Nome:");
		labelArquivoEntrada.setFont(fonte);
		labelArquivoEntrada.setBounds(12, 31, 61, 20);
		painelEntrada.add(labelArquivoEntrada);
		
		textArquivoEntrada = new JTextField();
		textArquivoEntrada.setEditable(false);
		textArquivoEntrada.setForeground(color);
		textArquivoEntrada.setFont(fonte);
		textArquivoEntrada.setBounds(67, 30, 273, 25);
		painelEntrada.add(textArquivoEntrada);
		textArquivoEntrada.setColumns(10);
		
		botaoArquivoEntrada = new JButton(searchIcon);
		botaoArquivoEntrada.setToolTipText("Busca o arquivo de entrada");
		botaoArquivoEntrada.addActionListener((event) -> carregaArquivoEntrada());
		
		JButton botaoReload = new JButton(reloadIcon);
		botaoReload.addActionListener((event) -> reload());
		botaoReload.setToolTipText("Recarrega o arquivo atual");
		botaoReload.setBounds(352, 30, 30, 25);
		painelEntrada.add(botaoReload);
		botaoArquivoEntrada.setBounds(394, 30, 30, 25);
		painelEntrada.add(botaoArquivoEntrada);
		
		ImageIcon loading = new ImageIcon(ResourceManager.getResource("img/loader.gif"));
		
		labelProcessando = new JLabel("Processando Arquivo",loading,SwingConstants.LEFT);
		labelProcessando.setVisible(false);
		labelProcessando.setFont(fonte);
		labelProcessando.setBounds(12, 72, 193, 15);
		painelEntrada.add(labelProcessando);
		
		painelSituacoes = new JPanel();
		painelSituacoes.setOpaque(false);
		painelSituacoes.setVisible(false);
		painelSituacoes.setBounds(12, 60, 416, 41);
		painelEntrada.add(painelSituacoes);
		painelSituacoes.setLayout(null);
		
		JLabel labelSituacoes = new JLabel("Solicitações:");
		labelSituacoes.setFont(fonte);
		labelSituacoes.setBounds(0, 12, 102, 15);
		painelSituacoes.add(labelSituacoes);
		
		textOK = new JLabel("0 (OK)",SwingConstants.CENTER);
		textOK.setForeground(gr_dk);
		textOK.setFont(fonte);
		textOK.setBounds(97, 12, 85, 15);
		painelSituacoes.add(textOK);
		
		textErro = new JLabel("0 (ERRO)", SwingConstants.CENTER);
		textErro.setForeground(rd_dk);
		textErro.setFont(fonte);
		textErro.setBounds(190, 12, 96, 15);
		painelSituacoes.add(textErro);
		
		textTotal = new JLabel("0 (TOTAL)", SwingConstants.RIGHT);
		textTotal.setForeground(color);
		textTotal.setFont(fonte);
		textTotal.setBounds(302, 12, 102, 15);
		painelSituacoes.add(textTotal);
		
		JPanel painelSaida = new JPanel();
		painelSaida.setOpaque(false);
		painelSaida.setBorder(instance.getTitledBorder("Arquivos de Saída"));
		painelSaida.setBounds(12, 129, 436, 130);
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
		
		JLabel labelSaidaSistac = new JLabel("Arquivo Sistac:");
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
		
		JLabel labelSaidaExcel = new JLabel("Planilha Erros:");
		labelSaidaExcel.setFont(fonte);
		labelSaidaExcel.setBounds(12, 94, 118, 15);
		painelSaida.add(labelSaidaExcel);
		
		textSaidaExcel = new JTextField();
		textSaidaExcel.setFont(fonte);
		textSaidaExcel.setForeground(color);
		textSaidaExcel.setEditable(false);
		textSaidaExcel.setColumns(10);
		textSaidaExcel.setBounds(130, 91, 252, 25);
		painelSaida.add(textSaidaExcel);
		
		botaoSaidaExcel = new JButton(searchIcon);
		botaoSaidaExcel.setToolTipText("Escolher aonde será salva a planilha de erros. Lembrando que esta é opcional caso não haja erros");
		botaoSaidaExcel.addActionListener((event) -> selecionaSaidaExcel());
		botaoSaidaExcel.setBounds(394, 91, 30, 25);
		painelSaida.add(botaoSaidaExcel);
		
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
		
		setVisible(true);
		
	}

	/** Escreve o diretório completo até o arquivo informado */
	private void atualizaSugestao(File arquivo) {
		this.sugestao = new File(arquivo.getParent());
	}
	
	
	/** Carrega o arquivo de entrada de dados e atualiza as informações da janela */
	private void carregaArquivoEntrada() {
		
		try {
			
			arquivoEntrada  = FileChooserHelper.loadFile(this, FileFilters.SISTAC_INPUT, "Selecione o arquivo de entrada", false, sugestao);
			
			atualizaSugestao(arquivoEntrada);
			textArquivoEntrada.setText(arquivoEntrada.getName());
			
			switchPanels(PANEL_LOADING);
			
			Runnable job = () -> loadSheet();
			new Thread(job).start();
			
			
		} catch (Exception exception) {
			return;
		}
		
	}
	
	/** Alterna entre os painéis de processamento de arquivo e de resultados */
	private void switchPanels(boolean panel) {
		
		painelSituacoes .setVisible(!panel);
		labelProcessando.setVisible(panel);
		
	}
	
	/** Recarrega o arquivo de entrada */
	private void reload() {
		
		if (arquivoEntrada == null)
			AlertDialog.error("Selecione o arquivo de entrada");
		else {
			Runnable job = () -> loadSheet();
			new Thread(job).start();
		}
		
	}
	
	/** Carrega os dados de entrada para o sistema */
	private void loadSheet() {
		
		try {
			
			switchPanels(PANEL_LOADING);
			
			// melhorar esses IFs
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
			textArquivoEntrada.setText (null );
			labelProcessando.setVisible(false);
		}
		
	}
	
	/** Atualiza os totais de candidatos processados */
	private void updateStatistics() {
		
		    sizeERR = listaResultados.getListaExcecoes  ().size();
		int sizeOK  = listaResultados.getListaCandidatos().size();
		int sizeALL = (sizeOK + sizeERR);
		
		textOK   .setText(sizeOK  + " (OK)"   );
		textErro .setText(sizeERR + " (ERRO)" );
		textTotal.setText(sizeALL + " (TOTAL)");
		
		switchPanels(PANEL_RESULTS);
		
	}
	
	/** Seleciona e monta, com base nos dados de entrada, o nome do arquivo sistac */
	private void selecionaSaidaSistac() {
		
		try {
			
			dependenciaSistac();
			
			File sugestao = SistacFile.getSistacExportName(this.sugestao, textEdital.getText(), textSequencia.getText());
			
			saidaSistac = FileChooserHelper.loadFile(TelaEnvio.this, FileFilters.SISTAC_SEND, "Selecione o arquivo de saída", true, sugestao);
			textSaidaSistac.setText(saidaSistac.getName());
			
		} catch (BlankFieldException exception) {
			AlertDialog.error(exception.getMessage());
		} catch (Exception exception) { }
		
	}
	
	/** Verifica se as dependências para a montagem do nome do arquivo sistac estão satisfeitas */
	private void dependenciaSistac() throws BlankFieldException {
		
		if (textEdital.getText().trim().equals(""))
			throw new BlankFieldException("Informe o Edital!");
		
		if (textSequencia.getText().trim().equals(""))
			throw new BlankFieldException("Informe a Sequência!");
		
	}
	
	/** Seleciona o arquivo de saída da planilha de erros em formato excel */
	private void selecionaSaidaExcel() {
		
		try {
			
			saidaExcel = FileChooserHelper.loadFile(TelaEnvio.this, FileFilters.XLSX, "Selecione o arquivo de saída", true, sugestao);
			
			atualizaSugestao(saidaExcel);
			textSaidaExcel.setText(saidaExcel.getName());
			
		} catch (Exception exception) { }
		
	}
	
	/** Exporta os arquivos de envio do sistac e/ou a planilha de erros no formato excel.
	 *  Obs.: a exportação da planilha é opcional quando não há erros de processamento */
	private void exportarArquivos() {
		
		try {
			
			dependenciaExportacao();
			
			ExcelSheetWriter.write(listaResultados.getListaExcecoes(), saidaExcel);
			SistacFile.generate(listaResultados.getListaCandidatos(), saidaSistac);
			
			AlertDialog.info("Arquivo(s) exportado(s) com sucesso!");
			
		} catch (Exception exception) {
			AlertDialog.error(exception.getMessage());
		}
		
	}
	
	/** Verifica se todas os requisitos para a exportação foram devidamente satisfeitos */
	private void dependenciaExportacao() throws FileNotSelectedException {
		
		if (listaResultados == null)
			throw new FileNotSelectedException("Selecione o arquivo de entrada de dados!");
		
		if (saidaSistac == null)
			throw new FileNotSelectedException("Selecione o arquivo de exportação do Sistac!");
		
		if ((saidaExcel == null) && (sizeERR != 0))
			throw new FileNotSelectedException("Selecione o arquivo de exportação do Excel!");
		
	}
	
}
