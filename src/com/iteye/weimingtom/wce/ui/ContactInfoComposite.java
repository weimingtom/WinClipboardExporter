package com.iteye.weimingtom.wce.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

import com.iteye.weimingtom.wce.app.AppData;
import com.iteye.weimingtom.wce.db.DBException;
import com.iteye.weimingtom.wce.model.ContactInfo;
import com.iteye.weimingtom.wce.service.ContactService;
import com.iteye.weimingtom.wce.util.JobQueue;
import com.iteye.weimingtom.wce.util.UIUtil;

public class ContactInfoComposite extends ScrolledComposite {
	public static final int TYPE_VIEW = 0;
	public static final int TYPE_ADD = 1;
	public static final int TYPE_EDIT = 2;

	private int type;
	
	private Composite mContent;
	
	private int mId;
	private int mContactId;
	private Combo comboSession;
	private Combo comboGroup;
	private Combo comboJob;
	private DateTime datePostTimeBegin;
	private DateTime datePostTimeEnd;
	private Text textNumber;
	private Text textName;
	private Combo comboSex;
	private Text textPhoto;
	private Text textCardId;
	private Text textPhone;
	private Text textFax;
	private Text textEmail;
	
	private boolean enableAutoUpdate = true;

	public ContactInfoComposite(final Composite parent, int type) {
		super(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		this.type = type;
		this.setExpandHorizontal(true);
		this.setExpandVertical(true);
		mContent = new Composite(this, SWT.NONE);
		this.setContent(mContent);
		this.createContents(mContent);
		this.setMinSize(mContent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.addModifyListener();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	private void createContents(Composite composite) {
		composite.setLayout(new GridLayout(2, false));

		Composite compositeLeft = new Composite(composite, SWT.NONE);
		compositeLeft
				.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));

		Composite compositeRight = new Composite(composite, SWT.NONE);
		compositeRight.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false,
				false));
		compositeRight.setLayout(new GridLayout(1, false));
		Label labelPhotoImage = new Label(compositeRight, SWT.BORDER);
		labelPhotoImage.setText("照片预览");
		labelPhotoImage.setLayoutData(new GridData(110, 135));

		compositeLeft.setLayout(new GridLayout(4, false));

		Label labelSession = new Label(compositeLeft, SWT.NONE);
		labelSession.setText("*分类：");
		labelSession.setAlignment(SWT.RIGHT);
		Composite compositeSession = new Composite(compositeLeft, SWT.NONE);
		GridLayout compositeSessionLayout = new GridLayout(5, false);
		compositeSession.setLayout(compositeSessionLayout);
		comboSession = new Combo(compositeSession, SWT.DROP_DOWN | SWT.READ_ONLY);
		Link linkSession = new Link(compositeSession, SWT.NONE);
		linkSession.setText("<a>分类管理</a>");
		Label labelGroup = new Label(compositeSession, SWT.NONE);
		labelGroup.setText("*所属组别：");
		labelGroup.setAlignment(SWT.RIGHT);
		GridData labelGroupGridData = new GridData();
		labelGroupGridData.horizontalIndent = 20;
		labelGroup.setLayoutData(labelGroupGridData);
		comboGroup = new Combo(compositeSession, SWT.DROP_DOWN | SWT.READ_ONLY);
		Link linkGroup = new Link(compositeSession, SWT.NONE);
		linkGroup.setText("<a>组别管理</a>");

		Label labelJob = new Label(compositeLeft, SWT.NONE);
		labelJob.setText("*职务：");
		labelJob.setAlignment(SWT.RIGHT);
		Composite compositeDateTime = new Composite(compositeLeft, SWT.NONE);
		compositeDateTime.setLayout(new GridLayout(6, false));
		comboJob = new Combo(compositeDateTime, SWT.DROP_DOWN | SWT.READ_ONLY);
		Link linkJob = new Link(compositeDateTime, SWT.NONE);
		linkJob.setText("<a>职务管理</a>");
		Label labelTime = new Label(compositeDateTime, SWT.NONE);
		labelTime.setText("*任职时间：");
		GridData labelTimeGridData = new GridData();
		labelTimeGridData.horizontalIndent = 20;
		labelTime.setLayoutData(labelTimeGridData);
		datePostTimeBegin = new DateTime(compositeDateTime, SWT.BORDER
				| SWT.DATE);
		datePostTimeBegin.setLayoutData(new GridData(100, SWT.DEFAULT));
		Label labelTimeTo = new Label(compositeDateTime, SWT.NONE);
		labelTimeTo.setText("至");
		labelTimeTo.setLayoutData(new GridData());
		datePostTimeEnd = new DateTime(compositeDateTime, SWT.BORDER
				| SWT.DATE);
		datePostTimeEnd.setLayoutData(new GridData(100, SWT.DEFAULT));

