package ink.anyway.component.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: StringUtil
 * @Description: TODO
 * @author 李海博 (haibo_li@neusoft.com)
 * @date 2016-11-23 上午10:16:53
 * @version V1.0
 */
public class StringUtil {

	private static final Logger logger = LoggerFactory.getLogger(StringUtil.class);

	public final static String MD5(String s) {
		try {
			byte[] btInput = s.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = FileUtil.hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = FileUtil.hexDigits[byte0 & 0xf];
			}
			return new String(str).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public final static String UUID(){
		return  java.util.UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}

	public final static boolean isValid(Object input){
		return input!=null&&!"".equals(input.toString().trim());
	}

	public final static String toString(Object input){
		if(input == null)
			return "";
		return input.toString();
	}

	public final static List<String> splitToList(String input, String regex){
		List<String> res = new ArrayList<>();
		if(isValid(input)){
			String[] arr = input.split(regex);
			if(arr!=null){
				for(String mo:arr){
					if(isValid(mo)){
						res.add(mo.trim());
					}
				}
			}
		}
		return res;
	}

	public final static String compose(String... unions){
		if(unions!=null&&unions.length>0){
			StringBuilder builder = new StringBuilder();
			for(String union:unions) {
				if (isValid(union))
					builder.append(union.trim());
			}
			return builder.toString();
		}
		return "";
	}

	public final static String composeWithRegex(String[] unions, String regex){
		if(unions!=null&&unions.length>0){
			StringBuilder builder = null;
			for(String union:unions) {
				if (isValid(union)){
					if(builder==null){
						builder = new StringBuilder();
					}else{
						builder.append(regex);
					}
					builder.append(union.trim());
				}
			}
			if(builder!=null){
				return builder.toString();
			}else{
				return "";
			}
		}
		return "";
	}

	public final static String composeWithRegex(Collection<String> unions, String regex){
		if(unions!=null&&unions.size()>0){
			return composeWithRegex(unions.toArray(new String[unions.size()]), regex);
		}
		return "";
	}

	public final static String filterPositiveIntStr(String input){
		if(!isValid(input))
			return "";
		try{
			String regEx="[^0-9]";
			Pattern pattern = Pattern.compile(regEx);
			Matcher matcher = pattern.matcher(input);
			return matcher.replaceAll("").trim();
		}catch (Exception e){
			logger.warn("", e);
		}
		return "";
	}

	public final static int filterPositiveInt(String input){
		int res = -1;
		try{
			res = Integer.parseInt(filterPositiveIntStr(input));
		}catch (Exception e){
			logger.warn("", e);
		}
		if(res<0)
			res = 0;
		return res;
	}

}
