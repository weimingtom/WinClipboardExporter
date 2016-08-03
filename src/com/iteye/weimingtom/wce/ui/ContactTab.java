package com.iteye.weimingtom.wce.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
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
import com.iteye.weimingtom.wce.model.ContactGroupInfo;
import com.iteye.weimingtom.wce.model.ContactInfo;
import com.iteye.weimingtom.wce.service.ContactService;
import com.iteye.weimingtom.wce.util.JobQueue;
import com.iteye.weimingtom.wce.util.UIUtil;

public class ContactTab extends Composite {
	private static final boolean USE_VIRTUAL = true;
	private static final boolean USE_PACK = false;
	
	private final static int MAX_WIDTH = 640;
	
	public Tree tree;
	public Color textBGColor = new Color(Display.getCurrent(),
			new RGB(0, 0xff, 0));
	
	private String[] titles = { "组别", "序号", "编号", "姓名", "性别", "职务", "任职时间", "卡号", "照片", "手机", "传真", "电子邮箱"};
	
	private TreeItem parentItem;
	private int parentItemChildrenCount;
	
	private volatile List<ContactGroupInfo> groupInfoList;
	private volatile List<ContactInfo> results;
	
	private boolean isExpanding = false;
	
	public ContactTab(final Composite composite) {
		super(composite, 0);
		this.setLayout(new GridLayout(1, false));
		
		Composite containerHeaderSession = new Composite(this, SWT.NONE);
		containerHeaderSession.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		containerHeaderSession.setLayout(new GridLayout(2, false));
		final Combo comboSession = new Combo(containerHeaderSession, SWT.DROP_DOWN | SWT.READ_ONLY);
		comboSession.add("分类1");
		comboSession.add("分类2");
		comboSession.add("分类3");
		comboSession.add("分类4");
		comboSession.select(0);
		final Label labelHeader1 = new Label(containerHeaderSession, SWT.NONE);
		labelHeader1.setText("共1人");
		
		
		tree = new Tree(this, /*SWT.CHECK | */SWT.VIRTUAL | SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION
				| SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		//tree.setRedraw(true);
		tree.setLayoutData(new GridData(GridData.FILL_BOTH));
		for (int i = 0; i < titles.length; i++) {
			TreeColumn column = new TreeColumn(tree, SWT.NONE);
			column.setText(titles[i]);
		}
		tree.addTreeListener(new TreeAdapter() {
			@Override
			public void treeCollapsed(TreeEvent e) {
				super.treeCollapsed(e);
				expand();
			}

			@Override
			public void treeExpanded(TreeEvent e) {
				super.treeExpanded(e);
				expand();
			}
			
			private void expand() {
				//System.out.println("expand...");
				isExpanding = true;
				Display.getDefault().timerExec(500, new Runnable() {
					public void run() {
						isExpanding = false;
						//System.out.println("isGroupClick: " + isGroupClick);
					}
				});
			}
		});
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
				if (item != null && item.getData() != null && isExpanding == false) {
					ContactInfoDialog dialog = new ContactInfoDialog(getParent().getShell(), 
						ContactInfoComposite.TYPE_EDIT);
					ContactInfo info = (ContactInfo)item.getData();
					dialog.setData(info);
					if (dialog.open() == ContactInfoDialog.OK) {
						AppData.getInstance().getMainWin().refreshContactData(true);
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
	                ContactInfo info = null;
	                if (item != null && item.getData() != null) {
	            		info = (ContactInfo) item.getData();
	            	}
	            	AppData.getInstance().getMainWin().showDetail(info);
	            }
			}
	    });
	    
		setupPopupMenu();
	    
		Composite containerHeaderEdit = new Composite(this, SWT.NONE);
		containerHeaderEdit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		containerHeaderEdit.setLayout(new GridLayout(7, false));		

		final Label labelCatalog = new Label(containerHeaderEdit, SWT.PUSH);
		labelCatalog.setText("小类：");
		final Combo comboCatalog = new Combo(containerHeaderEdit, SWT.DROP_DOWN | SWT.READ_ONLY);
		comboCatalog.add("小类1");
		comboCatalog.add("小类2");
		final Label labelSearch = new Label(containerHeaderEdit, SWT.PUSH);
		labelSearch.setText("联系人搜索：");
		final Text textSearch = new Text(containerHeaderEdit, SWT.BORDER);
		textSearch.setLayoutData(new GridData(150, SWT.DEFAULT));
		
