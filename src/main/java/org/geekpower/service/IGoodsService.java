package org.geekpower.service;

import org.geekpower.common.PageResult;
import org.geekpower.common.dto.GoodsApplyDTO;
import org.geekpower.common.dto.GoodsDTO;
import org.geekpower.form.ApplyParam;
import org.geekpower.form.AuditParam;
import org.geekpower.form.PageParam;

public interface IGoodsService {

    /**
     * 查询商品列表，已分页
     * 
     * @param param
     * @return
     */
    PageResult<GoodsDTO> getGoodsList(PageParam param);

    /**
     * 查询需要我审批的商品申请列表，已分页
     * 
     * @param param
     * @return
     */
    PageResult<GoodsApplyDTO> getGoodsApplyListByMyRole(PageParam param);

    /**
     * 商品发布，创建发布申请，内部需要启动工作流
     * 
     * @param param
     */
    void createApply(ApplyParam param);

    /**
     * 审批商品发布申请，内部需要驱动工作流
     * 
     * @param param
     */
    void auditApply(AuditParam param);

    /**
     * 最终通过商品发布申请，供工作流服务任务调用的方法
     * 
     * @param applyId
     */
    void passApply(int applyId);

    /**
     * 最终驳回商品发布申请，供工作流服务任务调用的方法
     * 
     * @param applyId
     */
    void rejectApply(int applyId);

}