		Label labelNumber = new Label(compositeLeft, SWT.NONE);
		labelNumber.setText("*编号：");
		labelNumber.setAlignment(SWT.RIGHT);
		Composite compositeNumber = new Composite(compositeLeft, SWT.NONE);
		compositeNumber.setLayout(new GridLayout(1, false));
		textNumber = new Text(compositeNumber, SWT.BORDER);
		textNumber.setLayoutData(new GridData(200, SWT.DEFAULT));
		
		Label labelName = new Label(compositeLeft, SWT.NONE);
		labelName.setText("*姓名：");
		labelName.setAlignment(SWT.RIGHT);
		Composite compositeName = new Composite(compositeLeft, SWT.NONE);
		compositeName.setLayout(new GridLayout(3, false));
		textName = new Text(compositeName, SWT.BORDER);
		textName.setLayoutData(new GridData(100, SWT.DEFAULT));
		Label labelSex = new Label(compositeName, SWT.NONE);
		GridData labelSexGridData = new GridData();
		labelSexGridData.horizontalIndent = 20;
		labelSex.setLayoutData(labelSexGridData);
		labelSex.setText("*性别：");
		labelSex.setAlignment(SWT.RIGHT);
		comboSex = new Combo(compositeName, SWT.DROP_DOWN | SWT.READ_ONLY);

