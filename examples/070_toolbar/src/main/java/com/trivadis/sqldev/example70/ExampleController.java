package com.trivadis.sqldev.example70;

import javax.swing.JOptionPane;

import oracle.ide.Context;
import oracle.ide.controller.Controller;
import oracle.ide.controller.IdeAction;

public class ExampleController implements Controller {

	public ExampleController() {
		super();
	}
	
	private void show (String message) {
		JOptionPane.showMessageDialog(null, message, ExampleResources.getString("DIALOG_SHOW_TITLE"), JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public boolean handleEvent(IdeAction action, Context context) {
		show("Action Name: " + action.getValue("Name"));
		return true;
	}

	@Override
	public boolean update(IdeAction action, Context context) {
		action.setEnabled(true);
		return action.isEnabled();
	}

}
