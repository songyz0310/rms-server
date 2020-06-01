package org.geekpower.utils;

import org.apache.commons.lang3.StringUtils;
import org.geekpower.form.PageParam;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public class RepositoryUtils {

    /**
     * 根据分页参数创建分页对象
     * 
     * 
     * @param param
     * @return
     */
    public static Pageable initPageable(PageParam param) {
        // 分页信息：前端从1开始，jpa从0开始，做个转换
        int page = param.getPageNo() - 1;
        int size = param.getPageSize();

        if (StringUtils.isNotEmpty(param.getSort())) {
            Direction direction = param.getSort().indexOf("+") == 0 ? Direction.ASC : Direction.DESC;

            Sort sort = Sort.by(direction, param.getSort().substring(1));

            return PageRequest.of(page, size, sort);
        }
        else {
            return PageRequest.of(page, size);
        }
    }

}
