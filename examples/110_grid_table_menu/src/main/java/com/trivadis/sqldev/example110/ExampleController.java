package com.trivadis.sqldev.example110;

import java.util.logging.Logger;

import javax.swing.JOptionPane;

import oracle.ide.Context;
import oracle.ide.Ide;
import oracle.ide.controller.Controller;
import oracle.ide.controller.IdeAction;

public class ExampleController implements Controller {

	private static final Logger logger = Logger.getLogger(ExampleController.class.getName());
	private static final int EXAMPLE_CONTEXT_MENU_1_CMD_ID = Ide.findOrCreateCmdID("EXAMPLE_CONTEXT_MENU_1_ACTION_ID");
	private static final int EXAMPLE_CONTEXT_MENU_2_CMD_ID = Ide.findOrCreateCmdID("EXAMPLE_CONTEXT_MENU_2_ACTION_ID");
	private static final int EXAMPLE_CONTEXT_MENU_3_CMD_ID = Ide.findOrCreateCmdID("EXAMPLE_CONTEXT_MENU_3_ACTION_ID");
	public static final IdeAction EXAMPLE_CONTEXT_MENU_1_ACTION = IdeAction.get(EXAMPLE_CONTEXT_MENU_1_CMD_ID);
	public static final IdeAction EXAMPLE_CONTEXT_MENU_2_ACTION = IdeAction.get(EXAMPLE_CONTEXT_MENU_2_CMD_ID);
	public static final IdeAction EXAMPLE_CONTEXT_MENU_3_ACTION = IdeAction.get(EXAMPLE_CONTEXT_MENU_3_CMD_ID);
			
	public ExampleController() {
		super();
		logger.fine("controller initialized.");
	}
	
	private void show (String message) {
		JOptionPane.showMessageDialog(null, message, ExampleResources.getString("DIALOG_SHOW_TITLE"), JOptionPane.INFORMATION_MESSAGE);
	}
			
	@Override
	public boolean handleEvent(IdeAction action, Context context) {
		int cmdId = action.getCommandId();
		if (cmdId == EXAMPLE_CONTEXT_MENU_1_CMD_ID) {
			show("1");
		} else if (cmdId == EXAMPLE_CONTEXT_MENU_2_CMD_ID) {
			show("2");
		} else if (cmdId == EXAMPLE_CONTEXT_MENU_3_CMD_ID) {
			show("3");
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
