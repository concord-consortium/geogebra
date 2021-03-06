package geogebra.gui.menubar;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.OptionsDialog;
import geogebra.gui.layout.Layout;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra.main.GeoGebraPreferences;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

/**
 * The "Options" menu.
 */
class OptionsMenu extends BaseMenu implements ActionListener {
	private static final long serialVersionUID = -8032696074032177289L;
	
	private Kernel kernel;
	private Layout layout;
	
	private AbstractAction
		drawingPadPropAction,
		ignoreDocumentPerspectiveAction,
		showViewTitlebarAction,
		savePreferencesAction,
		clearPreferencesAction,
		showOptionsAction
	;
	
	private JCheckBoxMenuItem
		cbShowViewTitlebar,
		cbIgnoreDocumentPerspective
	;
	
	private JMenu
		menuAngleUnit,
		menuPointCapturing,
		menuDecimalPlaces,
		menuContinuity,
		menuPointStyle,
		menuBooleanSize,
		menuRightAngleStyle,
		menuCoordStyle,
		menuLabeling, 
		menuAlgebraStyle
	;
	
	public OptionsMenu(Application app, Layout layout) {
		super(app, app.getMenu("Options"));
		
		kernel = app.getKernel();
		this.layout = layout;
		
		initActions();
		initItems();
		
		update();
	}
	
