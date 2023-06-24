package compec.ufam.isensys.view;

import java.io.*;
import java.awt.*;

import javax.swing.*;

import com.phill.libs.*;
import com.phill.libs.br.*;
import com.phill.libs.ui.*;
import com.phill.libs.i18n.*;
import com.phill.libs.table.*;
import com.phill.libs.mfvapi.*;

import compec.ufam.isensys.io.*;
import compec.ufam.isensys.model.*;
import compec.ufam.isensys.constants.*;

/** Implementa a tela de ajustes de configurações do sistema.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.8, 21/JUN/2023 */
public class TelaConfigs extends JFrame {

	// Serial
	private static final long serialVersionUID = 6100739943641167028L;

	// Carregando bundle de idiomas
	private final static PropertyBundle bundle = new PropertyBundle("i18n/tela-configs", null);
	
	// Declaração de atributos gráficos
	private final CNPJTextField textCNPJ;
	private final JTextField textNomeFantasia, textRazaoSocial;
	private final JTable tableSheetIndex;
	private final PositiveIntegerTableModel modelo;
	
	// MFV API
	private final MandatoryFieldsManager fieldValidator;
	private final MandatoryFieldsLogger  fieldLogger;
	
	public TelaConfigs() {
		
		// Recuperando o título da janela
		setTitle(bundle.getString("configs-window-title"));
		
		// Inicializando atributos gráficos
		GraphicsHelper instance = GraphicsHelper.getInstance();
		GraphicsHelper.setFrameIcon(this,"icon/isensys-icon.png");
		ESCDispose.register(this);
		
		Dimension dimension = new Dimension(1024,275);
		
		JPanel painel = new JPaintedPanel("img/envio-screen.jpg",dimension);
		painel.setLayout(null);
		setContentPane(painel);
		
		// Recuperando ícones
		Icon saveIcon   = ResourceManager.getIcon("icon/save.png"  , 25, 25);
		Icon reloadIcon = ResourceManager.getIcon("icon/reload.png", 25, 25);
		
		// Recuperando fontes e cores
		Font  fonte = instance.getFont ();
		Color color = instance.getColor();
		
		// Painel 'Dados da Instituição'
		JPanel panelInstituicao = new JPanel();
		panelInstituicao.setOpaque(false);
		panelInstituicao.setLayout(null);
		panelInstituicao.setBorder(instance.getTitledBorder("Dados da Instituição"));
		panelInstituicao.setBounds(10, 10, 990, 105);
		painel.add(panelInstituicao);
						
		JLabel labelCNPJ = new JLabel("CNPJ:");
		labelCNPJ.setHorizontalAlignment(JLabel.RIGHT);
		labelCNPJ.setFont(fonte);
		labelCNPJ.setBounds(10, 25, 115, 20);
		panelInstituicao.add(labelCNPJ);
						
		textCNPJ = new CNPJTextField();
		textCNPJ.setFont(fonte);
		textCNPJ.setForeground(color);
		textCNPJ.setHorizontalAlignment(JTextField.CENTER);
		textCNPJ.setBounds(130, 25, 170, 20);
		panelInstituicao.add(textCNPJ);
						
		JLabel labelNomeFantasia = new JLabel("Nome Fantasia:");
		labelNomeFantasia.setHorizontalAlignment(JLabel.RIGHT);
		labelNomeFantasia.setFont(fonte);
		labelNomeFantasia.setBounds(10, 50, 115, 20);
		panelInstituicao.add(labelNomeFantasia);
						
		textNomeFantasia = new JTextFieldBounded(100);
		textNomeFantasia.setToolTipText(bundle.getString("hint-text-nome-fantasia"));
		textNomeFantasia.setFont(fonte);
		textNomeFantasia.setForeground(color);
		textNomeFantasia.setBounds(130, 50, 850, 20);
		panelInstituicao.add(textNomeFantasia);
						
		JLabel labelRazaoSocial = new JLabel("Razão Social:");
		labelRazaoSocial.setHorizontalAlignment(JLabel.RIGHT);
		labelRazaoSocial.setFont(fonte);
		labelRazaoSocial.setBounds(10, 75, 115, 20);
		panelInstituicao.add(labelRazaoSocial);
						
		textRazaoSocial = new JTextFieldBounded(100);
		textRazaoSocial.setToolTipText(bundle.getString("hint-text-razao-social"));
		textRazaoSocial.setFont(fonte);
		textRazaoSocial.setForeground(color);
		textRazaoSocial.setBounds(130, 75, 850, 20);
		panelInstituicao.add(textRazaoSocial);
		
		// Painel 'Índices da Planilha de Importação'
		JPanel panelSheetIndex = new JPanel();
		panelSheetIndex.setOpaque(false);
		panelSheetIndex.setLayout(null);
		panelSheetIndex.setBorder(instance.getTitledBorder("Índices da Planilha de Importação"));
		panelSheetIndex.setBounds(10, 115, 990, 80);
		painel.add(panelSheetIndex);
		
		JScrollPane scrollSheetindex = new JScrollPane();
		scrollSheetindex.setOpaque(false);
		scrollSheetindex.getViewport().setOpaque(false);
		scrollSheetindex.setBounds(10, 30, 970, 40);
		panelSheetIndex.add(scrollSheetindex);
		
		// Inicializando tabela
		modelo = new PositiveIntegerTableModel(Constants.SheetIndex.IMPORT_COLUMN_TITLES);
		
		tableSheetIndex = new JTable(modelo);
		tableSheetIndex .setOpaque(false);
		tableSheetIndex.setFont(fonte);
		tableSheetIndex.setForeground(color);
		tableSheetIndex.getTableHeader().setFont(instance.getFont(11));
		scrollSheetindex.setViewportView(tableSheetIndex);
		
		JButton buttonReload = new JButton(reloadIcon);
		buttonReload.setToolTipText(bundle.getString("hint-button-reload"));
		buttonReload.addActionListener((event) -> reload());
		buttonReload.setBounds(920, 200, 35, 30);
		painel.add(buttonReload);
		
		JButton buttonSave = new JButton(saveIcon);
		buttonSave.setToolTipText(bundle.getString("hint-button-save"));
		buttonSave.addActionListener((event) -> save());
		buttonSave.setBounds(965, 200, 35, 30);
		painel.add(buttonSave);
		
		// Definindo validação de dados das células
		final TableCellValidator validator = (cellData) -> ((cellData == null) || !(cellData instanceof Integer)) ? false : (int) cellData >= 0;
		
		final ValidatorCellRenderer cellRenderer = new ValidatorCellRenderer(validator, Color.WHITE, new Color(0xEF8E84));
		cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		
		for (int i=0; i<Constants.SheetIndex.IMPORT_COLUMN_TITLES.length; i++)
			tableSheetIndex.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
		
		// Cadastrando validação de campos
		this.fieldValidator = new MandatoryFieldsManager();
		this.fieldLogger    = new MandatoryFieldsLogger ();
		
		fieldValidator.addPermanent(labelCNPJ        , () -> textCNPJ.valido()                    , bundle.getString("configs-mfv-cnpj"    ), false);
		fieldValidator.addPermanent(labelNomeFantasia, () -> parseNome(textNomeFantasia.getText()), bundle.getString("configs-mfv-fantasia"), false);
		fieldValidator.addPermanent(labelRazaoSocial , () -> parseNome(textRazaoSocial .getText()), bundle.getString("configs-mfv-razao"   ), false);
		fieldValidator.addPermanent(new JLabel()     , () -> parseIndices()                       , bundle.getString("configs-mfv-table"   ), false);
		
		// Carregando configurações
		load();
		
		// Mostrando a janela
		setSize(dimension);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		
	}
	
