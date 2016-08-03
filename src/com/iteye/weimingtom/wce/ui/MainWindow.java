package com.iteye.weimingtom.wce.ui;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
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
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.iteye.weimingtom.wce.app.AppConfig;
import com.iteye.weimingtom.wce.model.ContactInfo;
import com.iteye.weimingtom.wce.model.ArticleInfo;
import com.iteye.weimingtom.wce.util.ResourceUtil;
import com.iteye.weimingtom.wce.util.UIUtil;

public class MainWindow {
	private static final boolean ENABLE_MAXIMIZE = true;
	private static final String TITLE = "微博文章管理系统";
	private final static int SHELL_WIDTH = 800;
	private final static int SHELL_HEIGHT = 600;
	private final static int TIMER_INTERVAL = 200;
	
	private static final int TAB_INDEX_CONTACT = 0;
	private static final int TAB_INDEX_ARTICLE = 1;
	
	private Image icon = ResourceUtil.loadImage(Display.getCurrent(),
			this.getClass(), AppConfig.ICON_APP);
	private Image iconBack = ResourceUtil.loadImage(Display.getCurrent(),
			this.getClass(), AppConfig.ICON_BACK);
	
	private Display display;
	private Shell shell;
	private CTabFolder tabFolder;
	private CTabItem logItem;
	private LogTab logTab;
	private CTabItem contactItem;
	private ContactTab contactTab;
	private CTabItem articleItem;
	private ArticleTab articleTab;
	
	private Composite containerBodyBottom;
	private StackLayout layoutBodyBottom;
	private MessageComposite messageDetail;
	private ContactInfoComposite contactInfoDetail;
	private ArticleInfoComposite articleInfoDetail;
	
	public transient Shell dlgImme;
	public transient Button button1;
	public transient Button button2;
	public transient Button buttonShowLeftPanel;

	public final static String COMMAND = "lua";
	public final static String LIB_NAME = "debughook";
	public static String scriptFileName = null; // = "test/factorial.lua";
	private StringBuffer outputBuffer = new StringBuffer();

	private static final String[] columnNames = {
		"姓名",
		"性别",
		"职务",
		"组别",
		"邮箱",
		"手机",
	};
	
	public MainWindow() {

	}

