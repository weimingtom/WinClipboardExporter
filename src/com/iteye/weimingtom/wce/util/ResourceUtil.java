package com.iteye.weimingtom.wce.util;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;

public class ResourceUtil {
	public static Image loadImage(Device device, Class<?> clazz, String str) {
		InputStream stream = clazz.getClassLoader().getResourceAsStream(str);
		if (stream == null) {
			return null;
		}
		Image image = null;
		try {
			image = new Image(device, stream);
		} catch (SWTException ex) {
			ex.printStackTrace();
		} finally {
			try {
				stream.close();
			} catch (IOException ex) {
			}
		}
		return image;
	}
}
