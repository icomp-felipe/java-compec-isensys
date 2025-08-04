package compec.ufam.isensys.view;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.phill.libs.ResourceManager;
import com.phill.libs.i18n.PropertyBundle;
import com.phill.libs.ui.AlertDialog;
import com.phill.libs.ui.ESCDispose;
import com.phill.libs.ui.GraphicsHelper;
import com.phill.libs.ui.JPaintedPanel;

import compec.ufam.isensys.constants.Reviewed;

@Reviewed("2025-08-04")
/** Classe que exibe a tela inicial do software
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 4.0, 04/AGO/2025 */
public class TelaInicioIsensys extends JFrame {

	private static final long serialVersionUID = 6673738709226295401L;
	
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
		ESCDispose.register(this);
		
		Dimension dimension = new Dimension(490,355);
		JPanel painel = new JPaintedPanel("img/initial-screen.jpg", dimension);
		painel.setLayout(null);
		
		Icon iconEnvio   = ResourceManager.getIcon("img/botao-envio.png"     ,442,30);
		Icon iconPrelim  = ResourceManager.getIcon("img/botao-preliminar.png",442,30);
		Icon iconFinal   = ResourceManager.getIcon("img/botao-definitivo.png",442,30);
		Icon iconConfigs = ResourceManager.getIcon("img/botao-configs.png"   ,442,30);
		
		Icon isensysIcon  = ResourceManager.getIcon("img/isensys-logo.png",154,130);
		
		// Construindo janela
		JButton buttonEnvio = new JButton(iconEnvio);
		buttonEnvio.setToolTipText(bundle.getString("hint-button-envio"));
		buttonEnvio.setBorderPainted(false);
		buttonEnvio.setOpaque(false);
		buttonEnvio.setContentAreaFilled(false);
		buttonEnvio.addActionListener((_) -> new TelaEnvio());
		buttonEnvio.setBounds(20, 145, 442, 30);
		painel.add(buttonEnvio);
		
		JButton buttonRetornoPrelim = new JButton(iconPrelim);
		buttonRetornoPrelim.setToolTipText(bundle.getString("hint-button-retorno-prelim"));
		buttonRetornoPrelim.addActionListener((_) -> new TelaRetornoPreliminar());
		buttonRetornoPrelim.setBorderPainted(false);
		buttonRetornoPrelim.setOpaque(false);
		buttonRetornoPrelim.setContentAreaFilled(false);
		buttonRetornoPrelim.setBounds(20, 187, 442, 30);
		painel.add(buttonRetornoPrelim);
		
		JButton buttonRetornoFinal = new JButton(iconFinal);
		buttonRetornoFinal.setToolTipText(bundle.getString("hint-button-retorno-final"));
		buttonRetornoFinal.addActionListener((_) -> new TelaRetornoDefinitivo());
		buttonRetornoFinal.setBorderPainted(false);
		buttonRetornoFinal.setOpaque(false);
		buttonRetornoFinal.setContentAreaFilled(false);
		buttonRetornoFinal.setBounds(20, 229, 442, 30);
		painel.add(buttonRetornoFinal);
		
		JButton buttonConfigs = new JButton(iconConfigs);
		buttonConfigs.setToolTipText(bundle.getString("hint-button-configs"));
		buttonConfigs.addActionListener((_) -> new TelaConfigs());
		buttonConfigs.setBorderPainted(false);
		buttonConfigs.setOpaque(false);
		buttonConfigs.setContentAreaFilled(false);
		buttonConfigs.setBounds(20, 271, 442, 30);
		painel.add(buttonConfigs);
		
		JButton buttonEasterEgg = new JButton(isensysIcon);
		buttonEasterEgg.addActionListener((_) -> actionEasterEgg());
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
		AlertDialog.info(this, title, dialog);
		
	}
	
}
