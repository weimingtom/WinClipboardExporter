package com.iteye.weimingtom.wce.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.iteye.weimingtom.wce.db.DBException;
import com.iteye.weimingtom.wce.model.ArticleInfo;
import com.iteye.weimingtom.wce.service.ArticleService;
import com.iteye.weimingtom.wce.util.JobQueue;
import com.iteye.weimingtom.wce.util.UIUtil;

public class ArticleInfoDialog {
	private static final String TITLE_ADD = "新建文章";
	private static final String TITLE_EDIT = "编辑文章";
	
	private final static int SHELL_WIDTH = 600;
	private final static int SHELL_HEIGHT = 400;
	
	public static final int OK = 0;
	public static final int CANCEL = 1;
	protected int result = CANCEL;
	
	private Shell parent;
	private Shell shell;
	private Composite compositeBody;
	private Composite compositeFoot;
	private ArticleInfoComposite articleInfo;
	
	private ArticleInfo mInfo;
	
	/**
	 * @see ContactInfoComposite
	 */
	private int type; 

	public ArticleInfoDialog(Shell parent, int type) {
		this.parent = parent;
		this.type = type;
	}
	
	public void setData(ArticleInfo info) {
		mInfo = info;
	}

	private void fillData() {
		articleInfo.setArticleInfo(mInfo);
	}
	
	public int open() {
		createShell();
		createBodyContents();
		createFootContents();
		fillData();
		
		FormData data;
		
		data = new FormData();
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(100, -5);
		data.top = new FormAttachment(0, 5);
		data.bottom = new FormAttachment(compositeFoot, -5);
		compositeBody.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(100, -5);
		data.bottom = new FormAttachment(100, -5);
		compositeFoot.setLayoutData(data);
		
		//shell.pack();
		//UIUtil.setShellCenter(shell, shell.getSize().x, shell.getSize().y);
		UIUtil.setShellCenter(shell, SHELL_WIDTH, SHELL_HEIGHT);
		shell.open();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}
	
	private void close(int result) {
		if (shell != null && !shell.isDisposed()) {
			this.result = result;
			shell.close();
		}
	}

	private void createShell() {
		shell = new Shell(parent, 
				//SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
				SWT.MAX | SWT.MIN | SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setLayout(new FormLayout());
		//shell.setSize(SHELL_WIDTH, SHELL_HEIGHT);
		switch(this.type) {
		case ContactInfoComposite.TYPE_ADD:
			shell.setText(TITLE_ADD);
			break;
		
		case ContactInfoComposite.TYPE_EDIT:
			shell.setText(TITLE_EDIT);
			break;
		}
	}
	
	private void createBodyContents() {		
		compositeBody = new Composite(shell, SWT.NONE);
		compositeBody.setLayout(new FillLayout(SWT.HORIZONTAL));
		articleInfo = new ArticleInfoComposite(compositeBody, type);
	}
	
	private void createFootContents() {
		compositeFoot = new Composite(shell, SWT.NONE);
		//new GridData(GridData.HORIZONTAL_ALIGN_CENTER)
		compositeFoot.setLayout(new GridLayout(1, false));
		
		Composite compositeFootCenter = new Composite(compositeFoot, SWT.NONE);
		compositeFootCenter.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		compositeFootCenter.setLayout(new GridLayout(2, false));
		
		Button okButton = new Button(compositeFootCenter, SWT.PUSH);
		okButton.setLayoutData(new GridData(100, SWT.DEFAULT));
		okButton.setText("确定");
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				executeOK();
				//close();
			}
		});
		
		Button cancelButton = new Button(compositeFootCenter, SWT.PUSH);
		cancelButton.setLayoutData(new GridData(100, SWT.DEFAULT));
		cancelButton.setText("取消");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				close(CANCEL);
			}
		});

		//shell.setDefaultButton(okButton);
	}
	
	private void executeOK() {
		switch(this.type) {
		case ArticleInfoComposite.TYPE_ADD:
			addArticle();
			break;
		
		case ArticleInfoComposite.TYPE_EDIT:
			updateArticle();
			break;
		}
	}
	
	private void addArticle() {
		JobQueue.getInstance().addJob(new InsertArticleTask());
	}
	
	private void updateArticle() {
		JobQueue.getInstance().addJob(new UpdateArticleTask());
	}
	
	private class InsertArticleTask implements Runnable {
		private ArticleInfo info;
		
		public InsertArticleTask() {
			info = articleInfo.getArticleInfo();
		}
		
		@Override
		public void run() {
			final String error = insert();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (error != null) {
						UIUtil.showMessage("插入数据错误：" + error);
					} else {
						close(OK);
					}
				}
			});
		}
		
		private String insert() {
			try {
				ArticleService.getInstance().insert(info);
				return null;
			} catch (DBException e) {
				e.printStackTrace();
				return e.toString();
			}
		}
	}
	
	private class UpdateArticleTask implements Runnable {
		private ArticleInfo info;
		
		public UpdateArticleTask() {
			info = articleInfo.getArticleInfo();
		}
		
		@Override
		public void run() {
			final String error = update();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (error != null) {
						UIUtil.showMessage("更新数据错误：" + error);
					} else {
						close(OK);
					}
				}
			});
		}
		
		private String update() {
			try {
				ArticleService.getInstance().update(info);
				return null;
			} catch (DBException e) {
				e.printStackTrace();
				return e.toString();
			}
		}
	}
	
}
