package compec.ufam.sistac.view;

import java.awt.*;
import javax.swing.*;
import com.phill.libs.*;
import com.phill.libs.ui.*;
import com.phill.libs.i18n.*;

/** Classe que exibe a tela inicial do software
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 2.9, 11/04/2021 */
public class TelaInicioIsensys extends JFrame {

	private static final long serialVersionUID = -6673738709226295401L;
	
	// Carregando bundle de idiomas
	private final static PropertyBundle bundle = new PropertyBundle("i18n/tela-inicio-isensys", null);

	/** Função principal */
	public static void main(String[] args) {
		new TelaInicioIsensys();
	}

	/** Inicialização da view */
	public TelaInicioIsensys() {
		super(bundle.getString("inicio-window-title"));
				
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
		JButton buttonEnvio = new JButton(iconEnvio);
		buttonEnvio.setBorderPainted(false);
		buttonEnvio.setOpaque(false);
		buttonEnvio.setContentAreaFilled(false);
		buttonEnvio.addActionListener((event) -> new TelaEnvio());
		buttonEnvio.setBounds(20, 145, 442, 30);
		painel.add(buttonEnvio);
		
		JButton buttonRetornoPrelim = new JButton(iconPrelim);
		buttonRetornoPrelim.addActionListener((event) -> new TelaRetornoPreliminar());
		buttonRetornoPrelim.setBorderPainted(false);
		buttonRetornoPrelim.setOpaque(false);
		buttonRetornoPrelim.setContentAreaFilled(false);
		buttonRetornoPrelim.setBounds(20, 187, 442, 30);
		painel.add(buttonRetornoPrelim);
		
		JButton buttonRetornoFinal = new JButton(iconFinal);
		buttonRetornoFinal.addActionListener((event) -> new TelaRetornoFinal());
		buttonRetornoFinal.setBorderPainted(false);
		buttonRetornoFinal.setOpaque(false);
		buttonRetornoFinal.setContentAreaFilled(false);
		buttonRetornoFinal.setBounds(20, 229, 442, 30);
		painel.add(buttonRetornoFinal);
		
		Icon isensys_icon  = ResourceManager.getIcon("img/isensys-logo.png",154,130);
		
		JButton buttonAjuda = new JButton(isensys_icon);
		buttonAjuda.addActionListener((event) -> function_help());
		buttonAjuda.setBorderPainted(false);
		buttonAjuda.setOpaque(false);
		buttonAjuda.setContentAreaFilled(false);
		buttonAjuda.setBounds(308, 12, 154, 121);
		painel.add(buttonAjuda);
		
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
