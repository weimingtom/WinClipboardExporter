package com.iteye.weimingtom.wce.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.iteye.weimingtom.wce.app.AppData;
import com.iteye.weimingtom.wce.db.DBException;
import com.iteye.weimingtom.wce.model.ArticleInfo;
import com.iteye.weimingtom.wce.service.ArticleService;
import com.iteye.weimingtom.wce.util.JobQueue;
import com.iteye.weimingtom.wce.util.UIUtil;

public class ArticleInfoComposite extends ScrolledComposite {
	public static final int TYPE_VIEW = 0;
	public static final int TYPE_ADD = 1;
	public static final int TYPE_EDIT = 2;

	private int type;
	
	private Composite mContent;
	
	private int mId;
	private int mArticleId;
	private Text textName;
	private Text textLocation;
	private Text textDescription;
	private DateTime dateCreate;
	private DateTime timeCreate;
	private DateTime dateModify;
	private DateTime timeModify;
	
	private boolean enableAutoUpdate = true;
	
	public ArticleInfoComposite(final Composite parent, int type) {
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
		composite.setLayout(new GridLayout(1, false));

		Composite compositeLeft = new Composite(composite, SWT.NONE);
		compositeLeft.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));

		compositeLeft.setLayout(new GridLayout(4, false));
		
		Label labelName = new Label(compositeLeft, SWT.NONE);
		labelName.setText("*文章标题：");
		labelName.setAlignment(SWT.RIGHT);
		Composite compositeName = new Composite(compositeLeft, SWT.NONE);
		compositeName.setLayout(new GridLayout(1, false));
		textName = new Text(compositeName, SWT.BORDER);
		textName.setLayoutData(new GridData(200, SWT.DEFAULT));
		
		Label labelLocation = new Label(compositeLeft, SWT.NONE);
		labelLocation.setText("*文章地点：");
		labelLocation.setAlignment(SWT.RIGHT);
		Composite compositeLocation = new Composite(compositeLeft, SWT.NONE);
		compositeLocation.setLayout(new GridLayout(1, false));
		textLocation = new Text(compositeLocation, SWT.BORDER);
		textLocation.setLayoutData(new GridData(200, SWT.DEFAULT));

		Label labelDescription = new Label(compositeLeft, SWT.NONE);
		labelDescription.setText("文章内容：");
		labelDescription.setAlignment(SWT.RIGHT);
		Composite compositeDescription = new Composite(compositeLeft, SWT.NONE);
		compositeDescription.setLayout(new GridLayout(1, false));
		textDescription = new Text(compositeDescription, SWT.BORDER | SWT.MULTI);
		textDescription.setLayoutData(new GridData(400, 100));
		
		Label labelCreateTime = new Label(compositeLeft, SWT.NONE);
		labelCreateTime.setAlignment(SWT.RIGHT);
		labelCreateTime.setText("创建时间：");
		Composite compositeCreateTime = new Composite(compositeLeft, SWT.NONE);
		compositeCreateTime.setLayout(new GridLayout(3, false));
		GridData labelTimeGridData = new GridData();
		labelTimeGridData.horizontalIndent = 20;
		labelCreateTime.setLayoutData(labelTimeGridData);
		dateCreate = new DateTime(compositeCreateTime, SWT.BORDER
				| SWT.DATE);
		dateCreate.setLayoutData(new GridData(100, SWT.DEFAULT));
		Label labelCreateTimeTo = new Label(compositeCreateTime, SWT.NONE);
		labelCreateTimeTo.setText(" ");
		labelCreateTimeTo.setLayoutData(new GridData());
		timeCreate = new DateTime(compositeCreateTime, SWT.BORDER
				| SWT.TIME);
		timeCreate.setLayoutData(new GridData(100, SWT.DEFAULT));

		Label labelModifyTime = new Label(compositeLeft, SWT.NONE);
		labelModifyTime.setAlignment(SWT.RIGHT);
		labelModifyTime.setText("修改时间：");
		Composite compositeModifyTime = new Composite(compositeLeft, SWT.NONE);
		compositeModifyTime.setLayout(new GridLayout(3, false));
		GridData labelModifyTimeGridData = new GridData();
		labelModifyTimeGridData.horizontalIndent = 20;
		labelModifyTime.setLayoutData(labelModifyTimeGridData);
		dateModify = new DateTime(compositeModifyTime, SWT.BORDER
				| SWT.DATE);
		dateModify.setLayoutData(new GridData(100, SWT.DEFAULT));
		Label labelModifyTimeTo = new Label(compositeModifyTime, SWT.NONE);
		labelModifyTimeTo.setText(" ");
		labelModifyTimeTo.setLayoutData(new GridData());
		timeModify = new DateTime(compositeModifyTime, SWT.BORDER
				| SWT.TIME);
		timeModify.setLayoutData(new GridData(100, SWT.DEFAULT));
		
		final int labelW = SWT.DEFAULT; //100;
		final int textW = 240;

		GridData data;

		data = new GridData();
		data.widthHint = labelW;
		data.horizontalAlignment = SWT.END;
		labelLocation.setLayoutData(data);
		data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		//data.widthHint = textW;
		data.horizontalSpan = 3;
		compositeLocation.setLayoutData(data);

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
		data.widthHint = labelW;
		data.horizontalAlignment = SWT.END;
		labelDescription.setLayoutData(data);
		data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		//data.widthHint = textW;
		data.horizontalSpan = 3;
		compositeDescription.setLayoutData(data);
		
		data = new GridData();
		data.widthHint = labelW;
		data.horizontalAlignment = SWT.END;
		labelCreateTime.setLayoutData(data);
		data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		//data.widthHint = textW;
		data.horizontalSpan = 3;
		compositeCreateTime.setLayoutData(data);

		data = new GridData();
		data.widthHint = labelW;
		data.horizontalAlignment = SWT.END;
		labelModifyTime.setLayoutData(data);
		data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		//data.widthHint = textW;
		data.horizontalSpan = 3;
		compositeModifyTime.setLayoutData(data);
	}
	
	public void init() {
		
	}
	
	public void refreshLayout() {
		mContent.layout(true, true);
		this.setMinSize(mContent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	public ArticleInfo getArticleInfo() {
		ArticleInfo result = new ArticleInfo();
		result.setId(this.mId);
		result.setArticleId(this.mArticleId);
		result.setName(getTextString(textName));
		result.setLocation(getTextString(textLocation));
		result.setDescription(getTextString(textDescription));
		result.setCreateTime(getDateTimeString(dateCreate, timeCreate));
		result.setModifyTime(getDateTimeString(dateModify, timeModify));

		return result;
	}
	
	public void setArticleInfo(ArticleInfo info) {
		enableAutoUpdate = false;
		init();
		if (info != null) {
			mId = info.getId();
			mArticleId = info.getArticleId();
			setTextString(textName, info.getName());
			setTextString(textLocation, info.getLocation());
			setTextString(textDescription, info.getDescription());
			setDateTimeString(dateCreate, timeCreate, info.getCreateTime());
			setDateTimeString(dateModify, timeModify, info.getModifyTime());
		}
		refreshLayout();
		enableAutoUpdate = true;
	}
	
	private void addModifyListener() {
		enableAutoUpdate = false;
		if (type == TYPE_VIEW) {
			textName.addModifyListener(mModifyListener);
			textLocation.addModifyListener(mModifyListener);
			textDescription.addModifyListener(mModifyListener);
			dateCreate.addSelectionListener(mSelectionAdapter);
			timeCreate.addSelectionListener(mSelectionAdapter);
			dateModify.addSelectionListener(mSelectionAdapter);
			timeModify.addSelectionListener(mSelectionAdapter);
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
	
	private String getDateTimeString(DateTime date, DateTime time) {
		String strYear = Integer.toString(date.getYear());
		strYear = "0000" + strYear;
		strYear = strYear.substring(strYear.length() - 4);
		String strMonth = Integer.toString(date.getMonth() + 1);
		strMonth = "00" + strMonth;
		strMonth = strMonth.substring(strMonth.length() - 2);
		String strDay = Integer.toString(date.getDay());
		strDay = "00" + strDay;
		strDay = strDay.substring(strDay.length() - 2);
		
		String strHour = Integer.toString(time.getHours());
		strHour = "00" + strHour;
		strHour = strHour.substring(strHour.length() - 2);
		String strMinute = Integer.toString(time.getMinutes());
		strMinute = "00" + strMinute;
		strMinute = strMinute.substring(strMinute.length() - 2);
		String strSecond = Integer.toString(time.getSeconds());
		strSecond = "00" + strSecond;
		strSecond = strSecond.substring(strSecond.length() - 2);		
		
		return strYear + "-" + strMonth + "-" + strDay + " " + 
			strHour + ":" + strMinute + ":" + strSecond;
	}
	
	private void setDateTimeString(DateTime date, DateTime time, String str) {
		date.setDate(1900, 0, 1);
		time.setTime(0, 0, 0);
		if (str != null) {
			String[] strDateTime = str.split(" ");
			try {
				if (strDateTime != null && strDateTime.length >= 2 &&
					strDateTime[0] != null && strDateTime[1] != null) {
					String[] strs = strDateTime[0].split("-");
					if (strs != null && strs.length >= 3) {
						int year = Integer.parseInt(strs[0]);
						int month = Integer.parseInt(strs[1]);
						int day = Integer.parseInt(strs[2]);
						if (year >= 1752 && year <= 9999 &&
							month >= 0 && month <= 11 &&
							day >= 1) {
							date.setDate(year, month - 1, day);
						}
					}
					strs = strDateTime[1].split(":");
					if (strs != null && strs.length >= 3) {
						int hour = Integer.parseInt(strs[0]);
						int minute = Integer.parseInt(strs[1]);
						int second = Integer.parseInt(strs[2]);
						if (hour >= 0 && hour <= 23 &&
							minute >= 0 && minute <= 59 &&
							second >= 0 && second <= 59 ) {
							time.setTime(hour, minute, second);
						}
					}
				}
			} catch (Exception e) {
				//e.printStackTrace();
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
		JobQueue.getInstance().addJob(new AutoUpdateArticleTask());
	}
	
	private class AutoUpdateArticleTask implements Runnable {
		private ArticleInfo info;
		
		public AutoUpdateArticleTask() {
			info = getArticleInfo();
		}
		
		@Override
		public void run() {
			final String error = update();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (error != null) {
						UIUtil.showMessage("更新数据错误：" + error);
					} else {
						AppData.getInstance().getMainWin().refreshArticleData(false);
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
