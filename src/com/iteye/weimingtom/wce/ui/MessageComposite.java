package com.iteye.weimingtom.wce.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class MessageComposite extends ScrolledComposite {
	private Composite mContent;
	
	public MessageComposite(final Composite parent) {
		super(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		this.setExpandHorizontal(true);
		this.setExpandVertical(true);
		mContent = new Composite(this, SWT.NONE);
		this.setContent(mContent);
		this.createContents(mContent);
		this.setMinSize(mContent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	private void createContents(Composite composite) {
		composite.setLayout(new GridLayout(2, false));
		
		Label labelMessage = new Label(composite, SWT.NONE);
		labelMessage.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
		labelMessage.setText("没有可编辑的属性");
	}
}
