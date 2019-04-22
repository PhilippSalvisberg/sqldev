package com.trivadis.sqldev.example90;

import oracle.javatools.data.HashStructure;
import oracle.javatools.data.HashStructureAdapter;
import oracle.javatools.data.PropertyStorage;

public class PreferenceModel extends HashStructureAdapter {

	private static final String DATA_KEY = "example90";
	private static final String KEY_SHOW_PASSWORD = "showPassword";
	private static final String KEY_IMPLICIT_CONNECT = "implicitConnect";

	private PreferenceModel(final HashStructure hash) {
		super(hash);
	}

	public static PreferenceModel getInstance(final PropertyStorage prefs) {
		return new PreferenceModel(HashStructureAdapter.findOrCreate(prefs, PreferenceModel.DATA_KEY));
	}

	public boolean isShowPassword() {
		return this.getHashStructure().getBoolean(PreferenceModel.KEY_SHOW_PASSWORD, true);
	}

	public void setShowPassword(final boolean showPassword) {
		this.getHashStructure().putBoolean(PreferenceModel.KEY_SHOW_PASSWORD, showPassword);
	}

	public boolean isImplicitConnect() {
		return this.getHashStructure().getBoolean(PreferenceModel.KEY_IMPLICIT_CONNECT, false);
	}

	public void setImplicitConnect(final boolean implicitConnect) {
		this.getHashStructure().putBoolean(PreferenceModel.KEY_IMPLICIT_CONNECT, implicitConnect);
	}

}