		final Button buttonAdd = new Button(containerHeaderEdit, SWT.PUSH);
		buttonAdd.setText("新增...");
		buttonAdd.setLayoutData(new GridData(100, SWT.DEFAULT));
		buttonAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AppData.getInstance().getMainWin().addContact();
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
					tree.getColumn(i).setWidth(200);
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
						ContactGroupInfo groupInfo = groupInfoList.get(tree.indexOf(item));
						text = groupInfo.getGroupName() + " (共" + groupInfo.getSize() + "人)";
						item.setText(text);
						item.setItemCount(groupInfo.getSize());
						item.setExpanded(true);
					} else {
						ContactGroupInfo groupInfo = groupInfoList.get(tree.indexOf(parentItem));
						ContactInfo info = results.get(groupInfo.getIndex(parentItem.indexOf(item)));
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
		itemDelete.setText("删除联系人...");
		menuPopup.addListener(SWT.Show, new Listener () {
			@Override
			public void handleEvent(Event event) {
				TreeItem[] treeItems = tree.getSelection();
				List<ContactInfo> infoList = new ArrayList<ContactInfo>();
				if (treeItems != null) {
					for (TreeItem treeItem : treeItems) {
						ContactInfo info = (ContactInfo)treeItem.getData();
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
				List<ContactInfo> infoList = new ArrayList<ContactInfo>();
				if (treeItems != null) {
					for (TreeItem treeItem : treeItems) {
						ContactInfo info = (ContactInfo)treeItem.getData();
						if (info != null) {
							infoList.add(info);
						}
					}
				}
				if (infoList.size() > 0) {
					MessageBox mb = new MessageBox(getShell(), SWT.OK | SWT.CANCEL | SWT.ICON_QUESTION);
					mb.setMessage("确定要删除选择的联系人？");
					mb.setText("提示");
					if (mb.open() == SWT.OK) {
						deleteContact(infoList);
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
		JobQueue.getInstance().addJob(new LoadContactTask(refreshDetail));
	}
	
	public void deleteContact(List<ContactInfo> infoList) {
		JobQueue.getInstance().addJob(new DeleteContactTask(infoList));
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
		results = ContactService.getInstance().getAll();
		groupInfoList = new ArrayList<ContactGroupInfo>();
		for (int i = 0; i < results.size(); i++) {
			ContactInfo info = results.get(i);
			String catalog = info.getCatalog();
			ContactGroupInfo lastGroupInfo = null;
			if (groupInfoList.size() > 0) {
				lastGroupInfo = groupInfoList.get(groupInfoList.size() - 1);
			}
			if (lastGroupInfo != null && 
				lastGroupInfo.getGroupName() != null &&
				lastGroupInfo.getGroupName().equals(catalog)) {
				lastGroupInfo.setIndexEnd(i);
			} else {
				lastGroupInfo = new ContactGroupInfo();
				lastGroupInfo.setGroupName(catalog);
				lastGroupInfo.setIndexStart(i);
				lastGroupInfo.setIndexEnd(i);
				groupInfoList.add(lastGroupInfo);
			}
		}
	}
	
	private void fillTableOld() {
		for (ContactInfo info : results) {
			appendRow(info);
		}
		if (parentItem != null) {
			parentItem.setExpanded(true);
			parentItem.setText(parentItem.getText() + " (共" + parentItemChildrenCount + "人)");
		}
		parentItem = null;
		parentItemChildrenCount = 0;
	}
	
	private void appendRow(ContactInfo info) {
		// System.out.println(str);
		final TreeItem item;
		String catalog = info.getCatalog();
		if (catalog == "" || catalog.length() == 0) {
			catalog = "未分组";
		}
		if (parentItem == null || 
			parentItem.getText() == null ||
			!parentItem.getText().equals(catalog)) {
			if (parentItem != null) {
				parentItem.setExpanded(true);
				parentItem.setText(parentItem.getText() + " (共" + parentItemChildrenCount + "人)");
			}
			parentItem = new TreeItem(tree, SWT.NONE);  
			parentItem.setText(catalog);
			parentItemChildrenCount = 0;
		}
		item = new TreeItem(parentItem, SWT.NONE);
		parentItemChildrenCount++;
		fillItem(item, info);
	}
	
	private void fillItem(TreeItem item, ContactInfo info) {
		item.setText(1, Integer.toString(info.getContactId()));
		item.setText(2, info.getNumber());
		item.setText(3, info.getName());
		item.setText(4, info.getSex());
		item.setText(5, info.getJob());
		item.setText(6, info.getPostTimeBegin() + " 至 " + info.getPostTimeEnd());
		item.setText(7, info.getCardId());
		item.setText(8, info.getPhoto());
		item.setText(9, info.getPhone());
		item.setText(10, info.getFax());
		item.setText(11, info.getEmail());
		item.setData(info);
		item.setItemCount(0);
	}
	
	private class LoadContactTask implements Runnable {
		private boolean refreshDetail = false;
		
		public LoadContactTask(boolean refreshDetail) {
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
							tree.setItemCount(groupInfoList.size());
							tree.clearAll(true);
							doRefreshDetail();
						}
					}
				});
			} else {
				results = ContactService.getInstance().getAll();
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
							ContactInfo info = (ContactInfo)treeItems[0].getData();
							if (info != null) {
								AppData.getInstance().getMainWin().showDetail(info);
							}
						}
					}
				});
			}
		}
	}
	
	private class DeleteContactTask implements Runnable {
		private List<ContactInfo> infoList;
		
		public DeleteContactTask(List<ContactInfo> infoList) {
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
						AppData.getInstance().getMainWin().refreshContactData(true);
					}
				}
			});
		}
		
		private String delete() {
			try {
				ContactService.getInstance().delete(infoList);
				return null;
			} catch (DBException e) {
				e.printStackTrace();
				return e.toString();
			}
		}
	}
}
