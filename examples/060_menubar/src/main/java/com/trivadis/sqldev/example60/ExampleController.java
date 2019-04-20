package com.trivadis.sqldev.example60;

import javax.swing.JOptionPane;

import oracle.ide.Context;
import oracle.ide.Ide;
import oracle.ide.controller.Controller;
import oracle.ide.controller.IdeAction;

public class ExampleController implements Controller {
	
	private static final int EXAMPLE_VIEW_MENU_1_CMD_ID = Ide.findOrCreateCmdID("EXAMPLE_VIEW_MENU_1_ACTION_ID");
	private static final int EXAMPLE_VIEW_MENU_2_CMD_ID = Ide.findOrCreateCmdID("EXAMPLE_VIEW_MENU_2_ACTION_ID");
	private static final int EXAMPLE_VIEW_MENU_3_CMD_ID = Ide.findOrCreateCmdID("EXAMPLE_VIEW_MENU_3_ACTION_ID");

	public ExampleController() {
		super();
	}
	
	private void show (String message) {
		JOptionPane.showMessageDialog(null, message, ExampleResources.getString("DIALOG_SHOW_TITLE"), JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public boolean handleEvent(IdeAction action, Context context) {
		int cmdId = action.getCommandId();
		if (cmdId == EXAMPLE_VIEW_MENU_1_CMD_ID) {
			show("View menu item 1");
		} else if (cmdId == EXAMPLE_VIEW_MENU_2_CMD_ID) {
			show("View menu item 2");
		} else if (cmdId == EXAMPLE_VIEW_MENU_3_CMD_ID) {
			show("View menu item 3");
		} else {
			show("Action CmdID: " + cmdId + " Name: " + action.getValue("Name"));
		}
		return true;
	}

	@Override
	public boolean update(IdeAction action, Context context) {
		action.setEnabled(true);
		return action.isEnabled();
	}

}
