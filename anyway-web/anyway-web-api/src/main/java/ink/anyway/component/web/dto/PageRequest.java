package ink.anyway.component.web.dto;

/**
 * 前端分页请求封装，indexPage从0开始。
 */
public class PageRequest {

    /** 每页最大记录数 */
    private static final int MAX_SIZE = Integer.MAX_VALUE;

    /** 默认页码 */
    private static final int DEFAULT_INDEX = 0;

    /** 默认页大小 */
    private static final int DEFAULT_SIZE = 10;


    /** 页码，即第几页 */
    private int pageIndex = DEFAULT_INDEX;

    /** 页大小，不大于{@link #MAX_SIZE} */
    private int pageSize = DEFAULT_SIZE;


    public int getPageIndex() {
        return pageIndex + 1; // Mybatis PageHelper 页码从1开始，而前端从0开始传。
    }

    public void setPageIndex(int pageIndex) {
        if ( pageIndex > 0 )
            this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        if ( pageSize > 0 && pageSize <= MAX_SIZE )
            this.pageSize = pageSize;
    }
}
