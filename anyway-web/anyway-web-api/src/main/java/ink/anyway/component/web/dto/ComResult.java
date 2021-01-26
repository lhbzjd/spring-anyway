package ink.anyway.component.web.dto;

/**
 * [添加说明]
 * <br>@author : 李海博(haibo_li@neusoft.com)
 * <br>@date : 2018/4/16 11:03
 * <br>@version : 1.0
 */
public class ComResult {
    protected boolean success;
    protected String msg;

    protected Object content;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
