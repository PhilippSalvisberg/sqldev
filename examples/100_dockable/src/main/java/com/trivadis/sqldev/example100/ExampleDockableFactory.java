package com.trivadis.sqldev.example100;

import oracle.ide.IdeConstants;
import oracle.ide.docking.DockStation;
import oracle.ide.docking.Dockable;
import oracle.ide.docking.DockableFactory;
import oracle.ide.docking.DockingParam;
import oracle.ide.layout.ViewId;

public class ExampleDockableFactory implements DockableFactory {

	private ExampleDockable dockable;
	public static final String FACTORY_NAME = "EXAMPLE_FACTORY";

	
	@Override
	public void install() {
		final DockStation dockStation = DockStation.getDockStation();
		DockingParam dp = new DockingParam();
		dp.setPosition(IdeConstants.EAST);
		dockStation.dock(getLocalDockable(), dp);
	}	

	@Override
	public Dockable getDockable(ViewId viewId) {
		if (ExampleDockable.VIEW_ID.equals(viewId)) {
			return getLocalDockable();
		}
		return null;
	}
	
	private ExampleDockable getLocalDockable() {
		if (dockable == null) {
			dockable = new ExampleDockable();
		}
		return dockable;
	}
	
	public static ExampleDockable getDockable() {
		final DockStation dockStation = DockStation.getDockStation();
		final Dockable dockable = dockStation.findDockable(ExampleDockable.VIEW_ID);
		return (ExampleDockable)dockable;
	}
	

	public static void showDockable() {
		DockStation dockStation = DockStation.getDockStation();
		dockStation.setDockableVisible(getDockable(), true);
	}	
	
}
