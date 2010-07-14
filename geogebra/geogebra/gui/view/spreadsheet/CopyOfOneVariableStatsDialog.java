package geogebra.gui.view.spreadsheet;

import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class CopyOfOneVariableStatsDialog extends JDialog implements ActionListener, TableModelListener  {
	
	// ggb 
	private Application app;
	private Kernel kernel; 
	private Construction cons;
	
	// plot types
	private static final int PLOT_HISTOGRAM = 0;
	private static final int PLOT_BOXPLOT = 1;
	private static final int PLOT_DOTPLOT = 2;
	//private static final int PLOT_NORMALQUANTILE = 3;
	private static final int PLOT_STATISTICS = 3;
	private int numPlots = 4;
	private GeoElement[] plotGeos;
	private int selectedPlotIndex = PLOT_HISTOGRAM;
	private int numClasses = 6;
	
	// GUI
	private JPanel statPanel, plotPanel, dataPanel;
	private JTable statTable, dataTable;
	//private DefaultTableModel statModel;
	private JScrollPane statScroller;
	private JComboBox cbNumClasses, cbPlotTypes;
	private JToggleButton btnSort;
	private JCheckBox enableAllData;
	
	// data and stat lists
	private GeoList rawDataList, dataList, statList;
	private double xMin, xMax;
	
	// new EuclidianView instance for the dialog plots
	private EuclidianView ev;
	private EuclidianController ec;
	
	
	// layout
	private static final Color TABLE_GRID_COLOR = Color.gray;
	
	
	
	/*************************************************
	 * Construct the dialog
	 */
	public CopyOfOneVariableStatsDialog(MyTable table, Application app){
		super(app.getFrame(),true);
		
		this.app = app;	
		kernel = app.getKernel();
		cons = kernel.getConstruction();
				
		// create an instance of EuclideanView
		ec = new EuclidianController(kernel);
		boolean[] showAxes = { true, true };
		boolean showGrid = false;
		ev = new EuclidianView(ec, showAxes, showGrid);
		ev.setAntialiasing(true);
		ev.updateFonts();
		ev.setPreferredSize(new Dimension(300,200));
		ev.setSize(new Dimension(300,200));
		ev.updateSize();
		
		plotGeos = new GeoElement[numPlots];
		
		
		// create data and stat geoLists
	//	rawDataList = (GeoList) table.getCellRangeProcessor().createListNumeric(table.selectedCellRanges, true, true, true);	
		dataList = (GeoList)rawDataList.copyInternal(cons);
		setXMinMax();
		dataList.setLabel(null);
		createStatList(dataList);
		
		// start it up!
		initGUI();
		updateFonts();
	}
	
	
	
	
	//=================================================
	//       Create GUI
	//=================================================
	
	
	private void initGUI() {

		try {
			setTitle(app.getPlain("One Variable Statistics"));	
			
			JPanel mainPanel = new JPanel(new BorderLayout());
			mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
			
			mainPanel.add(buildPlotPanel(), BorderLayout.NORTH);	
			//mainPanel.add(buildStatPanel(), BorderLayout.SOUTH);
			//mainPanel.add(buildDataPanel(), BorderLayout.WEST);
			//this.setPreferredSize(new Dimension(400,300));
						
			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mainPanel, buildDataPanel());
			
			this.getContentPane().add(splitPane);
			this.getContentPane().setPreferredSize(new Dimension(500,500));
			//setResizable(false);
			pack();
			setLocationRelativeTo(app.getFrame());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	
	private JPanel buildPlotPanel(){
		
		JLabel lblNumClasses = new JLabel(app.getPlain("classes")); 
		
		Integer[] classArray = {2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};		
		cbNumClasses = new JComboBox(classArray);
		cbNumClasses.setSelectedItem(numClasses);
		cbNumClasses.addActionListener(this);
		
		JPanel numClassesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		numClassesPanel.add(lblNumClasses);
		numClassesPanel.add(cbNumClasses);
		
		String[] plotNames = new String[numPlots]; 
		plotNames[PLOT_HISTOGRAM] = app.getCommand("Histogram");
		plotNames[PLOT_BOXPLOT] = app.getCommand("Boxplot");
		plotNames[PLOT_DOTPLOT] = app.getCommand("DotPlot");
		plotNames[PLOT_STATISTICS] = app.getPlain("Statistics");
		//plotNames[PLOT_NORMALQUANTILE] = app.getCommand("NormalQuantile");
		
		cbPlotTypes = new JComboBox(plotNames);
		cbPlotTypes.setSelectedIndex(selectedPlotIndex);
		cbPlotTypes.addActionListener(this);
		JPanel plotTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		plotTypePanel.add(cbPlotTypes);
		
		JPanel controlPanel = new JPanel(new BorderLayout());
		controlPanel.add(plotTypePanel,BorderLayout.WEST);
		controlPanel.add(numClassesPanel,BorderLayout.EAST);
		
		plotPanel = new JPanel(new BorderLayout());
		plotPanel.add(controlPanel,BorderLayout.NORTH);
		plotPanel.add(ev,BorderLayout.CENTER);
		plotPanel.add(buildStatPanel(),BorderLayout.SOUTH);
		statPanel.setVisible(false);
		plotPanel.setBorder(BorderFactory.createEtchedBorder());
			
		updatePlot();
		
		return plotPanel;
	}
	
	private JPanel buildStatPanel(){
		
		// build the stat table	
		statTable = new JTable(){
			// disable cell editing
		      @Override
			public boolean isCellEditable(int rowIndex, int colIndex) {
		        return false;   
		      }
		    };
		 updateStatTable(); 
		 
		statTable.setDefaultRenderer(Object.class, new MyCellRenderer());    
		statTable.setColumnSelectionAllowed(true); 
		statTable.setRowSelectionAllowed(true);
		statTable.setShowGrid(true); 	 
		statTable.setGridColor(TABLE_GRID_COLOR); 	 	
		statTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		statTable.setPreferredScrollableViewportSize(statTable.getPreferredSize());
		
		statTable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		statTable.setMinimumSize(new Dimension(50,50));
		
	
		// enclose the table in a scroller
		statScroller = new JScrollPane(statTable);
		statScroller.setBorder(BorderFactory.createEmptyBorder());
		
		// hide the table header
		statTable.setTableHeader(null);
		statScroller.setColumnHeaderView(null);
		
		// put it all into the stat panel
		statPanel = new JPanel(new BorderLayout());
		statPanel.add(statScroller, BorderLayout.CENTER);
		
		// statPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		statPanel.setBorder(BorderFactory.createEmptyBorder());

		return statPanel;

	}



	
	private JPanel buildDataPanel(){

		// build the data table
		
		dataTable = new JTable(){
		      
		      @Override
		  	protected void configureEnclosingScrollPane() {
		  		super.configureEnclosingScrollPane();
		  		Container p = getParent();
		  		if (p instanceof JViewport) {
		  			((JViewport) p).setBackground(getBackground());
		  		}
		      }
		};
		updateDataTable();


		// Enable cell selection 
		dataTable.setDefaultRenderer(Object.class, new MyCellRenderer());    
		dataTable.setColumnSelectionAllowed(true); 
		dataTable.setRowSelectionAllowed(true);
		
		dataTable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		//dataTable.setAutoResizeMode(JTable.);
		dataTable.setPreferredScrollableViewportSize(dataTable.getPreferredSize());
		//dataTable.setMinimumSize(new Dimension(50,50));

		TableColumn tc = dataTable.getColumnModel().getColumn(0);  
		tc.setCellEditor(dataTable.getDefaultEditor(Boolean.class));  
		tc.setCellRenderer(dataTable.getDefaultRenderer(Boolean.class));  
		tc.setHeaderRenderer(new CheckBoxHeader(new MyItemListener()));
		tc.setPreferredWidth(40);

		JScrollPane dataScroller = new JScrollPane(dataTable);
		dataScroller.setBorder(BorderFactory.createEmptyBorder());
		
		dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		dataTable.doLayout();
		dataTable.setAutoCreateColumnsFromModel(false);
		

		// hide the table header
		//dataTable.setTableHeader(null);
		//dataScroller.setColumnHeaderView(null);
		
		
		btnSort = new JToggleButton(app.getCommand("sort"));
		enableAllData = new JCheckBox(" ");
		
		
		JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		//headerPanel.add(enableAllData);
		//headerPanel.add(btnSort);
		
		
		
		dataPanel = new JPanel(new BorderLayout());
		//dataPanel.add(headerPanel, BorderLayout.NORTH);
		dataPanel.add(dataScroller, BorderLayout.CENTER);
		
			
		return dataPanel;
	}
	

	private void  createStatList(GeoList dataList){
		
		String label = dataList.getLabel();	
		String text = "";
		ArrayList<String> list = new ArrayList<String>();
		
		try {		
			text += "{";
			text += statListCmdString("Length", label);
			text += ",";
			text += statListCmdString("Mean", label);
			text += ",";
			text += statListCmdString("SD", label);
			text += ",";
			text += statListCmdString("SampleSD", label);
			text += ",";
			text += statListCmdString("Min", label);
			text += ",";
			text += statListCmdString("Q1", label);
			text += ",";
			text += statListCmdString("Median", label);
			text += ",";
			text += statListCmdString("Q3", label);
			text += ",";
			text += statListCmdString("Max", label);
			
			text += "}";
			
			
			// convert list string to geo
			GeoElement[] geos = kernel.getAlgebraProcessor()
					.processAlgebraCommandNoExceptionHandling(text, false);
	
			statList = (GeoList) geos[0];
			//System.out.println(text);		
		} catch (Exception ex) {
			Application.debug("Creating list failed with exception " + ex);
			setVisible(false);
		}	
		
	}
	
	private String statListCmdString(String cmdStr, String geoLabel){
		
		String text = "{";
		text += "\"" + app.getCommand(cmdStr)+ "\",";
		text += cmdStr + "[" + geoLabel + "]";
		text += "}";
		
		return text; 
	}
	
	
	

	
	//=================================================
	//       Listeners & Event Handlers
	//=================================================
	
	
	public void tableChanged(TableModelEvent e) {

		if(e.getColumn()==0){
			int row = e.getFirstRow();
			int column = e.getColumn();
			TableModel model = (TableModel)e.getSource();
			//System.out.println("row="+row+", Column="+column+" "+model.getValueAt(row,column));
			this.updateDataList();
		}
	}
	
	
	class MyItemListener implements ItemListener  
	{  
		public void itemStateChanged(ItemEvent e) {  
			Object source = e.getSource();  
			if (source instanceof AbstractButton == false) return;  
			boolean checked = e.getStateChange() == ItemEvent.SELECTED;  
			for(int x = 0, y = dataTable.getRowCount(); x < y; x++)  
			{  
				dataTable.setValueAt(new Boolean(checked),x,0);  
			}  
		}  
	}  
	
	
	public void actionPerformed(ActionEvent e) {	
		doActionPerformed(e.getSource());
	}	
	public void doActionPerformed(Object source) {		
				
		if (source == cbNumClasses) {
			numClasses = (Integer) cbNumClasses.getSelectedItem();
			updatePlot();
		}
		
		if (source == cbPlotTypes) {
			if(plotGeos[selectedPlotIndex] != null)
				plotGeos[selectedPlotIndex].setEuclidianVisible(false);
			selectedPlotIndex = cbPlotTypes.getSelectedIndex();
			updatePlot();
			
		}
		statPanel.requestFocus(); 
	}

	
	
	@Override
	public void setVisible(boolean isVisible){
		super.setVisible(isVisible);

		
		if(!isVisible){
			
			for(int i = 0; i < plotGeos.length; ++i){
				if(plotGeos[i] != null)
					plotGeos[i].remove();
			}
		
			if(statList != null) statList.remove();
			if(dataList != null) dataList.remove();
			if(rawDataList != null) rawDataList.remove();
			
		//	if(boxPlot != null) boxPlot.remove();
		//	if(histogram != null) histogram.remove();
			
		}
	}
	

	
	
	

	
	//=================================================
	//      Update
	//=================================================
	
	 
	private void updatePlot(){
			
		switch(selectedPlotIndex){
		case PLOT_HISTOGRAM:
			statPanel.setVisible(false);
			ev.setVisible(true);
			updateHistogram( dataList, numClasses);
			break;	
		case PLOT_BOXPLOT:
			statPanel.setVisible(false);
			ev.setVisible(true);
			updateBoxPlot( dataList);
			break;
		case PLOT_DOTPLOT:
			statPanel.setVisible(false);
			ev.setVisible(true);
			updateDotPlot( dataList);
			break;
		case PLOT_STATISTICS:
			ev.setVisible(false);
			statPanel.setVisible(true);
			break;
		}
		if(plotGeos[selectedPlotIndex] != null){
			plotGeos[selectedPlotIndex].setEuclidianVisible(true);
			plotGeos[selectedPlotIndex].update();
			ev.repaint();
		}
	}

	private void updateStatTable(){
		TableModel statModel = new DefaultTableModel(statList.size(), 2);
		GeoList list;
		for (int elem = 0; elem < statList.size(); ++elem){
			list = (GeoList)statList.get(elem);
			statModel.setValueAt(list.get(0).toDefinedValueString(), elem, 0);
			statModel.setValueAt(list.get(1).toDefinedValueString(), elem, 1);
		}
		statTable.setModel(statModel);
	}
	
	private void updateDataTable(){
		TableModel dataModel = new DefaultTableModel(rawDataList.size(),2);
		for (int row = 0; row < rawDataList.size(); ++row){
			dataModel.setValueAt(new Boolean(true),row,0);
			dataModel.setValueAt(rawDataList.get(row).toDefinedValueString(),row,1);
		}
		dataTable.setModel(dataModel);
		dataModel.addTableModelListener(this);
	}
	
	
	private void updateDataList(){
		dataList.clear();
		for(int i=0; i < rawDataList.size(); ++i){
			if(((Boolean)dataTable.getValueAt(i, 0))== true){
				dataList.add(rawDataList.get(i).copyInternal(cons));
			}
		}
		dataList.updateCascade();
		updateStatTable();
		updatePlot();
	}
	
	
	public void updateFonts() {
		
		Font font = app.getPlainFont();
		
		int size = font.getSize();
		if (size < 12) size = 12; // minimum size
		double multiplier = (size)/12.0;
		
		setFont(font);
		statTable.setRowHeight((int)(MyTable.TABLE_CELL_HEIGHT * multiplier));
		statTable.setFont(font);  
		dataTable.setRowHeight((int)(MyTable.TABLE_CELL_HEIGHT * multiplier));
		dataTable.setFont(font);  
		
		
		//statTable.columnHeader.setFont(font);
		//table.preferredColumnWidth = (int) (MyTable.TABLE_CELL_WIDTH * multiplier);
		//table.columnHeader.setPreferredSize(new Dimension(table.preferredColumnWidth, (int)(MyTable.TABLE_CELL_HEIGHT * multiplier)));
		
	}
	
	
	
	
	
	
	//=================================================
	//       Plots
	//=================================================
	
	
	private void setXMinMax(){
		String label = rawDataList.getLabel();	
		NumberValue nv;
		nv = kernel.getAlgebraProcessor().evaluateToNumeric("Min[" + label + "]", false);		
		xMin = nv.getDouble();
		
		nv = kernel.getAlgebraProcessor().evaluateToNumeric("Max[" + label + "]", false);		
		xMax = nv.getDouble();
	}
	
	
	private void updateHistogram(GeoList dataList, int numClasses){
		
		String label = dataList.getLabel();	
		String text = "";
			
		double barWidth = (xMax - xMin)/(numClasses - 1);  
		double freqMax = getFrequencyTableMax(dataList, barWidth);
		
		// Set view parameters		
		ev.setRealWorldCoordSystem(xMin - barWidth, xMax + barWidth, -1.0, 1.1 * freqMax);
		ev.setShowAxis(EuclidianViewInterface.AXIS_Y, false, true);

		
		// Create histogram	
		if(plotGeos[PLOT_HISTOGRAM] != null)
			plotGeos[PLOT_HISTOGRAM].remove();

		text = "BarChart[" + label + "," + Double.toString(barWidth) + "]";
		plotGeos[PLOT_HISTOGRAM] = createDialogGeoFromText(text);
		
			
	}
	

	private void updateBoxPlot(GeoList dataList){

		String label = dataList.getLabel();	
		String text = "";
		
		// Set view parameters	
		double buffer = .1*(xMax - xMin);			
		ev.setRealWorldCoordSystem(xMin - buffer, xMax + buffer, -1.0, 3);
		ev.setShowAxis(EuclidianViewInterface.AXIS_Y, false, true);
		
		// create boxplot
		if(plotGeos[PLOT_BOXPLOT] == null){ 
			text = "BoxPlot[1,0.5," + label + "]";
			plotGeos[PLOT_BOXPLOT] = createDialogGeoFromText(text);
		}
	
				
	}
	
	
	private void updateDotPlot(GeoList dataList){

		String label = rawDataList.getLabel();	
		String text = "";
	
		double buffer = .1*(xMax - xMin);	
		
		if(plotGeos[PLOT_DOTPLOT] != null)  
			plotGeos[PLOT_DOTPLOT].remove();
		
		// create dotplot
		int maxCount = 1;
		text = "{";
		if(dataList.size()>0){
			text += "(" + ((GeoNumeric)dataList.get(0)).getDouble() + ", 1)";
			int k = 1;
			for (int i = 1; i < dataList.size(); ++i){
				if(((GeoNumeric)dataList.get(i-1)).getDouble() == ((GeoNumeric)dataList.get(i)).getDouble() ) 
					++k;
				else
					k = 1;

				text += ",(" + ((GeoNumeric)dataList.get(i)).getDouble() + "," + k + ")";
				maxCount = maxCount < k ? k : maxCount;
			}
		}
		text += "}";
	
		//System.out.println(text);
	
		plotGeos[PLOT_DOTPLOT] = createDialogGeoFromText(text);
		
		// Set view parameters		
		ev.setRealWorldCoordSystem(xMin - buffer, xMax + buffer, -1.0, maxCount + 1);
		ev.setShowAxis(EuclidianViewInterface.AXIS_Y, false, true);	
	}
	
	
	
	private GeoElement createDialogGeoFromText(String text){
		try {
			boolean oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			
			GeoElement[] geos = kernel.getAlgebraProcessor()
			.processAlgebraCommandNoExceptionHandling(text, false);
			geos[0].addView(ev);
			geos[0].setAlgebraVisible(false);
			cons.setSuppressLabelCreation(oldMacroMode);
			geos[0].setLabel(null);
			return geos[0];
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
	
	
	//=================================================
	//       Frequency Table
	//=================================================
	
	// create frequency table
	private double getFrequencyTableMax(GeoList list1, double n){

		
		double [] yval; // y value (= min) in interval 0 <= i < N
		double [] leftBorder; // leftBorder (x val) of interval 0 <= i < N
		GeoElement geo;	

		double mini = Double.MAX_VALUE;
		double maxi = Double.MIN_VALUE;
		int minIndex = -1;
		int maxIndex = -1;

		double step = n ;   //n.getDouble();
		int rawDataSize = list1.size();

		if (step < 0 || kernel.isZero(step) || rawDataSize < 2)
		{
			return 0;
		}


		// find max and min
		for (int i = 0; i < rawDataSize; i++) {
			geo = list1.get(i);
			if (!geo.isGeoNumeric()) {
				return 0;
			}
			double val = ((GeoNumeric)geo).getDouble();

			if (val > maxi) {
				maxi = val;
				maxIndex = i;
			}
			if (val < mini) {
				mini = val;
				minIndex = i;
			}
		}

		if (maxi == mini || maxIndex == -1 || minIndex == -1) {
			return 0;
		}

		double totalWidth = maxi - mini;
		double noOfBars = totalWidth / n;    //n.getDouble();
		double gap = 0;

		int N = (int)noOfBars + 2;
		gap = ((N-1) * step - totalWidth) / 2.0;
		
		NumberValue a = (new GeoNumeric(cons,mini - gap));
		NumberValue b = (new GeoNumeric(cons,maxi + gap));

		yval = new double[N];
		leftBorder = new double[N];


		// fill in class boundaries
		//double width = (maxi-mini)/(double)(N-2);
		for (int i=0; i < N; i++) {
			leftBorder[i] = mini - gap + step * i;
		}


		// zero frequencies
		for (int i=0; i < N; i++) yval[i] = 0; 	

		// work out frequencies in each class
		double datum;

		for (int i=0; i < list1.size() ; i++) {
			geo = list1.get(i);
			if (geo.isGeoNumeric())	datum = ((GeoNumeric)geo).getDouble(); 
			else {  return 0; }

			// fudge to make the last boundary eg 10 <= x <= 20
			// all others are 10 <= x < 20
			double oldMaxBorder = leftBorder[N-1];
			leftBorder[N-1] += Math.abs(leftBorder[N-1] / 100000000);

			// check which class this datum is in
			for (int j=1; j < N; j++) {
				//System.out.println("checking "+leftBorder[j]);
				if (datum < leftBorder[j]) 
				{
					//System.out.println(datum+" "+j);
					yval[j-1]++;
					break;
				}
			}

			leftBorder[N-1] = oldMaxBorder;
		}

		double freqMax = 0.0;
		for(int k = 0; k < yval.length; ++k){
			if(yval[k] > freqMax)
				freqMax = yval[k];
		}
		return freqMax;
		
	}


	
	
	
	
	
	

	//======================================================
	//         Cell Renderer 
	//======================================================
	
	class MyCellRenderer extends DefaultTableCellRenderer {
		
		public MyCellRenderer(){
			
		setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		
		}
		
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) 
		{	
			String text = value.toString();
			
			if(value instanceof Boolean){
				setText(text);
			}else{
				setText(text);
			}
			
			//setFont(app.getFontCanDisplay(text, Font.PLAIN));
			
			return this;
		}

	}

	
	//======================================================
	//         CheckBoxHeader  
	//======================================================
	

	class CheckBoxHeader extends JCheckBox  implements TableCellRenderer, MouseListener { 

		protected CheckBoxHeader rendererComponent;  
		protected int column;  
		protected boolean mousePressed = false;  

		public CheckBoxHeader(ItemListener itemListener) {  
			rendererComponent = this;  
			rendererComponent.addItemListener(itemListener);  
		}  

		public Component getTableCellRendererComponent( JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			
			if (table != null) {  
				JTableHeader header = table.getTableHeader();  
				if (header != null) {  
					rendererComponent.setForeground(header.getForeground());  
					rendererComponent.setBackground(header.getBackground());  
					rendererComponent.setFont(header.getFont());  
					header.addMouseListener(rendererComponent);  
				}  
			}  
			setColumn(column);  
			rendererComponent.setText(null);  
			setBorder(UIManager.getBorder("TableHeader.cellBorder")); 
			setHorizontalAlignment(CENTER);
			
			return rendererComponent;  
		}  
		protected void setColumn(int column) {  
			this.column = column;  
		}  
		public int getColumn() {  
			return column;  
		}  
		protected void handleClickEvent(MouseEvent e) {  
			if (mousePressed) {  
				mousePressed=false;  
				JTableHeader header = (JTableHeader)(e.getSource());  
				JTable tableView = header.getTable();  
				TableColumnModel columnModel = tableView.getColumnModel();  
				int viewColumn = columnModel.getColumnIndexAtX(e.getX());  
				int column = tableView.convertColumnIndexToModel(viewColumn);  

				if (viewColumn == this.column && e.getClickCount() == 1 && column != -1) {  
					doClick();  
				}  
			}  
		}  
		public void mouseClicked(MouseEvent e) {  
			handleClickEvent(e);  
			((JTableHeader)e.getSource()).repaint();  
		}  
		public void mousePressed(MouseEvent e) {  
			mousePressed = true;  
		}  
		public void mouseReleased(MouseEvent e) {  
		}  
		public void mouseEntered(MouseEvent e) {  
		}  
		public void mouseExited(MouseEvent e) {  
		}  
	}  



	
	
}
