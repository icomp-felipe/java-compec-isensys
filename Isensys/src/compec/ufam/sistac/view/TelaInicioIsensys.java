package compec.ufam.sistac.view;

import java.awt.*;
import javax.swing.*;
import com.phill.libs.*;
import com.phill.libs.ui.*;
import com.phill.libs.i18n.*;

/** Classe que exibe a tela inicial do software
 *  @author Felipe André
 *  @version 2.7, 08/04/2021 */
public class TelaInicioIsensys extends JFrame {

	private static final long serialVersionUID = -6673738709226295401L;
	
	// Carregando bundle de idiomas
	private final PropertyBundle bundle = new PropertyBundle("i18n/titles", null);;

	/** Função principal */
	public static void main(String[] args) {
		new TelaInicioIsensys();
	}

	/** Inicialização da view */
	public TelaInicioIsensys() {
		super("IsenSys v.2.7");
				
		// Carregando atributos gráficos
		GraphicsHelper instance = GraphicsHelper.getInstance();
		GraphicsHelper.setFrameIcon(this,"icon/isensys-icon.png");
		
		Dimension dimension = new Dimension(475,310);
		JPanel painel = new JPaintedPanel("img/initial-screen.jpg", dimension);
		painel.setLayout(null);
		
		Icon iconEnvio  = ResourceManager.getIcon("img/botao-envio.png",442,30);
		Icon iconPrelim = ResourceManager.getIcon("img/botao-preliminar.png",442,30);
		Icon iconFinal  = ResourceManager.getIcon("img/botao-final.png",442,30);
		
		// Construindo janela
		JButton botaoEnvio = new JButton(iconEnvio);
		botaoEnvio.setBorderPainted(false);
		botaoEnvio.setOpaque(false);
		botaoEnvio.setContentAreaFilled(false);
		botaoEnvio.addActionListener((event) -> new TelaEnvio());
		botaoEnvio.setBounds(20, 145, 442, 30);
		painel.add(botaoEnvio);
		
		JButton botaoRetornoPrelim = new JButton(iconPrelim);
		botaoRetornoPrelim.addActionListener((event) -> new TelaRetornoPreliminar());
		botaoRetornoPrelim.setBorderPainted(false);
		botaoRetornoPrelim.setOpaque(false);
		botaoRetornoPrelim.setContentAreaFilled(false);
		botaoRetornoPrelim.setBounds(20, 187, 442, 30);
		painel.add(botaoRetornoPrelim);
		
		JButton botaoRetornoFinal = new JButton(iconFinal);
		botaoRetornoFinal.addActionListener((event) -> new TelaRetornoFinal());
		botaoRetornoFinal.setBorderPainted(false);
		botaoRetornoFinal.setOpaque(false);
		botaoRetornoFinal.setContentAreaFilled(false);
		botaoRetornoFinal.setBounds(20, 229, 442, 30);
		painel.add(botaoRetornoFinal);
		
		Icon isensys_icon  = ResourceManager.getIcon("img/isensys-logo.png",154,130);
		
		JButton botaoAjuda = new JButton(isensys_icon);
		botaoAjuda.addActionListener((event) -> function_help());
		botaoAjuda.setBorderPainted(false);
		botaoAjuda.setOpaque(false);
		botaoAjuda.setContentAreaFilled(false);
		botaoAjuda.setBounds(308, 12, 154, 121);
		painel.add(botaoAjuda);
		
		// Carregando texto i18n
		final String text_info = bundle.getString("inicio-panel-info");
		
		JLabel labelInfos = new JLabel(text_info);
		labelInfos.setFont(instance.getFont(20));
		labelInfos.setBounds(20, 12, 270, 121);
		painel.add(labelInfos);
		
		setSize(dimension);
		setContentPane(painel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
		
	}
	
	/** Exibe um dialog com a ajuda do sistema */
	private void function_help() {
		
		// Carregando texto i18n
		final String inicio_button_help = bundle.getString("inicio-button-help");
		
		AlertDialog.info(inicio_button_help);
	}
	
}
