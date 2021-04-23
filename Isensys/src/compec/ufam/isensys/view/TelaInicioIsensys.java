package compec.ufam.isensys.view;

import java.awt.*;
import javax.swing.*;
import com.phill.libs.*;
import com.phill.libs.ui.*;
import com.phill.libs.i18n.*;

/** Classe que exibe a tela inicial do software
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.5, 23/04/2021 */
public class TelaInicioIsensys extends JFrame {

	private static final long serialVersionUID = -6673738709226295401L;
	
	// Carregando bundle de idiomas
	private final static PropertyBundle bundle = new PropertyBundle("i18n/tela-inicio-isensys", null);

	/** Método main */
	public static void main(String[] args) {
		new TelaInicioIsensys();
	}
	
	/** Inicialização da view */
	public TelaInicioIsensys() {
		
		// Recuperando o título da janela
		setTitle(bundle.getString("inicio-window-title"));
				
		// Carregando atributos gráficos
		GraphicsHelper instance = GraphicsHelper.getInstance();
		GraphicsHelper.setFrameIcon(this,"icon/isensys-icon.png");
		
		Dimension dimension = new Dimension(475,310);
		JPanel painel = new JPaintedPanel("img/initial-screen.jpg", dimension);
		painel.setLayout(null);
		
		Icon iconEnvio  = ResourceManager.getIcon("img/botao-envio.png"     ,442,30);
		Icon iconPrelim = ResourceManager.getIcon("img/botao-preliminar.png",442,30);
		Icon iconFinal  = ResourceManager.getIcon("img/botao-final.png"     ,442,30);
		
		Icon isensysIcon  = ResourceManager.getIcon("img/isensys-logo.png",154,130);
		
		// Construindo janela
		JButton buttonEnvio = new JButton(iconEnvio);
		buttonEnvio.setToolTipText(bundle.getString("hint-button-envio"));
		buttonEnvio.setBorderPainted(false);
		buttonEnvio.setOpaque(false);
		buttonEnvio.setContentAreaFilled(false);
		buttonEnvio.addActionListener((event) -> new TelaEnvio());
		buttonEnvio.setBounds(20, 145, 442, 30);
		painel.add(buttonEnvio);
		
		JButton buttonRetornoPrelim = new JButton(iconPrelim);
		buttonRetornoPrelim.setToolTipText(bundle.getString("hint-button-retorno-prelim"));
		buttonRetornoPrelim.addActionListener((event) -> new TelaRetornoPreliminar());
		buttonRetornoPrelim.setBorderPainted(false);
		buttonRetornoPrelim.setOpaque(false);
		buttonRetornoPrelim.setContentAreaFilled(false);
		buttonRetornoPrelim.setBounds(20, 187, 442, 30);
		painel.add(buttonRetornoPrelim);
		
		JButton buttonRetornoFinal = new JButton(iconFinal);
		buttonRetornoFinal.setToolTipText(bundle.getString("hint-button-retorno-final"));
		buttonRetornoFinal.addActionListener((event) -> new TelaRetornoFinal());
		buttonRetornoFinal.setBorderPainted(false);
		buttonRetornoFinal.setOpaque(false);
		buttonRetornoFinal.setContentAreaFilled(false);
		buttonRetornoFinal.setBounds(20, 229, 442, 30);
		painel.add(buttonRetornoFinal);
		
		JButton buttonEasterEgg = new JButton(isensysIcon);
		buttonEasterEgg.addActionListener((event) -> actionEasterEgg());
		buttonEasterEgg.setBorderPainted(false);
		buttonEasterEgg.setOpaque(false);
		buttonEasterEgg.setContentAreaFilled(false);
		buttonEasterEgg.setBounds(308, 12, 154, 121);
		painel.add(buttonEasterEgg);
		
		JLabel labelInfos = new JLabel(bundle.getString("inicio-panel-info"));
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
	private void actionEasterEgg() {
		
		// Carregando texto i18n
		final String title  = bundle.getString("inicio-button-help-title");
		final String dialog = bundle.getString("inicio-button-help-dialog");
		
		// Exibindo notas de versão na UI
		AlertDialog.info(title,dialog);
		
	}
	
}
