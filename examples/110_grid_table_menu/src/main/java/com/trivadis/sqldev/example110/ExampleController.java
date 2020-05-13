package com.trivadis.sqldev.example110;

import java.awt.Component;
import java.awt.Container;
import java.awt.IllegalComponentStateException;
import java.awt.MenuContainer;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import oracle.dbtools.raptor.controls.grid.RaptorGridTable;
import oracle.dbtools.raptor.oviewer.xmleditor.XMLBasedEditor;
import oracle.dbtools.worksheet.StatementRunnerResultsPanel;
import oracle.dbtools.worksheet.WorksheetResultPanel;
import oracle.dbtools.worksheet.editor.Worksheet;
import oracle.ide.Context;
import oracle.ide.Ide;
import oracle.ide.controller.Controller;
import oracle.ide.controller.IdeAction;
import oracle.ide.controls.MenuItem;
import oracle.ide.view.View;

public class ExampleController implements Controller {

	private static final Logger logger = Logger.getLogger(ExampleController.class.getName());
	private static final int EXAMPLE_CONTEXT_MENU_1_CMD_ID = Ide.findOrCreateCmdID("EXAMPLE_CONTEXT_MENU_1_ACTION_ID");
	private static final int EXAMPLE_CONTEXT_MENU_2_CMD_ID = Ide.findOrCreateCmdID("EXAMPLE_CONTEXT_MENU_2_ACTION_ID");
	public static final IdeAction EXAMPLE_CONTEXT_MENU_1_ACTION = IdeAction.get(EXAMPLE_CONTEXT_MENU_1_CMD_ID);
	public static final IdeAction EXAMPLE_CONTEXT_MENU_2_ACTION = IdeAction.get(EXAMPLE_CONTEXT_MENU_2_CMD_ID);
			
	public ExampleController() {
		super();
		logger.fine("controller initialized.");
	}
	
	private static String getSelectedValue(RaptorGridTable tab) {
		int selRow = tab.getSelectedRow();
		int selCol = tab.getSelectedColumn();
		String selectedValue = tab.getDisplayValueAt(selRow, selCol, -1);
		return selectedValue;
	}
	
	private static String getSelectedValue(Component component) {
		if (component instanceof RaptorGridTable) {
			return getSelectedValue((RaptorGridTable)component);
		}
		return "";
	}

	private static List<Component> getAllComponents(Container container) {
		List<Component> components = new ArrayList<>();
		for (Component c : container.getComponents()) {
			components.add(c);
			if (c instanceof Container) {
				components.addAll(getAllComponents((Container) c));
			}
		}
		return components;
	}
	
	private static Point getLocation(Context context) {
		Object source = context.getEvent().getSource();
		if (source instanceof MenuItem) {
			 MenuContainer parent = ((MenuItem)source).getParent();
			 if (parent instanceof JPopupMenu) {
				 JPopupMenu menu = (JPopupMenu)parent;
				 try {
					 return menu.getLocationOnScreen();
				 } catch (IllegalComponentStateException e) {
					 logger.warning("cannot get location of popup menu.");
				 }
			 }
		}
		return MouseInfo.getPointerInfo().getLocation();
	}

	private void show (String message, Context context) {
		View view = context.getView();
		Component viewGui = view.getGUI();
		String selectedValue = "";
		if (viewGui instanceof RaptorGridTable) {
			selectedValue = getSelectedValue(viewGui);
		} else if (view instanceof XMLBasedEditor) {
			if (viewGui instanceof JSplitPane) {
				Point location = getLocation(context);
				for (Component c : getAllComponents(((JSplitPane)viewGui))) {
					if (c instanceof RaptorGridTable) {
						Rectangle rect = SwingUtilities.convertRectangle(c, c.getBounds(), null);
						if (rect.contains(location)) {
							selectedValue = getSelectedValue(c);
						}
					}
				}
			} else {
				selectedValue = getSelectedValue(((XMLBasedEditor)view).getMainUI().getUI());
			}
		} else if (view instanceof Worksheet) {
			Worksheet worksheet = (Worksheet)view;
			WorksheetResultPanel gui = worksheet.getSelectedResultPanel();
			if (gui instanceof StatementRunnerResultsPanel) {
				selectedValue = getSelectedValue(((StatementRunnerResultsPanel)gui).getResultSetTable());
			}
		}
		JOptionPane.showMessageDialog(null, message + ": " + selectedValue, ExampleResources.getString("DIALOG_SHOW_TITLE"), JOptionPane.INFORMATION_MESSAGE);
	}
			
	@Override
	public boolean handleEvent(IdeAction action, Context context) {
		int cmdId = action.getCommandId();
		if (cmdId == EXAMPLE_CONTEXT_MENU_1_CMD_ID) {
			show("1", context);
		} else if (cmdId == EXAMPLE_CONTEXT_MENU_2_CMD_ID) {
			show("2", context);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean update(IdeAction action, Context context) {
		action.setEnabled(true);
		return action.isEnabled();
	}

}
