package com.iteye.weimingtom.wce.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com.iteye.weimingtom.wce.app.AppData;
import com.iteye.weimingtom.wce.db.DBException;
import com.iteye.weimingtom.wce.model.ArticleInfo;
import com.iteye.weimingtom.wce.service.ArticleService;
import com.iteye.weimingtom.wce.util.JobQueue;
import com.iteye.weimingtom.wce.util.UIUtil;

public class ArticleTab extends Composite {
	private static final boolean USE_VIRTUAL = true;
	private static final boolean USE_PACK = false;
	
	private final static int MAX_WIDTH = 640;
	
	public Tree tree;
	public Color textBGColor = new Color(Display.getCurrent(),
			new RGB(0, 0xff, 0));
	
	private String[] titles = { " ", "序号", "文章名称", "文章地点", "文章内容", "创建时间", "修改时间" };
	
	private volatile List<ArticleInfo> results;

	public ArticleTab(final Composite composite) {
		super(composite, 0);
		this.setLayout(new GridLayout(1, false));
		
		Composite containerHeaderArchieved = new Composite(this, SWT.NONE);
		containerHeaderArchieved.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		containerHeaderArchieved.setLayout(new GridLayout(2, false));
		Button buttonCurrentArticles = new Button(containerHeaderArchieved, SWT.RADIO);
		buttonCurrentArticles.setText("最近文章");
		buttonCurrentArticles.setSelection(true);
		Button buttonArchievedArticles = new Button(containerHeaderArchieved, SWT.RADIO);
		buttonArchievedArticles.setText("历史文章");		
		
		tree = new Tree(this, /*SWT.CHECK | */SWT.VIRTUAL | SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION
				| SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(GridData.FILL_BOTH));
		for (int i = 0; i < titles.length; i++) {
			TreeColumn column = new TreeColumn(tree, SWT.NONE);
			column.setText(titles[i]);
		}

		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {
				super.mouseDown(event);
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent event) {
				super.mouseDoubleClick(event);
				Point point = new Point (event.x, event.y);
				TreeItem item = tree.getItem (point);
				//System.out.println("Mouse double click: " + item);
				if (item != null && item.getData() != null) {
					ArticleInfoDialog dialog = new ArticleInfoDialog(getParent().getShell(), 
						ArticleInfoComposite.TYPE_EDIT);
					ArticleInfo info = (ArticleInfo)item.getData();
					dialog.setData(info);
					if (dialog.open() == ArticleInfoDialog.OK) {
						AppData.getInstance().getMainWin().refreshArticleData(true);
					}
				}
			}
		});
		
	    tree.addSelectionListener(new SelectionAdapter() {
	        @Override
			public void widgetSelected(SelectionEvent event) {
	        	if (event.detail == SWT.CHECK) {
	                TreeItem item = (TreeItem) event.item;
	                boolean checked = item.getChecked();
	                UIUtil.checkItems(item, checked);
	                UIUtil.checkPath(item.getParentItem(), checked, false);
	            } else {
	                TreeItem item = (TreeItem) event.item;
	                ArticleInfo info = null;
	                if (item != null && item.getData() != null) {
	            		info = (ArticleInfo) item.getData();
	            	}
	            	AppData.getInstance().getMainWin().showDetail(info);
	            }
			}
	    });
	    
	    setupPopupMenu();
		
		Composite containerHeaderEdit = new Composite(this, SWT.NONE);
		containerHeaderEdit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		containerHeaderEdit.setLayout(new GridLayout(7, false));		

		final Label labelSearch = new Label(containerHeaderEdit, SWT.PUSH);
		labelSearch.setText("文章搜索：");
		final Text textSearch = new Text(containerHeaderEdit, SWT.BORDER);
		textSearch.setLayoutData(new GridData(150, SWT.DEFAULT));
		
		final Button buttonAdd = new Button(containerHeaderEdit, SWT.PUSH);
		buttonAdd.setText("新增...");
		buttonAdd.setLayoutData(new GridData(100, SWT.DEFAULT));
		buttonAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AppData.getInstance().getMainWin().addArticles();
			}
		});
		final Button buttonImport = new Button(containerHeaderEdit, SWT.PUSH);
		buttonImport.setText("导入...");
		buttonImport.setLayoutData(new GridData(100, SWT.DEFAULT));
		final Button buttonExport = new Button(containerHeaderEdit, SWT.PUSH);
		buttonExport.setText("导出...");
		buttonExport.setLayoutData(new GridData(100, SWT.DEFAULT));
		
		
		if (!USE_PACK) {
			for (int i = 0; i < titles.length; i++) {
				if (i == 0) {
					tree.getColumn(i).setWidth(50);
				} else {
					tree.getColumn(i).setWidth(100);
				}
			}
		}
		if (USE_VIRTUAL) {
			tree.addListener(SWT.SetData, new Listener() {
				@Override
				public void handleEvent(Event event) {
					TreeItem item = (TreeItem)event.item;
					TreeItem parentItem = item.getParentItem();
					String text = null;
					if (parentItem == null) {
						ArticleInfo info = results.get(tree.indexOf(item));
						fillItem(item, info);
					}
					if (USE_PACK) {
						packTable();
					}
				}
			});
		}
		
		reloadData(false);
	}
	
	private void setupPopupMenu() {
		final Menu menuPopup = new Menu(this.getShell(), SWT.POP_UP);
		final MenuItem itemDelete = new MenuItem(menuPopup, SWT.PUSH);
		itemDelete.setText("删除文章...");
		menuPopup.addListener(SWT.Show, new Listener () {
			@Override
			public void handleEvent(Event event) {
				TreeItem[] treeItems = tree.getSelection();
				List<ArticleInfo> infoList = new ArrayList<ArticleInfo>();
				if (treeItems != null) {
					for (TreeItem treeItem : treeItems) {
						ArticleInfo info = (ArticleInfo)treeItem.getData();
						if (info != null) {
							infoList.add(info);
						}
					}
				}
				if (infoList.size() > 0) {
					itemDelete.setEnabled(true);
				} else {
					itemDelete.setEnabled(false);
				}
			}
		});
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] treeItems = tree.getSelection();
				List<ArticleInfo> infoList = new ArrayList<ArticleInfo>();
				if (treeItems != null) {
					for (TreeItem treeItem : treeItems) {
						ArticleInfo info = (ArticleInfo)treeItem.getData();
						if (info != null) {
							infoList.add(info);
						}
					}
				}
				if (infoList.size() > 0) {
					MessageBox mb = new MessageBox(getShell(), SWT.OK | SWT.CANCEL | SWT.ICON_QUESTION);
					mb.setMessage("确定要删除选择的文章？");
					mb.setText("提示");
					if (mb.open() == SWT.OK) {
						deleteArticle(infoList);
					}
				}
			}
		});
		tree.setMenu(menuPopup);
	}
	
	public void reloadData(boolean refreshDetail) {
		if (!USE_VIRTUAL) {
			tree.removeAll();
		}
		if (USE_PACK) {
			packTable();
		}
		JobQueue.getInstance().addJob(new LoadArticleTask(refreshDetail));
	}
	
	public void deleteArticle(List<ArticleInfo> infoList) {
		JobQueue.getInstance().addJob(new DeleteArticleTask(infoList));
	}
	
	private void packTable() {
		for (int i = 0; i < titles.length; i++) {
			tree.getColumn(i).pack();
		}
		if (tree.getColumn(titles.length - 1).getWidth() < MAX_WIDTH) {
			tree.getColumn(titles.length - 1).setWidth(MAX_WIDTH);
		}
	}
	
	private void fillTable() {
		results = ArticleService.getInstance().getAll();
	}
	
	private void fillTableOld() {
		for (ArticleInfo info : results) {
			appendRow(info);
		}
	}
	
	private void appendRow(ArticleInfo info) {
		// System.out.println(str);
		final TreeItem item;
		item = new TreeItem(tree, SWT.NONE);
		fillItem(item, info);
	}
	
	private void fillItem(TreeItem item, ArticleInfo info) {
		item.setText(1, Integer.toString(info.getArticleId()));
		item.setText(2, info.getName());
		item.setText(3, info.getLocation());
		item.setText(4, info.getDescription());
		item.setText(5, info.getCreateTime());
		item.setText(6, info.getModifyTime());
		item.setData(info);
		item.setItemCount(0);
	}
	
	private class LoadArticleTask implements Runnable {
		private boolean refreshDetail = false;
		
		public LoadArticleTask(boolean refreshDetail) {
			this.refreshDetail = refreshDetail;
		}
		
		@Override
		public void run() {
			if (USE_VIRTUAL) {
				fillTable();
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						if (!tree.isDisposed()) {
							tree.setItemCount(results.size());
							tree.clearAll(true);
							doRefreshDetail();
						}
					}
				});
			} else {
				results = ArticleService.getInstance().getAll();
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						if (!tree.isDisposed()) {
							fillTableOld();
							if (USE_PACK) {
								packTable();
							}
							doRefreshDetail();
						}
					}
				});
			}
		}
		
		private void doRefreshDetail() {
			if (refreshDetail) {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						TreeItem[] treeItems = tree.getSelection();
						if (treeItems.length > 0) {
							ArticleInfo info = (ArticleInfo)treeItems[0].getData();
							if (info != null) {
								AppData.getInstance().getMainWin().showDetail(info);
							}
						}
					}
				});
			}
		}
	}
	
	private class DeleteArticleTask implements Runnable {
		private List<ArticleInfo> infoList;
		
		public DeleteArticleTask(List<ArticleInfo> infoList) {
			this.infoList = infoList;
		}
		
		@Override
		public void run() {
			final String error = delete();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (error != null) {
						UIUtil.showMessage("删除数据错误：" + error);
					} else {
						AppData.getInstance().getMainWin().refreshArticleData(true);
					}
				}
			});
		}
		
		private String delete() {
			try {
				ArticleService.getInstance().delete(infoList);
				return null;
			} catch (DBException e) {
				e.printStackTrace();
				return e.toString();
			}
		}
	}
}