	/********************** Bloco de Métodos Verificadores ********************************/
	
	/** Verifica se um determinado nome possui apenas caracteres alfanuméricos e espaços (formato Sistac).
	 *  @param nome - nome a ser verificado
	 *  @return 'true' apenas se <code>nome</code> possui caracteres alfanuméricos e espaços;<br>'false' caso contrário. */
	private boolean parseNome(final String nome) {
		return StringUtils.isAlphanumericStringOnly(nome.trim(), false);
	}
	
	/** Verifica se todos os índices da tabela estão preenchidos.
	 *  @return Estado de preenchimento de todos os índices da tabela. */
	private boolean parseIndices() {
		
		for (int i=0; i<Constants.SheetIndex.IMPORT_COLUMN_TITLES.length; i++)
			if (tableSheetIndex.getValueAt(0,i) == null)
				return false;
		
		return true;
	}
	
	/************************* Bloco de Métodos  de I/O ***********************************/
	
	/** Carrega o arquivo de configurações do sistema e atualiza a tela com seus dados. */
	private void load() {
		
		// Limpando a tabela
		TableUtils.clear(modelo);
		
		try {
			
			// Recuperando objeto de configurações
			final Configs configs   = SystemConfigs.retrieve();
			Instituicao instituicao = configs.getInstituicao();
			
			// Atualizando campos de texto
			textCNPJ        .setValue(instituicao.getCNPJ        ());
			textNomeFantasia.setText (instituicao.getNomeFantasia());
			textRazaoSocial .setText (instituicao.getRazaoSocial ());
			
			// Atualizando a tabela
			TableUtils.add(modelo, () -> configs.getIndicesTabela());
			
		}
		catch (FileNotFoundException exception) {
			
			AlertDialog.warning(this, bundle.getString("configs-load-title"),
		                              bundle.getString("configs-load-new-file"));
			
		}
		catch (IOException exception) {
			
			exception.printStackTrace();
			AlertDialog.error(this, bundle.getString("configs-load-title"),
                                    bundle.getString("configs-load-io-error"));
			
		}
		catch (Exception exception) {
			
			exception.printStackTrace();
			AlertDialog.error(this, bundle.getString("configs-load-title"),
                                    bundle.getString("configs-load-io-error"));
			
		}
		finally {
			
			// Caso haja alguma exceção, inicializa a tabela
			if (modelo.getRowCount() == 0)
				modelo.addRow(new Object[9]);
			
		}
		
	}
	
