package com.trivadis.sqldev.example110;

import java.util.logging.Logger;

import javax.swing.JMenu;

import oracle.dbtools.raptor.controls.grid.RaptorGridTable;
import oracle.dbtools.raptor.controls.grid.contextmenu.GridContextMenuItem;
import oracle.ide.controller.ContextMenu;

public class ExampleContextMenu extends GridContextMenuItem {

	private static final Logger logger = Logger.getLogger(ExampleContextMenu.class.getName());
	private static RaptorGridTable gridTable;

	public static RaptorGridTable getContextGridTable() {
		// workaround to keep the selected table in case of parent/child reports
		return gridTable;
	}
	
	@Override
	protected boolean canShow(ContextMenu contextMenu) {
		gridTable = RaptorGridTable.getRaptorGridTable(contextMenu.getContext());
		if (gridTable != null) {
			return true;
		}
		return false;
	}

	@Override
	protected void createAndShowMenu(ContextMenu contextMenu) {
		logger.fine("show context menu");
		JMenu subMenu = contextMenu.createSubMenu(ExampleResources.getString("EXAMPLE_CONTEXT_SUBMENU_LABEL"),null,1,1);
		subMenu.add(contextMenu.createMenuItem(ExampleController.EXAMPLE_CONTEXT_MENU_1_ACTION));
		subMenu.add(contextMenu.createMenuItem(ExampleController.EXAMPLE_CONTEXT_MENU_2_ACTION));
		contextMenu.add(subMenu);
	}

}
