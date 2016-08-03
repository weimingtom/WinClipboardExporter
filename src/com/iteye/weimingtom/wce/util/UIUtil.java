package com.iteye.weimingtom.wce.util;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class UIUtil {
	public static void showMessage(String mess){
		Display display = Display.getDefault();
		if (display != null) {
			Shell shell = display.getActiveShell();
			if (shell == null) {
				shell = new Shell(display);
			}
			MessageBox mb = new MessageBox(shell);
			mb.setMessage(mess);
			mb.setText("提示");
			mb.open();
		}
	}
	
	public static void setDate(DateTime dt, Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		dt.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
	}
	
	public static void showMessage2(String mess) {
		Display display = Display.getDefault();
		if (display != null) {
			final MessageBox messageBox = new MessageBox(
					new Shell(display), SWT.OK | SWT.ICON_ERROR /* 33 */);
			messageBox.setText("错误");
			messageBox.setMessage(mess);
			messageBox.open();
		}
	}
	
	public static final void setShellCenter(final Shell shell, final int posX, final int posY) {
		final Rectangle rect = shell.getDisplay().getBounds();
		if (rect.width > posX) {
			if (rect.height > posY) {
				shell.setSize(posX, posY);
				shell.setLocation((rect.width - posX) / 2,
						(rect.height - posY) / 2);
			} else {
				shell.setSize(posX, posY);
				shell.setLocation((rect.width - posX) / 2, rect.height);
			}
		} else if (rect.height > posY) {
			shell.setSize(posX, posY);
			shell.setLocation(rect.width, (rect.height - posY) / 2);
		} else {
			shell.setSize(posX, posY);
			shell.setLocation(rect.width, rect.height);
		}
	}
	
	/**
	 * @see http://git.eclipse.org/c/platform/eclipse.platform.swt.git/tree/examples/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet274.java
	 * @param item
	 * @param checked
	 * @param grayed
	 */
	public static void checkPath(TreeItem item, boolean checked, boolean grayed) {
	    if (item == null) return;
	    if (grayed) {
	        checked = true;
	    } else {
	        int index = 0;
	        TreeItem[] items = item.getItems();
	        while (index < items.length) {
	            TreeItem child = items[index];
	            if (child.getGrayed() || checked != child.getChecked()) {
	                checked = grayed = true;
	                break;
	            }
	            index++;
	        }
	    }
	    item.setChecked(checked);
	    item.setGrayed(grayed);
	    checkPath(item.getParentItem(), checked, grayed);
	}

	public static void checkItems(TreeItem item, boolean checked) {
	    item.setGrayed(false);
	    item.setChecked(checked);
	    TreeItem[] items = item.getItems();
	    for (int i = 0; i < items.length; i++) {
	        checkItems(items[i], checked);
	    }
	}
	
	public static void addTreeCheckBoxBehavior(Tree tree) {
	    tree.addListener(SWT.Selection, new Listener() {
	        @Override
			public void handleEvent(Event event) {
	            if (event.detail == SWT.CHECK) {
	                TreeItem item = (TreeItem) event.item;
	                boolean checked = item.getChecked();
	                checkItems(item, checked);
	                checkPath(item.getParentItem(), checked, false);
	            }
	        }
	    });
	}
}
