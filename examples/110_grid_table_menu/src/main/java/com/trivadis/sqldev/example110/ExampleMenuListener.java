package com.trivadis.sqldev.example110;

import java.util.logging.Logger;

import javax.swing.JMenu;

import oracle.ide.Context;
import oracle.ide.controller.ContextMenu;
import oracle.ide.controller.ContextMenuListener;

public class ExampleMenuListener implements ContextMenuListener {
	
	private static final Logger logger = Logger.getLogger(ExampleMenuListener.class.getName());
	
	public ExampleMenuListener() {
		super();
		logger.fine("menu listener initialized.");
	}
	
	@Override
	public boolean handleDefaultAction(Context context) {
		return false;
	}

	@Override
	public void menuWillHide(ContextMenu contextMenu) {
		logger.fine("hide context menu");
	}

	@Override
	public void menuWillShow(ContextMenu contextMenu) {
		logger.fine("show context menu");
		JMenu subMenu = contextMenu.createSubMenu(ExampleResources.getString("EXAMPLE_CONTEXT_SUBMENU_LABEL"),null,1,1);
		subMenu.add(contextMenu.createMenuItem(ExampleController.EXAMPLE_CONTEXT_MENU_1_ACTION));
		subMenu.add(contextMenu.createMenuItem(ExampleController.EXAMPLE_CONTEXT_MENU_2_ACTION));
		contextMenu.add(subMenu);
	}

}
