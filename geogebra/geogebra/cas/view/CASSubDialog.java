package geogebra.cas.view;

import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Dialog to substitude a string in a CAS input.
 * 
 * Quan Yuan
 */

public class CASSubDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JButton btSub, btEval, btNumeric; //, btCancel;
	private JPanel optionPane, btPanel, cbPanel, captionPanel;
	private JTextField valueTextField, subStrfield, lastFocusTF;
	
	private CASView casView;
	private Application app;
	private int editRow;
	private String prefix, evalText, postifx;

	/**
	 * Substitute dialog for CAS.
	 */
	public CASSubDialog(CASView casView, String prefix, String evalText, String postfix, int editRow) {
		super(casView.getApp().getFrame());
		setModal(false);
		
		this.casView = casView;
		this.app = casView.getApp();
		this.prefix = prefix;
		this.evalText = evalText;
		this.postifx = postfix;
		
		this.editRow = editRow;
	
		createGUI();
		pack();
		setLocationRelativeTo(null);
	}

	protected void createGUI() {
		setTitle(app.getPlain("Substitute") + " - " + app.getCommand("Row") + " " + (editRow+1));
		setResizable(false);

		// create label panel
		JPanel subTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		String temp = app.getPlain("SubstituteForAinB",
				"ThisIsJustTheSplitString", evalText);
		String[] strLabel = temp.split("ThisIsJustTheSplitString");
		JLabel subLabel = new JLabel(strLabel[0]);
		subStrfield = new MyTextField(app.getGuiManager(),4);
		JLabel subLabel2 = new JLabel(strLabel[1]);
		subTitlePanel.add(subLabel);
		subTitlePanel.add(subStrfield);
		subTitlePanel.add(subLabel2);		

		// create caption panel
		JLabel captionLabel = new JLabel(app.getPlain("NewExpression") + ":");
		valueTextField = new JTextField();
		valueTextField.setColumns(20);
		captionLabel.setLabelFor(valueTextField);
		
		JPanel subPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		subPanel.add(captionLabel);
		subPanel.add(valueTextField);

		captionPanel = new JPanel(new BorderLayout(5, 5));
		captionPanel.add(subTitlePanel, BorderLayout.CENTER);
		captionPanel.add(subPanel, BorderLayout.SOUTH);

		// tf focus listener
		FocusListener myFocusListener = new FocusListener() {
			public void focusGained(FocusEvent e) {
				if (e.getSource() instanceof JTextField)
					lastFocusTF = (JTextField) e.getSource();
			}

			public void focusLost(FocusEvent e) {
			}
		};
		valueTextField.addFocusListener(myFocusListener);
		subStrfield.addFocusListener(myFocusListener);
		

		// buttons
		btEval = new JButton("=");
		btEval.setToolTipText(app.getCommand("Evaluate"));
		btEval.setActionCommand("Evaluate");
		btEval.addActionListener(this);
		
		btNumeric = new JButton("\u2248");
		btNumeric.setToolTipText(app.getCommand("Numeric"));
		btNumeric.setActionCommand("Numeric");
		btNumeric.addActionListener(this);
		
		btSub = new JButton(app.getPlain("\u2713"));
		btNumeric.setToolTipText(app.getCommand("Substitute"));
		btSub.setActionCommand("Substitute");
		btSub.addActionListener(this);

//		btCancel = new JButton(app.getPlain("Cancel"));
//		btCancel.setActionCommand("Cancel");
//		btCancel.addActionListener(this);
		btPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		btPanel.add(btEval);
		btPanel.add(btNumeric);
		btPanel.add(btSub);
		//btPanel.add(btCancel);

		// Create the JOptionPane.
		optionPane = new JPanel(new BorderLayout(5, 5));

		// create object list
		optionPane.add(captionPanel, BorderLayout.NORTH);
		
		optionPane.add(btPanel, BorderLayout.SOUTH);
		optionPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		// Make this dialog display it.
		setContentPane(optionPane);
	}

	public void actionPerformed(ActionEvent ae) {
		Object src = ae.getSource();

//		if (src == btCancel) {
//			setVisible(false);
//		} else 
		if (src == btEval) {
			if (apply(btEval.getActionCommand()))
				setVisible(false);
		} else if (src == btSub) {
			if (apply(btSub.getActionCommand()))
				setVisible(false);
		} else if (src == btNumeric) {
			if (apply(btNumeric.getActionCommand()))
				setVisible(false);
		}
	}
	
	public void setVisible(boolean flag) {
		casView.setSubstituteDialog(flag ? this : null);
		super.setVisible(flag);
	}
	
	public void insertText(String inStr) {
		if (lastFocusTF == null || inStr == null) return;
		
		lastFocusTF.replaceSelection(inStr);	
		lastFocusTF.requestFocus();
	}

	private boolean apply(String actionCommand) {
		
		CASTable table = casView.getConsoleTable();
			
		// substitute from
		String	fromExp = subStrfield.getText().trim();
		String toExp = valueTextField.getText().trim();
		if (fromExp.length() == 0 || toExp.length() == 0) return false;	
		
		// resolve row references
		fromExp = casView.resolveCASrowReferences(fromExp, editRow);
		toExp = casView.resolveCASrowReferences(toExp, editRow);
		
		// make sure pure substitute is not evaluated 
		boolean keepInput = true;
		
		// substitute command
		String subCmd = "Substitute[" + evalText + "," + fromExp + ", " +  toExp + "]"; 
		if (actionCommand.equals("Evaluate")) {
			subCmd = "Simplify[" + subCmd + "]"; 
			keepInput = false;
		}
		else if (actionCommand.equals("Numeric")) {
			subCmd = "Numeric[" + subCmd + "]";
			keepInput = false;
		}
	
		try {
			CASTableCellValue currCell = table.getCASTableCellValue(editRow);
			currCell.setProcessingInformation("", subCmd, "");
			currCell.setEvalCommand("Substitute");
			currCell.setEvalComment(fromExp + "=" + toExp);
			
			// make sure pure substitute is not evaluated 
			currCell.setKeepInputUsed(keepInput);
			
			casView.processRow(editRow);
			table.startEditingRow(editRow + 1);
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	
}