package com.trivadis.sqldev.example100;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import oracle.ide.docking.DockableWindow;
import oracle.ide.layout.ViewId;

public class ExampleDockable extends DockableWindow {

	private static final String VIEW_NAME = "EXAMPLE_VIEW";
	public static final ViewId VIEW_ID = new ViewId(ExampleDockableFactory.FACTORY_NAME, VIEW_NAME);
	private ImageIcon icon;
	private JLabel label;
	private JScrollPane pane;
	private JPanel panel;
	
	@Override
	public String getTitleName() {
		return ExampleResources.getString("DOCKABLE_TITLE");
	}

	@Override
	public Component getGUI() {
		if (icon == null) {
			initializeGUI();
		}
		return panel;
	}

	private void initializeGUI() {
		icon = new ImageIcon(ExampleResources.getImage("BIG_ONE_ICON"));
		label = new JLabel();
		label.setIcon(icon);
		label.setHorizontalAlignment(JLabel.CENTER);
		pane = new JScrollPane(label);
		panel = new JPanel(new BorderLayout());
		panel.add(pane, BorderLayout.CENTER);
	}
	
	
}
