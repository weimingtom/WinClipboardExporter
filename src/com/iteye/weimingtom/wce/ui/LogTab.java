package com.iteye.weimingtom.wce.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class LogTab extends Composite {
	public transient Shell shell;
	public transient Text logOutput;
	public transient Color textBGColor = new Color(Display.getCurrent(),
			new RGB(0x53, 0xa5, 0xff));

	public LogTab(final Composite composite) {
		super(composite, 0);
		setLayout(new GridLayout(1, false));
		shell = composite.getShell();

		logOutput = new Text(this, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.READ_ONLY);
		logOutput.setLayoutData(new GridData(GridData.FILL_BOTH));
		logOutput.setBackground(textBGColor);
	}
}