	/** Recarrega o arquivo de configurações do sistema e atualiza a tela com seus dados. */
	private void reload() {
		
		final String title  = bundle.getString("configs-reload-title");
		final String dialog = bundle.getString("configs-reload-dialog");
		
		if (AlertDialog.dialog(this, title, dialog) == AlertDialog.OK_OPTION)
			load();
		
	}
	
	/** Salva as configurações no arquivo. */
	private void save() {

		// Realizando validação dos campos antes de prosseguir
		fieldValidator.validate(fieldLogger);
							
		// Só prossigo se todas os campos foram devidamente preenchidos
		if (fieldLogger.hasErrors()) {
								
			AlertDialog.warning(this, bundle.getString         ("configs-save-title"),
					                  bundle.getFormattedString("configs-save-mfv-error", fieldLogger.getErrorString()));
			fieldLogger.clear(); return;
								
		}
		
		try {
		
			// Recuperando textos
			final String cnpj  = textCNPJ        .getValue().toString();
			final String nome  = textNomeFantasia.getText ().trim();
			final String razao = textRazaoSocial .getText ().trim();
			
			Instituicao instituicao = new Instituicao(cnpj, nome, razao);
		
			// Recuperando índices da tabela
			final int[] indices = new int[Constants.SheetIndex.IMPORT_COLUMN_TITLES.length];
			
			for (int i=0; i<Constants.SheetIndex.IMPORT_COLUMN_TITLES.length; i++)
				indices[i] = (int) tableSheetIndex.getValueAt(0,i);
			
			// Preparando objeto de configurações
			final Configs configs = new Configs(instituicao, indices);
			
			// Salvando configurações no disco
			SystemConfigs.save(configs);
		
			// Exibindo mensagem de sucesso
			AlertDialog.info(this, bundle.getString("configs-save-title"), bundle.getString("configs-save-success"));
			
		}
		catch (Exception exception) {
			
			exception.printStackTrace();
			AlertDialog.error(this, bundle.getString("configs-save-title"), bundle.getString("configs-save-error"));
			
		}
		
	}
}