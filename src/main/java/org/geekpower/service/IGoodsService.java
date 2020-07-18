package org.geekpower.service;

import org.geekpower.common.PageResult;
import org.geekpower.common.dto.GoodsApplyDTO;
import org.geekpower.common.dto.GoodsDTO;
import org.geekpower.form.ApplyParam;
import org.geekpower.form.AuditParam;
import org.geekpower.form.PageParam;

public interface IGoodsService {

    PageResult<GoodsDTO> getGoodsList(PageParam param);

    PageResult<GoodsApplyDTO> getGoodsApplyListByMyRole(PageParam param);

    void createApply(ApplyParam param);

    void auditApply(AuditParam param);

    void passApply(int applyId);

    void rejectApply(int applyId);

}
