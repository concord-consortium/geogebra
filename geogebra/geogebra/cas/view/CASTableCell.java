package geogebra.cas.view;


import javax.swing.*;

public class CASTableCell extends JPanel{

	private JTextField	inputArea;
	private JTextField	outputArea;
	private CASBPanel	BBorder;
	private CASTableCellController inputListener;
	private CASTableCellMouseController inputMouseListener;
	private String		input;
	private String		output;
	private JTable		consoleTable;
	private	boolean		lineHighlighted;
	
	public CASTableCell(CASView view, JTable consoleTable) {
		inputArea = new JTextField();
		outputArea = new JTextField();
		BBorder	= new CASBPanel();
		inputArea.setBorder(BorderFactory.createEmptyBorder());
		outputArea.setBorder(BorderFactory.createEmptyBorder());
		this.consoleTable = consoleTable;
		lineHighlighted	=	false;
		
		inputListener =  new CASTableCellController(this, view);
		inputArea.addKeyListener(inputListener);
		inputMouseListener =  new CASTableCellMouseController(this, view);
		inputArea.addMouseListener(inputMouseListener);
		inputArea.setText(">>");
		//outputArea.setText("<<"); 
		
		//this.setLayout(new GridLayout(2, 1));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(inputArea);	
		this.add(outputArea);
		this.add(BBorder);
		this.setBorder(BorderFactory.createEmptyBorder());
		return;
	}			

	public void setInputA(String inValue){
		this.inputArea.setText(">>" + inValue);
	}
	
	public void setInputS(String inValue){
		this.input = inValue;
	}
	
	public void setInput(String inValue){
		this.inputArea.setText(">>" + inValue);
		this.input = inValue;
	}
	
	public void setOutputA(String inValue){
		//add outputarea
		this.outputArea.setText("<<" + inValue);
	}
	
	public void setOutputS(String inValue){
		//add outputarea
		this.output = inValue;
	}
	
	public void setOutput(String inValue){
		this.outputArea.setText("<<" + inValue);
		this.output = inValue;
	}

	public void setLineUnHighlighted(){
		lineHighlighted = false;
	}
	
	public void setLineHighlighted(){
		lineHighlighted = true;
	}
	
	public boolean isLineHighlighted(){
		return this.lineHighlighted;
	}
	
	public String getInputCAS(){
		return inputArea.getText();
	}

	public String getInput(){
		return input;
	}
	
	public String getOutputCAS(){
		return outputArea.getText();
	}
	
	public String getOutput(){
		return output;
	}
	
	public JTable getConsoleTable(){
		return consoleTable;
	}
	
	public void setInputFoucs(){
		inputArea.requestFocus();
	}
	
}
