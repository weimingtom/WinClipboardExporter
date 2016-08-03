package com.iteye.weimingtom.wce.app;

import com.iteye.weimingtom.wce.ui.MainWindow;

public class AppData {
	private static AppData appData;
	private static MainWindow mainWin;
	
	private AppData(){
		
	}
	
	public static AppData getInstance(){
		if(appData == null){
			appData = new AppData();
		}
		return appData;
	}
	
	public MainWindow getMainWin() {
		return mainWin;
	}

	public void setMainWin(MainWindow mainWin) {
		AppData.mainWin = mainWin;
	}	
}
