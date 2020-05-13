package com.trivadis.sqldev.example110;

import java.util.logging.Logger;

import javax.swing.JMenu;

import oracle.dbtools.raptor.controls.grid.RaptorGridTable;
import oracle.dbtools.raptor.controls.grid.contextmenu.GridContextMenuItem;
import oracle.ide.Addin;
import oracle.ide.controller.ContextMenu;

public class ExampleAddin implements Addin {

	private static final Logger logger = Logger.getLogger(ExampleAddin.class.getName());
	private static RaptorGridTable gridTable;
	
	public static RaptorGridTable getContextGridTable() {
		// workaround to keep the selected table in case of parent/child reports
		return gridTable;
	}

	@Override
	public void initialize() {
		logger.fine("initializing...");
		GridContextMenuItem menuListener = new GridContextMenuItem() {
			/**
			 * PRECONDITION: _table will NOT be null
			 * 
			 * @param contextMenu the context menu being built
			 * @return true if createAndShowAction should be called
			 */
			@Override
			protected boolean canShow(ContextMenu contextMenu) {
				// and any other preconditions besides 'just' RaptorGridTable _table != null
				Object table = contextMenu.getContext().getProperty("RaptorGridTableCtxProperty");
				if (table instanceof RaptorGridTable) {
					gridTable = (RaptorGridTable) table;
					return true;
				}
				return false;
			}

			/**
			 * PRECONDITION: _table will NOT be null and canShow has returned true
			 * 
			 * @param contextMenu the context menu being built Add your menuItem(s) to the
			 *                    context menu
			 */
			@Override
			protected void createAndShowMenu(ContextMenu contextMenu) {
				logger.fine("show context menu");
				JMenu subMenu = contextMenu.createSubMenu(ExampleResources.getString("EXAMPLE_CONTEXT_SUBMENU_LABEL"),null,1,1);
				subMenu.add(contextMenu.createMenuItem(ExampleController.EXAMPLE_CONTEXT_MENU_1_ACTION));
				subMenu.add(contextMenu.createMenuItem(ExampleController.EXAMPLE_CONTEXT_MENU_2_ACTION));
				contextMenu.add(subMenu);
			}
		};
		RaptorGridTable.addGridContextMenu(menuListener);
		logger.fine("addin initialized.");
	}

}