		Label labelPhoto = new Label(compositeLeft, SWT.NONE);
		labelPhoto.setAlignment(SWT.RIGHT);
		labelPhoto.setText("*上传头像：");
		Composite compositePhoto = new Composite(compositeLeft, SWT.NONE);
		compositePhoto.setLayout(new GridLayout(2, false));
		textPhoto = new Text(compositePhoto, SWT.BORDER
				| SWT.READ_ONLY);
		textPhoto.setLayoutData(new GridData(150, SWT.DEFAULT));
		Button buttonBrowse = new Button(compositePhoto, SWT.PUSH);
		buttonBrowse.setText("浏览...");
		buttonBrowse.setLayoutData(new GridData());
		buttonBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				FileDialog dialog = new FileDialog(getParent().getShell(),
						SWT.OPEN);
				String[] filterNames = new String[] { "图像文件(*.jpg)", "所有文件(*)" };
				String[] filterExtensions = new String[] { "*.jpg", "*" };
				String filterPath = "/";
				String platform = SWT.getPlatform();
				if (platform.equals("win32") || platform.equals("wpf")) {
					filterNames = new String[] { "图像文件(*.jpg)",
							"All Files (*.*)" };
					filterExtensions = new String[] { "*.jpg", "*.*" };
					// filterPath = "c:\\";
					filterPath = ".";
				}
				dialog.setFilterNames(filterNames);
				dialog.setFilterExtensions(filterExtensions);
				dialog.setFilterPath(filterPath);
				// System.out.println ("Open " + dialog.open ());
				String filename = dialog.open();
				if (filename != null) {
					textPhoto.setText(filename);
				}
			}
		});

		Label labelCardId = new Label(compositeLeft, SWT.NONE);
		labelCardId.setText("*卡号：");
		labelCardId.setAlignment(SWT.RIGHT);
		Composite compositeCardId = new Composite(compositeLeft, SWT.NONE);
		compositeCardId.setLayout(new GridLayout(3, false));
		textCardId = new Text(compositeCardId, SWT.BORDER);
		Label labelPhone = new Label(compositeCardId, SWT.NONE);
		GridData labelPhoneGridData = new GridData();
		labelPhoneGridData.horizontalIndent = 20;
		labelPhone.setLayoutData(labelPhoneGridData);
		labelPhone.setText("手机：");
		labelPhone.setAlignment(SWT.RIGHT);
		textPhone = new Text(compositeCardId, SWT.BORDER);

		Label labelFax = new Label(compositeLeft, SWT.NONE);
		labelFax.setText("传真：");
		labelFax.setAlignment(SWT.RIGHT);
		Composite compositeFax = new Composite(compositeLeft, SWT.NONE);
		compositeFax.setLayout(new GridLayout(3, false));
		textFax = new Text(compositeFax, SWT.BORDER);
		Label labelEmail = new Label(compositeFax, SWT.NONE);
		GridData labelEmailGridData = new GridData();
		labelEmailGridData.horizontalIndent = 20;
		labelEmail.setLayoutData(labelEmailGridData);
		labelEmail.setText("电子邮箱：");
		labelEmail.setAlignment(SWT.RIGHT);
		textEmail = new Text(compositeFax, SWT.BORDER);

		final int labelW = SWT.DEFAULT; //100;
		final int textW = 240;

		GridData data;

		data = new GridData();
		data.widthHint = labelW;
		data.horizontalAlignment = SWT.END;
		labelSession.setLayoutData(data);
		data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		//data.widthHint = textW;
		data.horizontalSpan = 3;
		compositeSession.setLayoutData(data);

		data = new GridData();
		data.widthHint = labelW;
		data.horizontalAlignment = SWT.END;
		labelJob.setLayoutData(data);
		data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		//data.widthHint = textW;
		data.horizontalSpan = 3;
		compositeDateTime.setLayoutData(data);

		data = new GridData();
		data.widthHint = labelW;
		data.horizontalAlignment = SWT.END;
		labelNumber.setLayoutData(data);
		data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		//data.widthHint = textW;
		data.horizontalSpan = 3;
		compositeNumber.setLayoutData(data);

		data = new GridData();
		data.widthHint = labelW;
		data.horizontalAlignment = SWT.END;
		labelName.setLayoutData(data);
		data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		//data.widthHint = textW;
		data.horizontalSpan = 3;
		compositeName.setLayoutData(data);

		data = new GridData();
		data.horizontalAlignment = SWT.END;
		data.widthHint = labelW;
		labelPhoto.setLayoutData(data);
		data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		//data.widthHint = textW;
		data.horizontalSpan = 3;
		compositePhoto.setLayoutData(data);

		data = new GridData();
		data.widthHint = labelW;
		data.horizontalAlignment = SWT.END;
		labelCardId.setLayoutData(data);
		data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		//data.widthHint = textW;
		data.horizontalSpan = 3;
		compositeCardId.setLayoutData(data);

		data = new GridData();
		data.widthHint = labelW;
		data.horizontalAlignment = SWT.END;
		labelFax.setLayoutData(data);
		data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		//data.widthHint = textW;
		data.horizontalSpan = 3;
		compositeFax.setLayoutData(data);
	}
	
	private void init() {
		comboSession.removeAll();
		comboSession.add("分类1");
		comboSession.add("分类2");
		comboSession.add("分类3");
		comboSession.add("分类4");
		comboSession.select(0);
		
		comboGroup.removeAll();
		comboGroup.add("组别1");
		comboGroup.add("组别2");
		comboGroup.select(0);
		
		comboJob.removeAll();
		comboJob.add("职务1");
		comboJob.add("职务2");
		comboJob.add("职务3");
		comboJob.select(0);
		
		comboSex.removeAll();
		comboSex.add("");
		comboSex.add("男");
		comboSex.add("女");
	}
	
	public void refreshLayout() {
		mContent.layout(true, true);
		this.setMinSize(mContent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	public ContactInfo getContactInfo() {
		ContactInfo result = new ContactInfo();
		result.setId(this.mId);
		result.setContactId(this.mContactId);
		result.setSession(getComboString(comboSession));
		result.setCatalog(getComboString(comboGroup));
		result.setJob(getComboString(comboJob));
		result.setPostTimeBegin(getDateString(datePostTimeBegin));
		result.setPostTimeEnd(getDateString(datePostTimeEnd));
		result.setNumber(getTextString(textNumber));
		result.setName(getTextString(textName));
		result.setSex(getComboString(comboSex));
		result.setPhoto(getTextString(textPhoto));
		result.setCardId(getTextString(textCardId));
		result.setPhone(getTextString(textPhone));
		result.setFax(getTextString(textFax));
		result.setEmail(getTextString(textEmail));
		return result;
	}
	
	public void setContactInfo(ContactInfo info) {
		enableAutoUpdate = false;
		init();
		if (info != null) {
			mId = info.getId();
			mContactId = info.getContactId();
			setComboString(comboSession, info.getSession());
			setComboString(comboGroup, info.getCatalog());
			setComboString(comboJob, info.getJob());
			setDateString(datePostTimeBegin, info.getPostTimeBegin());
			setDateString(datePostTimeEnd, info.getPostTimeEnd());
			setTextString(textNumber, info.getNumber());
			setTextString(textName, info.getName());
			setComboString(comboSex, info.getSex());
			setTextString(textPhoto, info.getPhoto());
			setTextString(textCardId, info.getCardId());
			setTextString(textPhone, info.getPhone());
			setTextString(textFax, info.getFax());
			setTextString(textEmail, info.getEmail());
		}
		refreshLayout();
		enableAutoUpdate = true;
	}
	
	private void addModifyListener() {
		enableAutoUpdate = false;
		if (type == TYPE_VIEW) {
			comboSession.addModifyListener(mModifyListener);
			comboGroup.addModifyListener(mModifyListener);
			comboJob.addModifyListener(mModifyListener);
			datePostTimeBegin.addSelectionListener(mSelectionAdapter);
			datePostTimeEnd.addSelectionListener(mSelectionAdapter);
			textNumber.addModifyListener(mModifyListener);
			textName.addModifyListener(mModifyListener);
			comboSex.addModifyListener(mModifyListener);
			textPhoto.addModifyListener(mModifyListener);
			textCardId.addModifyListener(mModifyListener);
			textPhone.addModifyListener(mModifyListener);
			textFax.addModifyListener(mModifyListener);
			textEmail.addModifyListener(mModifyListener);
		}
	}
	
	private String getTextString(Text text) {
		return text.getText();
	}
	
	private void setTextString(Text text, String str) {
		text.setText("");
		text.append(str);
	}
	
	private String getComboString(Combo combo) {
		return combo.getText();
	}
	
	private void setComboString(Combo combo, String str) {
		if (str == null) {
			str = "";
		}
		int pos = combo.indexOf(str);
		if (pos >= 0) {
			combo.select(pos);
		} else {
			combo.add(str, 0);
			combo.select(0);
		}
	}
	
	private String getDateString(DateTime dateTime) {
		String strYear = Integer.toString(dateTime.getYear());
		strYear = "0000" + strYear;
		strYear = strYear.substring(strYear.length() - 4);
		String strMonth = Integer.toString(dateTime.getMonth() + 1);
		strMonth = "00" + strMonth;
		strMonth = strMonth.substring(strMonth.length() - 2);
		String strDay = Integer.toString(dateTime.getDay());
		strDay = "00" + strDay;
		strDay = strDay.substring(strDay.length() - 2);
		return strYear + "-" + strMonth + "-" + strDay;
	}
	
	private void setDateString(DateTime dateTime, String str) {
		dateTime.setDate(1900, 0, 1);
		if (str != null) {
			String[] strs = str.split("-");
			if (strs != null && strs.length >= 3) {
				try {
					int year = Integer.parseInt(strs[0]);
					int month = Integer.parseInt(strs[1]);
					int day = Integer.parseInt(strs[2]);
					if (year >= 1752 && year <= 9999 &&
						month >= 0 && month <= 11 &&
						day >= 1) {
						dateTime.setDate(year, month - 1, day);
					}
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
		}
	}
	
	public boolean isEnableAutoUpdate() {
		return enableAutoUpdate;
	}

	public void setEnableAutoUpdate(boolean enableAutoUpdate) {
		this.enableAutoUpdate = enableAutoUpdate;
	}

	private SelectionAdapter mSelectionAdapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (enableAutoUpdate) {
				autoUpdateContact();
			}
		}
	};
	
	private ModifyListener mModifyListener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			if (enableAutoUpdate) {
				autoUpdateContact();
			}
		}
	};
	
	private void autoUpdateContact() {
		JobQueue.getInstance().addJob(new AutoUpdateContactTask());
	}
	
	private class AutoUpdateContactTask implements Runnable {
		private ContactInfo info;
		
		public AutoUpdateContactTask() {
			info = getContactInfo();
		}
		
		@Override
		public void run() {
			final String error = update();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (error != null) {
						UIUtil.showMessage("更新数据错误：" + error);
					} else {
						AppData.getInstance().getMainWin().refreshContactData(false);
					}
				}
			});
		}
		
		private String update() {
			try {
				ContactService.getInstance().update(info);
				return null;
			} catch (DBException e) {
				e.printStackTrace();
				return e.toString();
			}
		}
	}
}
