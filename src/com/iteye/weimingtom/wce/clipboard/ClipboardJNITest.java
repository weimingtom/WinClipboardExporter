package com.iteye.weimingtom.wce.clipboard;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.internal.win32.MSG;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.internal.win32.TCHAR;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class ClipboardJNITest {
	private final static int SHELL_WIDTH = 400;
	private final static int SHELL_HEIGHT = 300;
	
	private Display display;
	private Shell mShell;
	private Button button1, button2, button3, button4;
	private Label label1, label2;
	private Clipboard cb;
	private int filterHook;
	private AtomicLong lastTime = new AtomicLong();

	private int hwndNextViewer;

	public ClipboardJNITest() {
		
	}
	
	public void open() {
		display = Display.getDefault();
		cb = new Clipboard(display);
		createShell();
		createContents();
		
		setShellCenter(mShell, SHELL_WIDTH, SHELL_HEIGHT);
		mShell.open();
		mShell.layout();
		mShell.pack();
		while (!mShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	protected void createShell() {
		mShell = new Shell(SWT.MAX | SWT.MIN | SWT.CLOSE | SWT.TITLE
				| SWT.RESIZE);
		mShell.setText("Form1");
		FormLayout shellLayout = new FormLayout();
		mShell.setLayout(shellLayout);
		
		hwndNextViewer = ClipboardJNI.setClipboardViewer(mShell.handle);
		System.out.println("hwndNextViewer : " + hwndNextViewer);
		
		int threadId = OS.GetCurrentThreadId ();  
		Callback msgFilterCallback = new Callback(this, "msgFilterProc", 3);  
		int msgFilterProc = msgFilterCallback.getAddress();  
		//filterHook = OS.SetWindowsHookEx(OS.WH_GETMESSAGE, msgFilterProc, 0, threadId);  
		filterHook = OS.SetWindowsHookEx(ClipboardJNI.WH_CALLWNDPROC, msgFilterProc, 0, threadId);  
		
		System.out.println("handle : " + mShell.handle);
		
	}
	
	protected void createContents() {
		button1 = new Button(mShell, SWT.PUSH);
		button1.setText("读卡");
		button2 = new Button(mShell, SWT.PUSH);
		button2.setText("仅读一次，重新取放卡才能读到第二次");
		button3 = new Button(mShell, SWT.PUSH);
		button3.setText("让设备发出声响");
		button4 = new Button(mShell, SWT.PUSH);
		button4.setText("读设备序列号（每台机器都有不同）");
		label1 = new Label(mShell, SWT.NONE);
		label1.setText("建议将OUR_IDR.dll和IDUSB.DLL复制到应用程序同一目录");
		label1.setForeground(display.getSystemColor(SWT.COLOR_DARK_BLUE));
		label2 = new Label(mShell, SWT.NONE);
		label2.setText("ID卡读卡器例子程序");
		label2.setForeground(display.getSystemColor(SWT.COLOR_RED));
		
		
		button1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onButton1Click();
			}
		});
		button2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onButton2Click();
			}
		});
		button3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onButton3Click();
			}
		});
		button4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onButton4Click();
			}
		});

		FormData formData;
		
		formData = new FormData();
		formData.top = new FormAttachment(0, 10);
		formData.left = new FormAttachment(0, 10);
		button1.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(0, 10);
		formData.left = new FormAttachment(button1, 10);
		button2.setLayoutData(formData);
		
		formData = new FormData();
		formData.top = new FormAttachment(button1, 10);
		formData.left = new FormAttachment(0, 10);
		button3.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(button2, 10);
		formData.left = new FormAttachment(button3, 10);
		button4.setLayoutData(formData);
		
		formData = new FormData();
		formData.top = new FormAttachment(button3, 10);
		formData.left = new FormAttachment(0, 10);
		formData.right = new FormAttachment(100, -10);
		label1.setLayoutData(formData);
		
		formData = new FormData();
		formData.top = new FormAttachment(label1, 10);
		formData.bottom = new FormAttachment(100, -10);
		formData.left = new FormAttachment(0, 10);
		formData.right = new FormAttachment(100, -10);
		label2.setLayoutData(formData);
	}
	
	public static final void setShellCenter(final Shell shell, final int posX,
			final int posY) {
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
	
	private void onButton1Click() {
		
	}
	
	private void onButton2Click() {

	}
	
	private void onButton3Click() {
		
	}
	
	private void onButton4Click() {
		
	}
	
	private void showMessage(String mess) {
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
	
	int msgFilterProc (int code, int wParam, int lParam) {
		MSG msg0 = new MSG();
		OS.MoveMemory(msg0, lParam, MSG.sizeof);
		
		
		/*
typedef struct tagMSG {
    HWND        hwnd;
    UINT        message;
    WPARAM      wParam;
    LPARAM      lParam;
    DWORD       time;
    POINT       pt;
#ifdef _MAC
    DWORD       lPrivate;
#endif
} MSG, *PMSG, NEAR *NPMSG, FAR *LPMSG;
     
typedef struct tagCWPSTRUCT {
	  LPARAM lParam; //long
	  WPARAM wParam; //unsigned int
	  UINT   message; //
	  HWND   hwnd;
	} CWPSTRUCT, *PCWPSTRUCT, *LPCWPSTRUCT;
		*/
		
		//reversed field order??? (MSG and CWPSTRUCT)
		//see https://msdn.microsoft.com/en-us/library/ms644975(v=vs.85).aspx
		//see https://msdn.microsoft.com/en-us/library/ms644990(v=vs.85).aspx
		MSG msg = new MSG();
		msg.hwnd = msg0.lParam;
		msg.message = msg0.wParam;
		msg.wParam = msg0.message;
		msg.lParam = msg0.hwnd;
		
		if (false) {
			System.out.println(">>>>>code = " + code + ", msg.message = " + msg.message + 
				", wParam = " + Integer.toString(msg.wParam, 16) + 
				", lParam = " + Integer.toString(msg.lParam, 16));
		}
		
		//
		switch (msg.message) {
		case ClipboardJNI.WM_CHANGECBCHAIN:
			if (wParam == hwndNextViewer) {
				hwndNextViewer = lParam;
			} else if(hwndNextViewer != 0) {
				OS.SendMessage(hwndNextViewer, msg.message, wParam, lParam);
			}
			break;
			
		case ClipboardJNI.WM_DRAWCLIPBOARD:
			long curTime = System.currentTimeMillis();
			long delta = curTime - this.lastTime.getAndSet(curTime);
			//System.out.println("======" + delta + "," + curTime + "," + lastTime);
			if (delta > 500) {
				TextTransfer transfer = TextTransfer.getInstance();
				String data = (String)cb.getContents(transfer);
				if (data != null && data.length() > 0) {
					System.out.println(">>>>>>" + data);
				}
			}
			break;
			
		default:
			if (mShell != null && !mShell.isDisposed()) {
				//System.out.println("Got a Message : " + msg.message + "," + msg.wParam + "," + msg.lParam); 
			}
			break;
		}
	    return OS.CallNextHookEx(filterHook, code, wParam, lParam);  
	}
	
	private String getClipboardText() {
		String string = "";
		if (OS.OpenClipboard (0)) {
			int /*long*/ hMem = OS.GetClipboardData (OS.IsUnicode ? OS.CF_UNICODETEXT : OS.CF_TEXT);
			if (hMem != 0) {
				/* Ensure byteCount is a multiple of 2 bytes on UNICODE platforms */
				int byteCount = OS.GlobalSize (hMem) / TCHAR.sizeof * TCHAR.sizeof;
				int /*long*/ ptr = OS.GlobalLock (hMem);
				if (ptr != 0) {
					/* Use the character encoding for the default locale */
					TCHAR buffer = new TCHAR (0, byteCount / TCHAR.sizeof);
					OS.MoveMemory (buffer, ptr, byteCount);
					string = buffer.toString (0, buffer.strlen ());
					OS.GlobalUnlock (hMem);
				}
			}
			OS.CloseClipboard();
		}
		return string;
	}
	
	public static void main(String[] args) {
		ClipboardJNITest test = new ClipboardJNITest();
		test.open();
	}
}

