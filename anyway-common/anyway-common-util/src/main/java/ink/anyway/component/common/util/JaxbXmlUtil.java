package ink.anyway.component.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * Xml工具类
 * @author 李海博
 * @version v1.0
 *
 */
public class JaxbXmlUtil {

	private static final Logger logger = LoggerFactory.getLogger(JaxbXmlUtil.class);

	public static String xmlTop = "";

	static {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=").append('"').append("1.0").append('"')
				.append(" encoding=").append('"').append("GB18030").append('"')
				.append(" standalone=").append('"').append("yes").append('"')
				.append("?>");
		xmlTop = sb.toString();
	}

	public static String objectToXml(Object object) {
		String resV = "";
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			JAXBContext context = JAXBContext.newInstance(object.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "GB18030");
			marshaller.marshal(object, os);
			resV = new String(os.toByteArray(), "GB18030");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resV;

	}

	public static Object xmlToObject(Class<?> classOb, String xml) {
		Object resV = null;
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(
					xml.getBytes("GB18030"));
			JAXBContext context = JAXBContext.newInstance(classOb);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			resV = unmarshaller.unmarshal(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resV;
	}

	public static boolean objectToXmlFile(Object object, String filePath) {
		boolean bResult = false;
		// 文件目录
		File file = new File(filePath);
		// 文件否
		if (file.isDirectory()) {
			// 目录自动创建，并文件名自动获取，文件名使用类名
			if (!file.exists()) {
				if (!file.mkdir()) {
					logger.warn("[" + JaxbXmlUtil.class.getName()
							+ "]目录创建失败!!!");
					return false;
				}
			}
			if (!filePath.endsWith("/") || !filePath.endsWith("\\")) {
				filePath = filePath + System.getProperty("file.separator");
			}
			filePath = filePath + object.getClass().getSimpleName() + ".xml";
		} else {
			int fileSeparatorIndex = filePath.lastIndexOf(System
					.getProperty("file.separator"));
			String dirStr = filePath.substring(0, fileSeparatorIndex);
			File testFile = new File(dirStr);
			if (!testFile.exists()) {
				if (!testFile.mkdir()) {
					logger.warn("[" + JaxbXmlUtil.class.getName()
							+ "]目录创建失败!!!");
					return false;
				}
			}
		}

		File xmlFile = new File(filePath);
		try {
			JAXBContext context = JAXBContext.newInstance(object.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(object, new FileOutputStream(xmlFile));
			bResult = true;
		} catch (JAXBException e) {
			logger.warn("JAXB失败!!!");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			logger.warn("文件未找到!!!");
			e.printStackTrace();
		}
		return bResult;
	}

	public static Object xmlFileToObject(Class<?> class1, String filePath) {
		Object rObject = null;
		File xmlFile = new File(filePath);
		if (xmlFile.isDirectory()) {
			if (!filePath.endsWith("/") || !filePath.endsWith("\\")) {
				filePath = filePath + System.getProperty("file.separator");
			}
			filePath = filePath + class1.getSimpleName() + ".xml";
			xmlFile = new File(filePath);
			if (!xmlFile.exists()) {
				logger.warn("文件不存在!!!");
				return null;
			}
		} else {
			if (!xmlFile.exists()) {
				logger.warn("文件不存在!!!");
				return null;
			}
		}
		try {
			JAXBContext context = JAXBContext.newInstance(class1);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			rObject = unmarshaller.unmarshal(xmlFile);
		} catch (JAXBException e) {
			logger.warn("JAXB失败!!!");
			e.printStackTrace();
		}
		return rObject;
	}
}
