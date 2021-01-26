package ink.anyway.component.web.dto;

public class NeuViewResponse {

    public enum ResponseInfo
    {
        SERVER_SUCCESS("2000", "success"), SERVER_ERROR("5000", ""), SERVER_DATA_NULL("4004", "no data"), SERVER_PARAMETER_ERROR("4061", "Request Parameter Error");

        private String code;
        private String msg;

        private ResponseInfo(String code, String msg){
            this.code=code;
            this.msg=msg;
        }

        public String getCode()
        {
            return this.code; }

        public String getMsg() {
            return this.msg;
        }

        public static String getResponseMsg(String code) {
            ResponseInfo[] arr = values();
            for (int i = 0; i < arr.length; i++) {
                ResponseInfo responseInfo = arr[i];
                if (code.equals(responseInfo.getCode()))
                    return responseInfo.getMsg();
            }

            return SERVER_ERROR.getMsg();
        }
    }

    private String code;
    private Object data;
    private String message;

    public String getCode()
    {
        return this.code;
    }

    public NeuViewResponse(String code, Object data, String message)
    {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
