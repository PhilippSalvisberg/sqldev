package com.trivadis.sqldev.example90;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;

import oracle.dbtools.raptor.utils.Connections;
import oracle.ide.Context;
import oracle.ide.Ide;
import oracle.ide.config.Preferences;
import oracle.ide.controller.Controller;
import oracle.ide.controller.IdeAction;
import oracle.ide.editor.Editor;
import oracle.javatools.db.DBException;

public class ExampleController implements Controller {

	private static final Logger logger = Logger.getLogger(ExampleController.class.getName());
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
				final String connName = (String) context.getProperty("ObjectAction.CONN_NAME");
				Connections conns = null;
				try {
					 conns = Connections.getInstance();
				} catch (Throwable e) {
					// this happens with wrong/incomplete bundle dependencies/imports in pom.xml
					logger.severe("Cannot get Connections. Got error " + e);
					show("Connection " + connName + "\nGot error: " + e);
					return;
				}
				PreferenceModel preferences = PreferenceModel.getInstance(Preferences.getPreferences());
				String schema;
				String password;
				String customUrl;
				String productName;
				String productVersion;
				logger.fine("connName: " + connName);
				try {
					schema = conns.getConnectionInfo(connName).getProperty("user");;
					if (preferences.isShowPassword()) {
						password = conns.getConnectionInfo(connName).getProperty("password");
					} else {
						password = "***";
					}
					customUrl = conns.getConnectionInfo(connName).getProperty("customUrl");
					if (conns.isConnectionOpen(connName) || preferences.isImplicitConnect()) {
						Connection conn = conns.getConnection(connName);
						productName = conn.getMetaData().getDatabaseProductName();
						productVersion = conn.getMetaData().getDatabaseProductVersion();
					} else {
						productName = "?";
						productVersion = "(not connected)";
					}

				} catch (DBException | SQLException e) {
					logger.severe("got error: " + e);
					return;
				}
				final String message = "Connected to "
						+ schema + "/"+ password 
						+ customUrl.substring(customUrl.indexOf("@")) + "\n\n" 
						+ "using " + productName + " " + productVersion + ".";
				show(message);
			}
		}
		Thread t = new Thread(new AsyncRunner(context));
		t.start();
	}
		
	private void handleEditor(Context context) {
		final Editor editor = (Editor) context.getView();
		final JEditorPane pane = (JEditorPane) editor.getDefaultFocusComponent();
		final String text = pane.getText();
		final int numChars = text.length();
		final int numLines = text.split(System.getProperty("line.separator")).length;
		final int pos = pane.getCaretPosition();
		final String message = "Editor '" + editor.getTabDescription() + "'\n" 
				+ "is at position " + pos + ".\n"
				+ "Text has " + numChars + " characters and " + numLines + " lines."; 
		show(message);
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
