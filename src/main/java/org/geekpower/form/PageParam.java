package org.geekpower.form;

/**
 * 分页请求对象
 * 
 * @author songyz
 * @createTime 2020-05-31 10:37:51
 */
public class PageParam extends SearchParam {

    private int pageNo = 1;
    private int pageSize = 20;
    private boolean needTotal = false;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isNeedTotal() {
        return needTotal;
    }

    public void setNeedTotal(boolean needTotal) {
        this.needTotal = needTotal;
    }

}
