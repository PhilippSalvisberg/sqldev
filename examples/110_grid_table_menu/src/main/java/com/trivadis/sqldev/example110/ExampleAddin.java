package com.trivadis.sqldev.example110;

import java.util.logging.Logger;

import oracle.dbtools.raptor.controls.grid.RaptorGridTable;
import oracle.dbtools.raptor.controls.grid.contextmenu.GridContextMenuItem;
import oracle.ide.Addin;

public class ExampleAddin implements Addin {

	private static final Logger logger = Logger.getLogger(ExampleAddin.class.getName());
	
	@Override
	public void initialize() {
		GridContextMenuItem menuListener = new ExampleGridContextMenuItem();
		RaptorGridTable.addGridContextMenu(menuListener);
		logger.fine("addin initialized.");
	}

}
