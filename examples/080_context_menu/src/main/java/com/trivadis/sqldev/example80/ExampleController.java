package com.trivadis.sqldev.example80;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import oracle.dbtools.raptor.utils.Connections;
import oracle.ide.Context;
import oracle.ide.Ide;
import oracle.ide.controller.Controller;
import oracle.ide.controller.IdeAction;
import oracle.javatools.db.DBException;

public class ExampleController implements Controller {

	static final Logger logger = Logger.getLogger(ExampleController.class.getName());
	private static final int EXAMPLE_CONTEXT_MENU_1_CMD_ID = Ide.findOrCreateCmdID("EXAMPLE_CONTEXT_MENU_1_ACTION_ID");
	private static final int EXAMPLE_CONTEXT_MENU_2_CMD_ID = Ide.findOrCreateCmdID("EXAMPLE_CONTEXT_MENU_2_ACTION_ID");
	
	
	public ExampleController() {
		super();
	}
	
	private void show (String message) {
		JOptionPane.showMessageDialog(null, message, ExampleResources.getString("DIALOG_SHOW_TITLE"), JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void handleConnection(Context context) {
		class AsyncRunner implements Runnable {
			Context context;
			AsyncRunner(Context context) {
				this.context = context;
			}
			public void run() {
				String connName = (String) context.getProperty("ObjectAction.CONN_NAME");
				String productName;
				String productVersion;
				logger.info("connName: " + connName);
				try {
					Connection conn = Connections.getInstance().getConnection(connName);
					productName = conn.getMetaData().getDatabaseProductName();
					productVersion = conn.getMetaData().getDatabaseProductVersion();
				} catch (DBException | SQLException e) {
					logger.severe("got error: " + e);
					throw new RuntimeException(e);
				}
				String message = "Connected to " + productName + " " + productVersion + ".";
				show(message);
			}
		}
		Thread t = new Thread(new AsyncRunner(context));
		t.start();
	}
		
	private void handleEditor(Context context) {
		show("Editor");		
	}

	@Override
	public boolean handleEvent(IdeAction action, Context context) {
		int cmdId = action.getCommandId();
		if (cmdId == EXAMPLE_CONTEXT_MENU_1_CMD_ID) {
			handleConnection(context);			
		} else if (cmdId == EXAMPLE_CONTEXT_MENU_2_CMD_ID) {
			handleEditor(context);
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
