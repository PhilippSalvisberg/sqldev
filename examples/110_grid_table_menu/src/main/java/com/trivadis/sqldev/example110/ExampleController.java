package com.trivadis.sqldev.example110;

import java.awt.Component;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import javax.swing.JOptionPane;

import oracle.dbtools.raptor.controls.grid.RaptorGridTable;
import oracle.dbtools.raptor.oviewer.xmleditor.XMLBasedEditor;
import oracle.dbtools.worksheet.StatementRunnerResultsPanel;
import oracle.dbtools.worksheet.WorksheetResultPanel;
import oracle.dbtools.worksheet.editor.Worksheet;
import oracle.ide.Context;
import oracle.ide.Ide;
import oracle.ide.controller.Controller;
import oracle.ide.controller.IdeAction;
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
	
	private static String getSelectedValue(RaptorGridTable tab, boolean allValues) {
		StringBuffer sb = new StringBuffer();
		int[] rows = tab.getCellSelectionEnabled() || tab.getRowSelectionAllowed() ? tab.getSelectedRows()
				: IntStream.range(0, tab.getRowCount()).toArray();
		for (int selRow : rows) {
			int[] cols = tab.getCellSelectionEnabled() || tab.getColumnSelectionAllowed() ? tab.getSelectedColumns()
					: IntStream.range(0, tab.getColumnCount()).toArray();
			for (int selCol : cols) {
				if (allValues || sb.length() == 0) {
					if (sb.length() > 0) {
						sb.append(", ");
					}
					sb.append(tab.getDisplayValueAt(selRow, selCol, -1));
				}
			}
		}
		return sb.toString();
	}
	
	private static String getSelectedValue(Component component, boolean allValues) {
		if (component instanceof RaptorGridTable) {
			return getSelectedValue((RaptorGridTable)component, allValues);
		}
		return null;
	}

	private void show (String message, Context context, boolean allValues) {
		View view = context.getView();
		Component viewGui = view.getGUI();
		String selectedValue = null;
		if (viewGui instanceof RaptorGridTable) {
			selectedValue = getSelectedValue(viewGui, allValues);
		} else if (view instanceof XMLBasedEditor) {
			if (ExampleContextMenu.getContextGridTable() != null) {
				// parent or child table; the selected table is not available in the context
				selectedValue = getSelectedValue(ExampleContextMenu.getContextGridTable(), allValues);
			} else {
				// parent table only (dead code)
				selectedValue = getSelectedValue(((XMLBasedEditor)view).getMainUI().getUI(), allValues);
			}
		} else if (view instanceof Worksheet) {
			Worksheet worksheet = (Worksheet)view;
			WorksheetResultPanel gui = worksheet.getSelectedResultPanel();
			if (gui instanceof StatementRunnerResultsPanel) {
				selectedValue = getSelectedValue(((StatementRunnerResultsPanel)gui).getResultSetTable(), allValues);
			}
		}
		JOptionPane.showMessageDialog(null, message + ": " + selectedValue, ExampleResources.getString("DIALOG_SHOW_TITLE"), JOptionPane.INFORMATION_MESSAGE);
	}
			
	@Override
	public boolean handleEvent(IdeAction action, Context context) {
		int cmdId = action.getCommandId();
		if (cmdId == EXAMPLE_CONTEXT_MENU_1_CMD_ID) {
			show("1", context, false);
		} else if (cmdId == EXAMPLE_CONTEXT_MENU_2_CMD_ID) {
			show("2", context, true);
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