	/**
	 * Initialize the menu items.
	 */
	private void initItems()
	{
		JMenu submenu;
		int pos;
		
		//G.Sturr 2009-10-18
		// Algebra description: show value or definition of objects
		menuAlgebraStyle = new JMenu(app.getPlain("Algebra"));
		menuAlgebraStyle.setIcon(app.getEmptyIcon());
		String[] strDescription = { app.getPlain("Value"), 
				app.getPlain("Definition"), 
				app.getPlain("Command") };
		String[] strDescriptionAC = { "0", "1", "2" };
		ActionListener descAL = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int desc = Integer.parseInt(ae.getActionCommand());
				kernel.setAlgebraStyle(desc);
				kernel.updateConstruction();
			}
		};
		addRadioButtonMenuItems(menuAlgebraStyle, descAL, strDescription, strDescriptionAC,0);
		add(menuAlgebraStyle);
		updateMenuViewDescription();
		//END G.Sturr
		
		// point capturing
		menuPointCapturing = new JMenu(app.getMenu("PointCapturing"));
		menuPointCapturing.setIcon(app.getImageIcon("magnet.gif"));
		String[] strPointCapturing = { "Labeling.automatic", "on",
				"Labeling.OnGrid", "off" };
		String[] strPointCapturingAC = { "3 PointCapturing",
				"1 PointCapturing", "2 PointCapturing", "0 PointCapturing" };
		addRadioButtonMenuItems(menuPointCapturing, (ActionListener) this,
				strPointCapturing, strPointCapturingAC, 0);
		add(menuPointCapturing);
		updateMenuPointCapturing();

		// Angle unit
		menuAngleUnit = new JMenu(app.getMenu("AngleUnit"));
		menuAngleUnit.setIcon(app.getImageIcon("mode_angle_16.gif"));
		String[] strAngleUnit = { "Degree", "Radiant" };
		addRadioButtonMenuItems(menuAngleUnit, (ActionListener) this,
				strAngleUnit, strAngleUnit, 0);
		add(menuAngleUnit);
		updateMenuAngleUnit();

		// decimal places
		menuDecimalPlaces = new JMenu(app.getMenu("Rounding"));
		menuDecimalPlaces.setIcon(app.getEmptyIcon());
		/*
		 * int max_dec = 15; String[] strDecimalSpaces = new String[max_dec +
		 * 1]; String[] strDecimalSpacesAC = new String[max_dec + 1]; for (int
		 * i=0; i <= max_dec; i++){ strDecimalSpaces[i] = Integer.toString(i);
		 * strDecimalSpacesAC[i] = i + " decimals"; }
		 */
		String[] strDecimalSpaces = app.getRoundingMenu();

		addRadioButtonMenuItems(menuDecimalPlaces, (ActionListener) this,
				strDecimalSpaces, Application.strDecimalSpacesAC, 0);
		add(menuDecimalPlaces);
		updateMenuDecimalPlaces();

		// continuity
		menuContinuity = new JMenu(app.getMenu("Continuity"));
		menuContinuity.setIcon(app.getEmptyIcon());
		String[] strContinuity = { "on", "off" };
		String[] strContinuityAC = { "true Continuity", "false Continuity" };
		addRadioButtonMenuItems(menuContinuity, (ActionListener) this,
				strContinuity, strContinuityAC, 0);
		add(menuContinuity);
		updateMenuContinuity();

		addSeparator();

		// point style
		menuPointStyle = new JMenu(app.getMenu("PointStyle"));
		menuPointStyle.setIcon(app.getImageIcon("mode_point_16.gif"));
		// dot, circle, cross, plus
		String[] strPointStyle = { "\u25cf", "\u25cb", "\u2716", "\u271a", "\u25c6", "\u25b2", "\u25bc" };
		String[] strPointStyleAC = { "0", "2", "1", "3", "4", "6", "7" };
		ActionListener psal = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int style = Integer.parseInt(ae.getActionCommand());
				app.getEuclidianView().setPointStyle(style);
			}
		};
		addRadioButtonMenuItems(menuPointStyle, psal, strPointStyle,
				strPointStyleAC, 0);
		add(menuPointStyle);
		updateMenuPointStyle();

		// checkboxsize
		// Michael Borcherds 2008-05-12
		menuBooleanSize = new JMenu(app.getMenu("CheckboxSize"));
		menuBooleanSize.setIcon(app.getImageIcon("checkbox16.gif"));
		// dot, circle, cross
		String[] strBooleanSize = { app.getMenu("CheckboxSize.Regular"),
				app.getMenu("CheckboxSize.Large") };
		String[] strBooleanSizeAC = { "13", "26" };
		ActionListener bsal = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int size = Integer.parseInt(ae.getActionCommand());
				app.getEuclidianView().setBooleanSize(size);
			}
		};
		addRadioButtonMenuItems(menuBooleanSize, bsal, strBooleanSize,
				strBooleanSizeAC, 0);
		add(menuBooleanSize);
		updateMenuBooleanSize();

		// added by Lo�c BEGIN
		// right angle style

		menuRightAngleStyle = new JMenu(app.getMenu("RightAngleStyle"));
		menuRightAngleStyle.setIcon(app.getImageIcon("right_angle.gif"));
		// dot, none, square
		String[] strAngleStyle = { app.getPlain("off"), "\u25a1", "\u2219", "\u2335" };
		String[] strAngleStyleAC = {
				String.valueOf(EuclidianView.RIGHT_ANGLE_STYLE_NONE),
				String.valueOf(EuclidianView.RIGHT_ANGLE_STYLE_SQUARE),
				String.valueOf(EuclidianView.RIGHT_ANGLE_STYLE_DOT),
				String.valueOf(EuclidianView.RIGHT_ANGLE_STYLE_L) };
		ActionListener asal = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int style = Integer.parseInt(ae.getActionCommand());
				app.getEuclidianView().setRightAngleStyle(style);
			}
		};
		addRadioButtonMenuItems(menuRightAngleStyle, asal, strAngleStyle,
				strAngleStyleAC, 0);
		add(menuRightAngleStyle);
		updateMenuRightAngleStyle();
		// END

		// coordinate style
		menuCoordStyle = new JMenu(app.getPlain("Coordinates"));
		menuCoordStyle.setIcon(app.getEmptyIcon());
		// dot, circle, cross
		String[] strCoordStyle = { "A = (x, y)", "A(x | y)" };
		String[] strCoordStyleAC = { "0", "1" };
		ActionListener csal = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int style = Integer.parseInt(ae.getActionCommand());
				kernel.setCoordStyle(style);
				kernel.updateConstruction();
			}
		};
		addRadioButtonMenuItems(menuCoordStyle, csal, strCoordStyle,
				strCoordStyleAC, 0);
		add(menuCoordStyle);
		updateMenuCoordStyle();

		// Labeling
		menuLabeling = new JMenu(app.getMenu("Labeling"));
		menuLabeling.setIcon(app.getImageIcon("mode_showhidelabel_16.gif"));
		String[] lstr = { "Labeling.automatic", "Labeling.on", "Labeling.off",
				"Labeling.pointsOnly" };
		String[] lastr = { "0_labeling", "1_labeling", "2_labeling",
				"3_labeling" };
		addRadioButtonMenuItems(menuLabeling, (ActionListener) this, lstr,
				lastr, 0);
		add(menuLabeling);

		/*
		 * // Graphics quality submenu = new
		 * JMenu(app.getMenu("GraphicsQuality")); String[] gqfi = {
		 * "LowQuality", "HighQuality" }; if
		 * (app.getEuclidianView().getAntialiasing()) pos = 1; else pos = 0;
		 * addRadioButtonMenuItems(submenu, this, gqfi, gqfi, pos);
		 * add(submenu);
		 */

		addSeparator();

		// Font size
		submenu = new JMenu(app.getMenu("FontSize"));
		submenu.setIcon(app.getImageIcon("font.png"));
		String[] fsfi = { "12 pt", "14 pt", "16 pt", "18 pt", "20 pt", "24 pt",
				"28 pt", "32 pt" };

		// find current pos
		String strFS = app.getFontSize() + " pt";
		pos = 0;
		for (int i = 0; i < fsfi.length; i++) {
			if (strFS.equals(fsfi[i])) {
				pos = i;
				break;
			}
		}

		addRadioButtonMenuItems(submenu, (ActionListener) this, fsfi, fsfi, pos);
		add(submenu);

		/*
		 * // FontName menuFontName = new JMenu(getMenu("PointCapturing"));
		 * String[] strFontName = { "Sans Serif", "Serif" }; String[]
		 * strFontNameAC = { "SansSerif", "Serif" };
		 * addRadioButtonMenuItems(menuFontName, al, strFontName, strFontNameAC,
		 * 0); add(menuFontName); updateMenuFontName();
		 */

		// addSeparator();
		// Language
		if (app.propertiesFilesPresent()) {
			LanguageActionListener langListener = new LanguageActionListener();
			submenu = new JMenu(app.getMenu("Language"));
			submenu.setIcon(app.getImageIcon("globe.png"));
			addLanguageMenuItems(submenu, langListener);
			add(submenu);
		}

		addSeparator();

		// drawing pad properteis
		add(drawingPadPropAction);

		if (!app.isApplet()) {
			// perspectives submenu
			submenu = new JMenu(app.getMenu("Perspectives"));
			submenu.setIcon(app.getImageIcon("perspective.gif"));

			cbShowViewTitlebar = new JCheckBoxMenuItem(showViewTitlebarAction);
			app.setEmptyIcon(cbShowViewTitlebar);
			cbShowViewTitlebar.setSelected(layout.isTitleBarVisible());

			cbIgnoreDocumentPerspective = new JCheckBoxMenuItem(
					ignoreDocumentPerspectiveAction);
			app.setEmptyIcon(cbIgnoreDocumentPerspective);
			cbIgnoreDocumentPerspective.setSelected(layout.isIgnoringDocument());

			submenu.add(cbShowViewTitlebar);
			submenu.add(cbIgnoreDocumentPerspective);
			add(submenu);

			// Preferences
			addSeparator();
		}
		
		add(showOptionsAction);
		
		if(!app.isApplet() && Application.hasFullPermissions()) {
			// submenu = new JMenu(app.getMenu("Settings"));
			add(savePreferencesAction);
			add(clearPreferencesAction);
			// add(submenu);
		}
	}

	/**
	 * Create a set of radio buttons automatically.
	 * 
	 * @param menu
	 * @param al
	 * @param items
	 * @param actionCommands
	 * @param selectedPos
	 */
	private void addRadioButtonMenuItems(JMenu menu, ActionListener al,
			String[] items, String[] actionCommands, int selectedPos) {
		JRadioButtonMenuItem mi;
		ButtonGroup bg = new ButtonGroup();
		// String label;

		for (int i = 0; i < items.length; i++) {
			if (items[i] == "---") {
				menu.addSeparator();
			} else {
				String text = app.getMenu(items[i]);
				mi = new JRadioButtonMenuItem(text);
				mi.setFont(app.getFontCanDisplay(text));
				if (i == selectedPos)
					mi.setSelected(true);
				mi.setActionCommand(actionCommands[i]);
				mi.addActionListener(al);
				bg.add(mi);
				menu.add(mi);
			}
		}
	}

	/**
	 * Create a list with all languages which can be selected.
	 * 
	 * @param menu
	 * @param al
	 */
	private void addLanguageMenuItems(JMenu menu, ActionListener al) {
		JRadioButtonMenuItem mi;
		ButtonGroup bg = new ButtonGroup();
		// String label;
		String ggbLangCode;

		JMenu submenu1 = new JMenu("A - G");
		JMenu submenu2 = new JMenu("H - Z");
		menu.add(submenu1);
		menu.add(submenu2);

		for (int i = 0; i < Application.supportedLocales.size(); i++) {
			Locale loc = (Locale) Application.supportedLocales.get(i);
			ggbLangCode = loc.getLanguage() + loc.getCountry()
					+ loc.getVariant();

			// enforce to show specialLanguageNames first
			// because here getDisplayLanguage doesn't return a good result
			String text = (String) Application.specialLanguageNames
					.get(ggbLangCode);
			if (text == null)
				text = loc.getDisplayLanguage(Locale.ENGLISH);
			mi = new JRadioButtonMenuItem(text);

			if (loc == app.getLocale())
				mi.setSelected(true);
			mi.setActionCommand(ggbLangCode);
			mi.addActionListener(al);
			bg.add(mi);

			if (text.charAt(0) <= 'G')
				submenu1.add(mi);
			else
				submenu2.add(mi);
		}
	}
	
	/**
	 * Initialize the actions.
	 */
	private void initActions()
	{
		// display the options dialog
		showOptionsAction = new AbstractAction(app
				.getMenu("Options"), app.getEmptyIcon()) {
			public static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().showOptionsDialog(-1);
			}
		};
		
		// option to hide title bar of views
		showViewTitlebarAction = new AbstractAction(app
				.getMenu("ShowViewTitlebar")) {
			public static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				layout.setTitlebarVisible(!layout.isTitleBarVisible());
			}
		};

		// option to ignore the perspective stored in documents
		ignoreDocumentPerspectiveAction = new AbstractAction(app
				.getMenu("IgnoreDocumentPerspective")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				layout.setIgnoreDocument(!layout.isIgnoringDocument());
			}
		};

		drawingPadPropAction = new AbstractAction(app.getPlain("DrawingPad")
				+ " ...", app.getImageIcon("document-properties.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().showOptionsDialog(OptionsDialog.TAB_EUCLIDIAN);
			}
		};

		savePreferencesAction = new AbstractAction(
				app.getMenu("Settings.Save"), app
						.getImageIcon("document-save.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				GeoGebraPreferences.getPref().saveXMLPreferences(app);
			}
		};

		clearPreferencesAction = new AbstractAction(app
				.getMenu("Settings.ResetDefault"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				GeoGebraPreferences.getPref().clearPreferences();
			}
		};
	}

	@Override
	public void update() {
		// TODO update labels
		
		updateMenuAngleUnit();
		updateMenuBooleanSize();
		updateMenuContinuity();
		updateMenuCoordStyle();
		updateMenuDecimalPlaces();
		updateMenuPointCapturing();
		updateMenuPointStyle();
		updateMenuRightAngleStyle();
		updateMenuViewDescription();
	}
	
	/**
	 * Update angle menu (switch between degree / radiant).
	 */
	private void updateMenuAngleUnit() {
		int pos;
		if (kernel.getAngleUnit() == Kernel.ANGLE_DEGREE)
			pos = 0;
		else
			pos = 1;
		if (menuAngleUnit != null) {
			((JRadioButtonMenuItem) menuAngleUnit.getMenuComponent(pos))
					.setSelected(true);
		}
	}
	
	/**
	 * Update algebra style description (switch between value / definition / command).
	 */
	private void updateMenuViewDescription() {
		if (menuAlgebraStyle != null) {
			((JRadioButtonMenuItem) menuAlgebraStyle.getMenuComponent(kernel.getAlgebraStyle()))
					.setSelected(true);
		}
	}
	
	/**
	 * Update default point drawing style.
	 */
	protected void updateMenuPointStyle() {
		if (menuPointStyle == null) return;
		
		int pos = app.getEuclidianView().getPointStyle();
		if (pos == 1) pos=2; else if (pos == 2) pos=1; // bugfix swap 2 and 1 Michael Borcherds 2008-03-13
		if (pos > 5) pos--; // needed as hollow diamond not in menu (yet)
		((JRadioButtonMenuItem) menuPointStyle.getMenuComponent(pos)).setSelected(true);
	}
	
	/**
	 * Update the size of the checkboxes.
	 */
	private void updateMenuBooleanSize() {
		if (menuBooleanSize == null)
			return;

		int size = app.getEuclidianView().getBooleanSize();
		int pos = (size == 13) ? 0 : 1; // only 13 and 26 allowed
		((JRadioButtonMenuItem) menuBooleanSize.getMenuComponent(pos))
				.setSelected(true);
	}

	/**
	 * Update the style of a right angle in the euclidian view.
	 * 
	 * @author Lo�c
	 */
	private void updateMenuRightAngleStyle() {
		if (menuRightAngleStyle == null)
			return;

		int pos = app.getEuclidianView().getRightAngleStyle();
		((JRadioButtonMenuItem) menuRightAngleStyle.getMenuComponent(pos))
				.setSelected(true);
	}
	
	/**
	 * Update the coordinate style.
	 */
	private void updateMenuCoordStyle() {
		if (menuCoordStyle == null)
			return;

		int pos = kernel.getCoordStyle();
		((JRadioButtonMenuItem) menuCoordStyle.getMenuComponent(pos))
				.setSelected(true);
	}
	
	/**
	 * Update the point capturing menu.
	 */
	private void updateMenuPointCapturing() {
		if (menuPointCapturing == null)
			return;

		String pos = Integer.toString(app.getEuclidianView()
				.getPointCapturingMode());
		for (int i = 0; i < 4; i++) {
			JRadioButtonMenuItem mi = (JRadioButtonMenuItem) menuPointCapturing
					.getMenuComponent(i);
			String ac = mi.getActionCommand();
			if (ac.substring(0, 1).equals(pos)) {
				mi.setSelected(true);
				break;
			}
		}
	}

	/**
	 * Update the menu with all decimal places.
	 */
	private void updateMenuDecimalPlaces() {
		if (menuDecimalPlaces == null)
			return;
		int pos = -1;

		if (kernel.useSignificantFigures) {
			int figures = kernel.getPrintFigures();
			if (figures > 0 && figures < Application.figuresLookup.length)
				pos = Application.figuresLookup[figures];
		} else {
			int decimals = kernel.getPrintDecimals();

			if (decimals > 0 && decimals < Application.decimalsLookup.length)
				pos = Application.decimalsLookup[decimals];

		}

		try {
			((JRadioButtonMenuItem) menuDecimalPlaces.getMenuComponent(pos))
					.setSelected(true);
		} catch (Exception e) {
		}

	}
	
	/**
	 * Update the continuity menu.
	 */
	private void updateMenuContinuity() {
		int pos = kernel.isContinuous() ? 0 : 1;
		try {
			((JRadioButtonMenuItem) menuContinuity.getMenuComponent(pos))
					.setSelected(true);
		} catch (Exception e) {
		}
	}
	
	/**
	 * Handle the change of the language.
	 */
	private class LanguageActionListener implements ActionListener {

		public LanguageActionListener() {
		}

		public void actionPerformed(ActionEvent e) {
			app.setLanguage(Application.getLocale(e.getActionCommand()));
			// make sure axes labels are updated eg for Arabic 
			app.getEuclidianView().updateBackground(); 
			GeoGebraPreferences.getPref().saveDefaultLocale(app.getLocale());
		}
	}

	/**
	 * Execute a performed action.
	 */
	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();

		// change angle unit
		if (cmd.equals("Degree")) {
			kernel.setAngleUnit(Kernel.ANGLE_DEGREE);
			kernel.updateConstruction();
			app.setUnsaved();
		} else if (cmd.equals("Radiant")) {
			kernel.setAngleUnit(Kernel.ANGLE_RADIANT);
			kernel.updateConstruction();
			app.setUnsaved();
		}

		// change graphics quality
		else if (cmd.equals("LowQuality")) {
			app.getEuclidianView().setAntialiasing(false);
		} else if (cmd.equals("HighQuality")) {
			app.getEuclidianView().setAntialiasing(true);
		}

		// font size
		else if (cmd.endsWith("pt")) {
			try {
				app.setFontSize(Integer.parseInt(cmd.substring(0, 2)));
				app.setUnsaved();
				System.gc();
			} catch (Exception e) {
				app.showError(e.toString());
			}
			;
		}

		// decimal places
		else if (cmd.endsWith("decimals")) {
			try {
				String decStr = cmd.substring(0, 2).trim();
				int decimals = Integer.parseInt(decStr);
				// Application.debug("decimals " + decimals);

				kernel.setPrintDecimals(decimals);
				kernel.updateConstruction();
				app.refreshViews();
				
				// see ticket 79
				kernel.updateConstruction();

				app.setUnsaved();
			} catch (Exception e) {
				app.showError(e.toString());
			}
		}

		// significant figures
		else if (cmd.endsWith("figures")) {
			try {
				String decStr = cmd.substring(0, 2).trim();
				int figures = Integer.parseInt(decStr);
				// Application.debug("figures " + figures);

				kernel.setPrintFigures(figures);
				kernel.updateConstruction();
				app.refreshViews();
				
				// see ticket 79
				kernel.updateConstruction();

				app.setUnsaved();
			} catch (Exception e) {
				app.showError(e.toString());
			}
		}

		// Point capturing
		else if (cmd.endsWith("PointCapturing")) {
			int mode = Integer.parseInt(cmd.substring(0, 1));
			app.getEuclidianView().setPointCapturing(mode);
			app.setUnsaved();
		}

		// Continuity
		else if (cmd.endsWith("Continuity")) {
			boolean state = cmd.startsWith("true");
			kernel.setContinuous(state);
			kernel.updateConstruction();
			app.setUnsaved();
		}

		// Labeling
		else if (cmd.endsWith("labeling")) {
			int style = Integer.parseInt(cmd.substring(0, 1));
			app.setLabelingStyle(style);
			app.setUnsaved();
		}
	}
}
