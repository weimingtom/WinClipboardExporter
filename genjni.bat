@set PATH=C:\Program Files (x86)\Java\jdk1.7.0_45\bin;%PATH%
@set PATH=D:\Program Files\Java\jdk1.7.0_71\bin;%PATH%
@javah -jni -classpath ./bin -d jni com.iteye.weimingtom.wce.clipboard.ClipboardJNI
@pause
