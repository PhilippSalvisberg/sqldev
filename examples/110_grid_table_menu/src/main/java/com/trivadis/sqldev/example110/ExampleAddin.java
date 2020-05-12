package com.trivadis.sqldev.example110;

import java.util.logging.Logger;

import oracle.ide.Addin;
import oracle.ide.editor.EditorManager;

public class ExampleAddin implements Addin {
	
	private static final Logger logger = Logger.getLogger(ExampleAddin.class.getName());

	@Override
	public void initialize() {
		EditorManager.getEditorManager().getContextMenu().addContextMenuListener(new ExampleMenuListener());
		logger.fine("addin initialized.");
	}

}
