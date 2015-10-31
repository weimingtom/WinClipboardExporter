package com.iteye.weimingtom.wce.clipboard;

public class ClipboardJNI {
/*
#define WM_CHANGECBCHAIN                0x030D
#define WM_DRAWCLIPBOARD                0x0308
 */
	
	public final static int WH_CALLWNDPROC = 4;
	
	public final static int WM_CHANGECBCHAIN = 0x030D;
	public final static int WM_DRAWCLIPBOARD = 0x0308;
	
	/*
WINUSERAPI
HWND
WINAPI
SetClipboardViewer(
    _In_ HWND hWndNewViewer);
	 */
	public native static int setClipboardViewer(int hWndNewViewer);

	static {
		System.loadLibrary("clipboardjni");
	}
}