	/**
	 * 
	 * @throws IOException
	 */
	public void open() throws IOException {
		display = Display.getDefault();
		createShell();
		createContents();
		display.timerExec(TIMER_INTERVAL, new Runnable() {
			@Override
			public void run() {
				synchronized (outputBuffer) {
					if (outputBuffer.length() > 0) {
						logTab.logOutput.setText("");
						logTab.logOutput.append(outputBuffer.toString());
						outputBuffer.setLength(0);
					}
				}
				display.timerExec(TIMER_INTERVAL, this);
			}
		});
		
		UIUtil.setShellCenter(shell, SHELL_WIDTH, SHELL_HEIGHT);
		if (ENABLE_MAXIMIZE) {
			shell.setMaximized(true);
		}
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	protected void createShell() {
		shell = new Shell(SWT.MAX | SWT.MIN | SWT.CLOSE | SWT.TITLE
				| SWT.RESIZE);
		shell.setText(TITLE);
		shell.setLayout(new FormLayout());
		shell.setImage(icon);
	}
	
	protected void createContents() {		
		Menu bar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(bar);
		createFileMenu(bar);
		createHelpMenu(bar);
		
		final Composite containerTop = new Composite(shell, SWT.NONE);
		containerTop.setLayout(new FillLayout(SWT.VERTICAL));
		final ToolBar toolBar = new ToolBar(containerTop, SWT.FLAT);
		final ToolItem toolItemContactCreate = new ToolItem(toolBar, SWT.DROP_DOWN/*SWT.PUSH*/);
		toolItemContactCreate.setText("新建");
		toolItemContactCreate.setImage(iconBack);
		final ToolItem toolItemSeparator = new ToolItem(toolBar, SWT.SEPARATOR);
		final ToolItem toolItemRefresh = new ToolItem(toolBar, SWT.PUSH);
		toolItemRefresh.setText("刷新");
		toolItemRefresh.setImage(iconBack);
		toolItemRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				switch (tabFolder.getSelectionIndex()) {
				case TAB_INDEX_CONTACT:
					refreshContactData(true);
					refreshArticleData(false);
					break;
					
				case TAB_INDEX_ARTICLE:
					refreshContactData(false);
					refreshArticleData(true);
					break;
				}
			}
		});
		
		final Menu menuContactCreate = new Menu(shell, SWT.NONE);
		for (int i = 0; i < 8; i++) {
			MenuItem item = new MenuItem(menuContactCreate, SWT.PUSH);
			item.setText("新建项" + i);
		}
		toolItemContactCreate.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				/*if (event.detail == SWT.ARROW)*/ {
					Rectangle rect = toolItemContactCreate.getBounds();
					Point pt = new Point(rect.x, rect.y + rect.height);
					pt = toolBar.toDisplay(pt);
					menuContactCreate.setLocation(pt.x, pt.y);
					menuContactCreate.setVisible(true);
				}
			}
		});
		
		final Composite containerBody = new Composite(shell, SWT.NONE);
		containerBody.setLayout(new FormLayout());
		//container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		final Composite containerBodyLeft = new Composite(containerBody, SWT.NONE);
		GridLayout gridLayoutBodyLeft = new GridLayout(1, false);
		gridLayoutBodyLeft.marginWidth = 0;
		gridLayoutBodyLeft.marginHeight = 0;
		gridLayoutBodyLeft.verticalSpacing = 0;
		gridLayoutBodyLeft.horizontalSpacing = 0;
		containerBodyLeft.setLayout(gridLayoutBodyLeft);
		Composite containerBodyLeftHeader = new Composite(containerBodyLeft, SWT.NONE);
		containerBodyLeftHeader.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		containerBodyLeftHeader.setLayout(new GridLayout(1, false));
		final Label labelBodyLeftHeader1 = new Label(containerBodyLeftHeader, SWT.NONE);
		labelBodyLeftHeader1.setText("操作");
		final Tree treeBodyLeft = new Tree(containerBodyLeft, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.H_SCROLL | SWT.V_SCROLL);
		
		TreeItem treeItem1 = new TreeItem(treeBodyLeft, SWT.NONE);
		final TreeItem subItem1_1 = new TreeItem(treeItem1, SWT.NONE);
		subItem1_1.setText("联系人名单");
		subItem1_1.setExpanded(true);
		final TreeItem subItem1_2 = new TreeItem(treeItem1, SWT.NONE);
		subItem1_2.setText("新建联系人...");
		subItem1_2.setExpanded(true);
		treeItem1.setText("通讯录");
		treeItem1.setExpanded(true);
		
		TreeItem treeItem2 = new TreeItem(treeBodyLeft, SWT.NONE);
		final TreeItem subItem2_1 = new TreeItem(treeItem2, SWT.NONE);
		subItem2_1.setText("文章列表");
		subItem2_1.setExpanded(true);
		final TreeItem subItem2_2 = new TreeItem(treeItem2, SWT.NONE);
		subItem2_2.setText("新建文章...");
		subItem2_2.setExpanded(true);
		treeItem2.setText("文章管理");
		treeItem2.setExpanded(true);
		
		treeBodyLeft.select(subItem1_1);
		treeBodyLeft.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent event) {
				Point point = new Point(event.x, event.y); 
			    TreeItem item = treeBodyLeft.getItem (point);
			    if (item == subItem1_1) {
			    	tabFolder.setSelection(TAB_INDEX_CONTACT);
			    } else if (item == subItem1_2) {
			    	addContact();
			    } else if (item == subItem2_1) {
			    	tabFolder.setSelection(TAB_INDEX_ARTICLE);
				} else if (item == subItem2_2) {
					addArticles();
				}
			}
		});
		treeBodyLeft.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Sash sashVer = new Sash(containerBody, SWT.VERTICAL);
		
		final Composite containerBodyRight = new Composite(containerBody, SWT.NONE);
		containerBodyRight.setLayout(new FormLayout());
		Composite containerBodyCenter = new Composite(containerBodyRight, SWT.NONE);
		GridLayout gridLayoutBodyRight = new GridLayout(1, false);
		gridLayoutBodyRight.marginWidth = 0;
		gridLayoutBodyRight.marginHeight = 0;
		gridLayoutBodyRight.verticalSpacing = 0;
		gridLayoutBodyRight.horizontalSpacing = 0;
		containerBodyCenter.setLayout(gridLayoutBodyRight);
		tabFolder = new CTabFolder(containerBodyCenter, SWT.TOP | SWT.BORDER/* SWT.NONE */);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH/* 1808 */));
		
		final Sash sashHor = new Sash(containerBodyRight, SWT.HORIZONTAL);
		
		containerBodyBottom = new Composite(containerBodyRight, SWT.NONE);
		layoutBodyBottom = new StackLayout();
		containerBodyBottom.setLayout(layoutBodyBottom);
		messageDetail = new MessageComposite(containerBodyBottom);
		contactInfoDetail = new ContactInfoComposite(containerBodyBottom, ContactInfoComposite.TYPE_VIEW);
		articleInfoDetail = new ArticleInfoComposite(containerBodyBottom, ArticleInfoComposite.TYPE_VIEW);
		layoutBodyBottom.topControl = messageDetail;
		
		// see http://www.blogjava.net/Javawind/archive/2008/06/06/206397.html
		// see
		// http://www.ibm.com/developerworks/cn/education/opensource/os-eclipse-rcp1/index.html#resources
		tabFolder.setTabHeight(25);
		tabFolder.setSimple(false);
		tabFolder.setBorderVisible(true);
		if (false) {
			int colorCount = 3;
			Color[] colors = new Color[colorCount];
			colors[0] = display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND);
			colors[1] = display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
			colors[2] = colors[0];
			int[] percents = new int[colorCount - 1];
			percents[0] = 4;
			percents[1] = 60;
			tabFolder.setSelectionBackground(colors, percents, true);
			tabFolder.setSelectionForeground(display.getSystemColor(SWT.COLOR_TITLE_FOREGROUND));
		} else {
			tabFolder.setSelectionForeground(display.getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
			tabFolder.setSelectionBackground(new Color[] { display.getSystemColor(SWT.COLOR_WHITE),
					display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND), }, new int[] { 75 },
					true);
		}
		//tabFolder.setSelectionForeground(display.getSystemColor(SWT.COLOR_WHITE));
		//tabFolder.setSelectionBackground(display.getSystemColor(SWT.COLOR_BLUE));
		//tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		tabFolder.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				for (int i = 0; i < tabFolder.getItemCount(); i++) {
					if (tabFolder.getSelection() == tabFolder.getItem(i)) {
						if (tabFolder.getItem(i) != null
								&& tabFolder.getItem(i).getControl() != null) {
							tabFolder.getItem(i).getControl().forceFocus();
						}
					}
				}
			}
		});
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				switch (tabFolder.getSelectionIndex()) {
				case TAB_INDEX_CONTACT:
					treeBodyLeft.select(subItem1_1);
					break;
					
				case TAB_INDEX_ARTICLE:
					treeBodyLeft.select(subItem2_1);
					break;
				}
			}
		});

		contactItem = new CTabItem(tabFolder, SWT.NONE/* SWT.CLOSE */);
		contactItem.setText("  联系人名单  ");
		contactTab = new ContactTab(tabFolder);
		contactItem.setControl(contactTab);
		
		articleItem = new CTabItem(tabFolder, SWT.NONE/* SWT.CLOSE */);
		articleItem.setText("  文章列表  ");
		articleTab = new ArticleTab(tabFolder);
		articleItem.setControl(articleTab);
		
		logItem = new CTabItem(tabFolder, SWT.NONE/* SWT.CLOSE */);
		logTab = new LogTab(tabFolder);
		logItem.setControl(logTab);
		logItem.setText("  系统日志  ");
		
		tabFolder.setSelection(0);
		
		Composite containerFoot = new Composite(shell, SWT.NONE);
		containerFoot.setLayout(new FormLayout());
		
		/*
		 * settingItem = new CTabItem(tabFolder, 0);
		 * settingItem.setText("Settings");
		 */
		button1 = new Button(containerFoot, SWT.PUSH);
		button1.setText("日志(&S)");
		button1.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event event) {
				logTab.logOutput.setText("");
			}
		});
		button2 = new Button(containerFoot, SWT.PUSH);
		button2.setText("新建(&D)");
		button2.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event event) {
				
			}
		});
		buttonShowLeftPanel = new Button(containerFoot, SWT.PUSH);
		buttonShowLeftPanel.setText("<<隐藏操作");
		// Layout
		FormData data;
		
		final FormData dataShowLeft, dataHideLeft;
		dataShowLeft = new FormData();
		dataShowLeft.left = new FormAttachment(0, 0);
		dataShowLeft.right = new FormAttachment(sashVer, 0);
		dataShowLeft.top = new FormAttachment(0, 0);
		dataShowLeft.bottom = new FormAttachment(100, 0);
		dataHideLeft = new FormData();
		dataHideLeft.left = new FormAttachment(0);
		dataHideLeft.right = new FormAttachment(0);
		dataHideLeft.top = new FormAttachment(0);
		dataHideLeft.bottom = new FormAttachment(0);
		containerBodyLeft.setLayoutData(dataShowLeft);		
		
		final int sashVerLimit = 50, sashVerPercent = 20, sashVerWidth = 5;
		final FormData sashDataShow = new FormData();
		sashDataShow.left = new FormAttachment(sashVerPercent, 0);
		sashDataShow.top = new FormAttachment(0, 0);
		sashDataShow.bottom = new FormAttachment(100, 0);
		sashDataShow.width = sashVerWidth;
		final FormData sashDataHide = new FormData();
		sashDataHide.left = new FormAttachment(0, 0);
		sashDataHide.top = new FormAttachment(0, 0);
		sashDataHide.bottom = new FormAttachment(100, 0);
		sashDataHide.width = 0;
		sashVer.setLayoutData(sashDataShow);
		sashVer.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				Rectangle sashRect = sashVer.getBounds();
				Rectangle shellRect = containerBody.getClientArea();
				int right = shellRect.width - sashRect.width - sashVerLimit;
				e.x = Math.max(Math.min(e.x, right), sashVerLimit);
				if (e.x != sashRect.x) {
					sashDataShow.left = new FormAttachment(0, e.x);
					containerBody.layout();
				}
			}
		});
		
		data = new FormData();
		data.left = new FormAttachment(sashVer, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);
		containerBodyRight.setLayoutData(data);
		
		buttonShowLeftPanel.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event event) {
				if (containerBodyLeft.getLayoutData() == dataShowLeft) {
					containerBodyLeft.setLayoutData(dataHideLeft);
					sashVer.setLayoutData(sashDataHide);
					buttonShowLeftPanel.setText(">>显示操作");
				} else {
					containerBodyLeft.setLayoutData(dataShowLeft);
					sashVer.setLayoutData(sashDataShow);
					buttonShowLeftPanel.setText("<<隐藏操作");
				}
				containerBody.layout();
			}
		});
		
		//---
		
		final int sashHorLimit = 50, sashHorPercent = 60/*80*/, sashHorHeight = 5;
		final FormData dataShowBottom, dataHideBottom;
		dataShowBottom = new FormData();
		dataShowBottom.top = new FormAttachment(0, 0);
		dataShowBottom.bottom = new FormAttachment(sashHor, 0);
		dataShowBottom.left = new FormAttachment(0, 0);
		dataShowBottom.right = new FormAttachment(100, 0);
		dataHideBottom = new FormData();
		dataHideBottom.left = new FormAttachment(0);
		dataHideBottom.right = new FormAttachment(0);
		dataHideBottom.top = new FormAttachment(0);
		dataHideBottom.bottom = new FormAttachment(0);
		containerBodyCenter.setLayoutData(dataShowBottom);		
		
		final FormData sashHorDataShow = new FormData();
		sashHorDataShow.top = new FormAttachment(sashHorPercent, 0);
		sashHorDataShow.left = new FormAttachment(0, 0);
		sashHorDataShow.right = new FormAttachment(100, 0);
		sashHorDataShow.height = sashHorHeight;
		final FormData sashHorDataHide = new FormData();
		sashHorDataHide.top = new FormAttachment(0, 0);
		sashHorDataHide.left = new FormAttachment(0, 0);
		sashHorDataHide.right = new FormAttachment(100, 0);
		sashHorDataHide.height = 0;
		sashHor.setLayoutData(sashHorDataShow);
		sashHor.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				Rectangle sashRect = sashHor.getBounds();
				Rectangle shellRect = containerBodyRight.getClientArea();
				int bottom = shellRect.height - sashRect.height - sashHorLimit;
				e.y = Math.max(Math.min(e.y, bottom), sashHorLimit);
				if (e.y != sashRect.y) {
					sashHorDataShow.top = new FormAttachment(0, e.y);
					containerBodyRight.layout();
				}
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(sashHor, 0);
		data.bottom = new FormAttachment(100, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		containerBodyBottom.setLayoutData(data);
		
		
		
		
		
		
		//---
		
		
		data = new FormData();
		data.left = new FormAttachment(0, 5);
		data.top = new FormAttachment(0, 1);
		data.bottom = new FormAttachment(100, -1);
		data.width = 150;
		buttonShowLeftPanel.setLayoutData(data);	
		
		data = new FormData();
		data.top = new FormAttachment(0, 1);
		data.bottom = new FormAttachment(100, -1);
		data.left = new FormAttachment(buttonShowLeftPanel, 5);
		data.width = 150;
		button2.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(0, 1);
		data.bottom = new FormAttachment(100, -1);
		data.left = new FormAttachment(button2, 5);
		data.width = 150;
		button1.setLayoutData(data);
		
		
		
		data = new FormData();
		data.top = new FormAttachment(0, 5);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(100, -5);
		containerTop.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(containerTop, 5);
		data.left = new FormAttachment(0, 5);
		data.bottom = new FormAttachment(containerFoot, 0);
		data.right = new FormAttachment(100, -5);
		containerBody.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(0, 1);
		data.bottom = new FormAttachment(100, -3);
		data.right = new FormAttachment(100, -1);
		containerFoot.setLayoutData(data);
		
		treeBodyLeft.forceFocus();
	}
	
	private void createFileMenu(Menu parent) {
		Menu menu = new Menu(parent);
		MenuItem header = new MenuItem(parent, SWT.CASCADE);
		header.setText("文件(&F)");
		header.setMenu(menu);

		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("退出(&X)\tAlt+F4");
		item.setAccelerator(SWT.ALT | SWT.F4);
		item.addSelectionListener(new SelectionAdapter () {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
	}
	
	private void createHelpMenu(Menu parent) {
		Menu menu = new Menu(parent);
		MenuItem header = new MenuItem(parent, SWT.CASCADE);
		header.setText("帮助(&H)");
		header.setMenu(menu);

		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("关于(&A)...");		
		item.addSelectionListener(new SelectionAdapter () {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
				box.setText("关于");
				box.setMessage("当前操作系统：" + System.getProperty("os.name"));
				box.open();
			}
		});
	}
	
	public void refreshContactData(boolean refreshDetail) {
		contactTab.reloadData(refreshDetail);
	}
	
	public void refreshArticleData(boolean refreshDetail) {
		articleTab.reloadData(refreshDetail);
	}
	
	public void showDetail(Object data) {
		if (data == null) {
			layoutBodyBottom.topControl = this.messageDetail;
			containerBodyBottom.layout();
		} else if (data instanceof ContactInfo) {
			contactInfoDetail.setContactInfo((ContactInfo) data);
			layoutBodyBottom.topControl = this.contactInfoDetail;
			containerBodyBottom.layout();
			this.contactInfoDetail.setEnableAutoUpdate(true);
		} else if (data instanceof ArticleInfo) {
			articleInfoDetail.setArticleInfo((ArticleInfo) data);
			layoutBodyBottom.topControl = this.articleInfoDetail;
			containerBodyBottom.layout();
			this.articleInfoDetail.setEnableAutoUpdate(true);
		}
	}
	
	public void addContact() {
		ContactInfoDialog dialog = new ContactInfoDialog(shell, 
				ContactInfoComposite.TYPE_ADD);
		if (dialog.open() == ContactInfoDialog.OK) {
			refreshContactData(false);
			tabFolder.setSelection(TAB_INDEX_CONTACT);
		}
	}
	
	public void addArticles() {
		ArticleInfoDialog dialog = new ArticleInfoDialog(shell, 
				ArticleInfoComposite.TYPE_ADD);
		if (dialog.open() == ArticleInfoDialog.OK) {
			refreshArticleData(false);
			tabFolder.setSelection(TAB_INDEX_ARTICLE);
		}
	}
}
