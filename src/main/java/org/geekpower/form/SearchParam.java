package org.geekpower.form;

import org.apache.commons.lang3.StringUtils;

/**
 * 搜索参数
 * 
 * @author songyz
 * @createTime 2020-05-31 10:41:37
 */
public class SearchParam {

    private String search;// 搜索内容
    private String sort;// 排序字段（+，正序，-，倒叙）

    public String getSearch() {
        if (StringUtils.isEmpty(search)) {
            return null;
        }
        else {
            return "%" + search + "%";
        }
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

}
