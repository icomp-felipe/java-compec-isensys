package compec.ufam.sistac.view;

import java.awt.*;
import javax.swing.*;

import com.phill.libs.ResourceManager;
import com.phill.libs.ui.GraphicsHelper;

public class TelaEdicao extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField textPesquisa;

	public static void main(String[] args) {
		new TelaEdicao();
	}

	public TelaEdicao() {
		super("Tela de Edição");
		
		GraphicsHelper instance = GraphicsHelper.getInstance();
		
		Font  fonte = instance.getFont ();
		Color color = instance.getColor();
		
		Icon clearIcon  = ResourceManager.getIcon("icon/clear.png",20,20);
		
		Dimension d = new Dimension(640,480);
		
		setSize(d);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		JPanel painelFiltros = new JPanel();
		painelFiltros.setBorder(instance.getTitledBorder("Filtros"));
		painelFiltros.setBounds(12, 12, 616, 101);
		getContentPane().add(painelFiltros);
		painelFiltros.setLayout(null);
		
		JLabel labelPesquisa = new JLabel("Pesquisa:");
		labelPesquisa.setFont(fonte);
		labelPesquisa.setBounds(12, 22, 76, 20);
		painelFiltros.add(labelPesquisa);
		
		textPesquisa = new JTextField();
		textPesquisa.setFont(fonte);
		textPesquisa.setForeground(color);
		textPesquisa.setBounds(95, 20, 467, 25);
		painelFiltros.add(textPesquisa);
		textPesquisa.setColumns(10);
		
		JButton botaoLimpa = new JButton(clearIcon);
		botaoLimpa.addActionListener((event) -> { textPesquisa.setText(null); textPesquisa.requestFocus(); });
		botaoLimpa.setBounds(574, 20, 30, 25);
		painelFiltros.add(botaoLimpa);
		
		JLabel labelOrdena = new JLabel("Ordenar por:");
		labelOrdena.setFont(fonte);
		labelOrdena.setBounds(12, 60, 111, 20);
		painelFiltros.add(labelOrdena);
		
		JRadioButton radioAlfabetica = new JRadioButton("Ordem Alfabética");
		radioAlfabetica.setFont(fonte);
		radioAlfabetica.setBounds(115, 60, 152, 23);
		painelFiltros.add(radioAlfabetica);
		
		JRadioButton radioErros = new JRadioButton("Erros Primeiro");
		radioErros.setSelected(true);
		radioErros.setFont(fonte);
		radioErros.setBounds(280, 60, 144, 23);
		painelFiltros.add(radioErros);
		
		ButtonGroup grupo = new ButtonGroup();
		grupo.add(radioAlfabetica);
		grupo.add(radioErros);
		
		JPanel painelEntradas = new JPanel();
		painelEntradas.setBorder(instance.getTitledBorder("Entradas"));
		painelEntradas.setBounds(12, 125, 616, 294);
		getContentPane().add(painelEntradas);
		painelEntradas.setLayout(null);
		
		JScrollPane scrollEntradas = new JScrollPane();
		scrollEntradas.setBounds(12, 23, 592, 259);
		painelEntradas.add(scrollEntradas);
		
		onCreateOptionsMenu();
		
		setVisible(true);
	}

	private void onCreateOptionsMenu() {
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu menuArquivo = new JMenu("Arquivo");
		menuBar.add(menuArquivo);
		
		JMenuItem itemArquivoSalvar = new JMenuItem("Salvar");
		itemArquivoSalvar.addActionListener((event) -> salvar());
		menuArquivo.add(itemArquivoSalvar);
		
		JMenuItem itemArquivoSair = new JMenuItem("Sair");
		itemArquivoSair.addActionListener((event) -> sair());
		menuArquivo.add(itemArquivoSair);
		
		setJMenuBar(menuBar);
		
	}
	
	private void salvar() {
		
	}
	
	private void sair() {
		dispose();
	}
	
}
