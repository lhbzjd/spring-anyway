package ink.anyway.component.common;

import java.util.Arrays;
import java.util.List;

public class Constants {

    public static List<String> cityAreaList = Arrays.asList("沈阳市", "大连市", "鞍山市", "抚顺市", "本溪市", "丹东市", "锦州市", "营口市", "阜新市", "辽阳市", "盘锦市", "铁岭市", "朝阳市", "葫芦岛市");
    public static List<String> hljCityAreaList = Arrays.asList("哈尔滨市", "齐齐哈尔市", "牡丹江市", "佳木斯市", "大庆市", "鸡西市", "双鸭山市", "伊春市", "七台河市", "鹤岗市", "黑河市", "绥化市", "大兴安岭地区");

    public static final String ONLINE_USER_SESSION_NAME = "userOnLine";

    public static String COMMON_STATIC_PATH;

    public static final int IO_BUFFER_SIZE = 8192;

    public static final String DCMQRSCP_RECEIVE_LOG_PREFIX = "DCMQRSCP RECEIVED::";

    public static final String SSO_TOKEN_NAME = "__vt_param__";

    public static final String SESSION_USER = "_sessionUser";

    public enum BUSINESS_TYPE{
        DIAGNOSE("诊断", "BT001"), CONSULT("会诊", "BT002"), QUALITY("质控", "BT003");

        public final String name;
        public final String code;

        private BUSINESS_TYPE(String name, String code){
            this.name = name;
            this.code = code;
        }
    }

    public enum BU_CENTER_TYPE{

        COUNTY_DIAGNOSE("A1157B013E3D48F8AFE44926183DF38C", "MCT006", "县级诊断中心"), CITY_DIAGNOSE("86452281DDC54400BE765EF457F2EDE4", "MCT005", "市级诊断中心")
        , CITY_CONSULT("A535320F607A4E7E9F82ECB841C107D0", "MCT004", "市级会诊中心"), CITY_QUALITY("B9EF7D787F794F60B57E9236F3309DA7", "MCT003", "市级质控中心")
        , PROVINCE_CONSULT("0913041CBF1A46B7AD0BB79C6AE62115", "MCT002", "省级会诊中心"), PROVINCE_QUALITY("8ED248E562DF44298EA8D7DF3969D966", "MCT001", "省级质控中心");

        public final String typeId;
        public final String typeCode;
        public final String typeName;

        private BU_CENTER_TYPE(String typeId, String typeCode, String typeName) {
            this.typeId = typeId;
            this.typeCode = typeCode;
            this.typeName = typeName;
        }
    }

    public enum CHECK_WORKFLOW {
        CENTER("center_check", "中心审核"),//中心审核
        DOCTOR("doctor_check", "医生审核"),//医生审核
        DOCTOR_LEVEL("doctor_level_check", "医生级别审核"),//医生级别审核
        HOSPITAL("hospital_check", "医院审核");//医院审核

        // 成员变量
        public String key;
        public String value;

        // 构造方法
        CHECK_WORKFLOW(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public enum HASH_ALGORITHM{
        MD5("MD5"), SHA1("SHA-1"), SHA256("SHA-256");

        public final String value;

        private HASH_ALGORITHM(String value){
            this.value = value;
        }
    }
}
