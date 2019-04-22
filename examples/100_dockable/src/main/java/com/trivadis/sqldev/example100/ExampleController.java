package com.trivadis.sqldev.example100;

import oracle.ide.Context;
import oracle.ide.controller.Controller;
import oracle.ide.controller.IdeAction;

public class ExampleController implements Controller {
	
	public ExampleController() {
		super();
	}
	
	@Override
	public boolean handleEvent(IdeAction action, Context context) {
		ExampleDockableFactory.showDockable();
		return true;
	}

	@Override
	public boolean update(IdeAction action, Context context) {
		action.setEnabled(true);
		return action.isEnabled();
	}

}
