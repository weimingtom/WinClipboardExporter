package com.iteye.weimingtom.wce.app;

import java.sql.SQLException;

import com.iteye.weimingtom.wce.db.SqliteDataSourceManager;
import com.iteye.weimingtom.wce.ui.MainWindow;
import com.iteye.weimingtom.wce.util.JobQueue;
import com.iteye.weimingtom.wce.util.UIUtil;

public class AppMain {
	public final static void main(final String args[]) throws ClassNotFoundException, SQLException {		
		JobQueue.getInstance().start();
		try {
			final AppMain appMain = new AppMain();
			appMain.init();
		} catch (Throwable e) {
			e.printStackTrace();
			UIUtil.showMessage2(e.toString());
		} finally {
			SqliteDataSourceManager.getInstance().close();
			JobQueue.getInstance().stop();
		}
	}
	
	public void init() {		
		final MainWindow mainWin = new MainWindow();
		AppData.getInstance().setMainWin(mainWin);
		try {
			mainWin.open();
		} catch (Throwable e) {
			e.printStackTrace();
			UIUtil.showMessage2(e.toString());
		} finally {
			
		}
	}
}
