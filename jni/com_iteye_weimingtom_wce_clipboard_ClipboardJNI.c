#include <stdio.h>
#include <windows.h>
#include <string.h>
#include "com_iteye_weimingtom_wce_clipboard_ClipboardJNI.h"

//see also: https://msdn.microsoft.com/en-us/library/ms680582(v=vs.85).aspx
void ErrorExit(LPTSTR lpszFunction) 
{ 
    // Retrieve the system error message for the last-error code

    LPVOID lpMsgBuf;
    LPVOID lpDisplayBuf;
    DWORD dw = GetLastError(); 

    FormatMessage(
        FORMAT_MESSAGE_ALLOCATE_BUFFER | 
        FORMAT_MESSAGE_FROM_SYSTEM |
        FORMAT_MESSAGE_IGNORE_INSERTS,
        NULL,
        dw,
        MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
        (LPTSTR) &lpMsgBuf,
        0, NULL );

    // Display the error message and exit the process

    lpDisplayBuf = (LPVOID)LocalAlloc(LMEM_ZEROINIT, 
        (lstrlen((LPCTSTR)lpMsgBuf) + lstrlen((LPCTSTR)lpszFunction) + 40) * sizeof(TCHAR)); 
    snprintf((LPTSTR)lpDisplayBuf, 
        LocalSize(lpDisplayBuf) / sizeof(TCHAR),
        TEXT("%s failed with error %d: %s"), 
        lpszFunction, dw, lpMsgBuf); 
    MessageBox(NULL, (LPCTSTR)lpDisplayBuf, TEXT("Error"), MB_OK); 

    LocalFree(lpMsgBuf);
    LocalFree(lpDisplayBuf);
    //ExitProcess(dw); 
}


JNIEXPORT jint JNICALL Java_com_iteye_weimingtom_wce_clipboard_ClipboardJNI_setClipboardViewer
  (JNIEnv *env, jclass cls, jint hWndNewViewer)
{
	HWND ret = 0; 
	ret = SetClipboardViewer((HANDLE)hWndNewViewer);
#if 0
	if (ret == NULL) 
	{
		ErrorExit("SetClipboardViewer");
	}
#endif
	return (jint)ret; 
}


