package ink.anyway.component.web.dto;

import java.util.List;

/**
 * [添加说明]
 * <br>@author : 李海博(haibo_li@neusoft.com)
 * <br>@date : 2017/11/29 9:30
 * <br>@version : 1.0
 */
public class PageDto {

    private long total;
    private List<?> list;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }
}
