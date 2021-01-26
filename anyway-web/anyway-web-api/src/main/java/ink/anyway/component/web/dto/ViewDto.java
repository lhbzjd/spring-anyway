package ink.anyway.component.web.dto;

import java.util.HashMap;
import java.util.Map;

public class ViewDto<T> {

    private boolean success;

    private String message;

    private T data;

    public ViewDto(){
        super();
    }

    public ViewDto(boolean success, String message, T data) {
        super();
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> res = new HashMap<>();
        res.put("success", this.success);
        res.put("message", this.message);
        res.put("data", this.data);
        return res;
    }
}
