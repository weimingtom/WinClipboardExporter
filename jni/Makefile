# Use mingw to execute make
# $ cd /D/workspace_adt_luna/WeiboClipboardExporter/jni
# $ cd /D/android_weibo/work/WinClipboardExporter/jni
# $ make clean all

CC := gcc
LD := ld
RM := rm -f
OBJS := com_iteye_weimingtom_wce_clipboard_ClipboardJNI.o
CFLAGS := -I. -Iinclude -Iinclude/win32 -D_WIN32_WINNT=0x0501

all : clipboardjni.dll

clipboardjni.dll : ${OBJS}
	${CC} -shared ${CFLAGS} -Wl,--add-stdcall-alias -o $@ ${OBJS}
	cp $@ ..

%.o : %.c
	${CC} ${CFLAGS} -o $@ -c $<

clean :
	${RM} *.o *.dll
