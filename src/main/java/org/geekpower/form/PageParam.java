package org.geekpower.form;

import java.util.List;

/**
 * 分页请求对象
 * 
 * @author songyz
 * @createTime 2020-05-31 10:37:51
 */
public class PageParam extends SearchParam {

    private int pageNo = 1;
    private int pageSize = 10;
    private boolean needTotal = false;

    private List<Integer> includes;// 分页查询时，必须包含的记录
    private List<Integer> excludes;// 分页查询时，必须排除的记录

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

    public List<Integer> getIncludes() {
        return includes;
    }

    public void setIncludes(List<Integer> includes) {
        this.includes = includes;
    }

    public List<Integer> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<Integer> excludes) {
        this.excludes = excludes;
    }

}
