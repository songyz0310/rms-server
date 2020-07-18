package org.geekpower.web;

import org.geekpower.common.PageResult;
import org.geekpower.common.ParameterValidator;
import org.geekpower.common.RpcResponse;
import org.geekpower.common.Tuple;
import org.geekpower.common.dto.GoodsApplyDTO;
import org.geekpower.common.dto.GoodsDTO;
import org.geekpower.form.ApplyParam;
import org.geekpower.form.AuditParam;
import org.geekpower.form.PageParam;
import org.geekpower.service.IGoodsService;
import org.geekpower.utils.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    private static Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    private IGoodsService goodsService;

    /**
     * 查询商品列表接口
     * 
     * @param param
     * @return
     */
    @GetMapping("/list")
    public RpcResponse<PageResult<GoodsDTO>> getGoodsList(PageParam param) {
        if (logger.isDebugEnabled())
            logger.debug("查询商品列表参数:{}", GsonUtil.toJson(param));

        try {
            return new RpcResponse<>(goodsService.getGoodsList(param));
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

    /**
     * 查询商品列表接口
     * 
     * @param param
     * @return
     */
    @GetMapping("/task/list")
    public RpcResponse<PageResult<GoodsApplyDTO>> getGoodsApplyListByMyRole(PageParam param) {
        if (logger.isDebugEnabled())
            logger.debug("查询商品列表参数:{}", GsonUtil.toJson(param));

        try {
            return new RpcResponse<>(goodsService.getGoodsApplyListByMyRole(param));
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

    /**
     * 创建商品发布申请
     * 
     * @param param
     * @return
     */
    @PostMapping("/create/apply")
    public RpcResponse<Boolean> createApply(@RequestBody ApplyParam param) {
        if (logger.isDebugEnabled())
            logger.debug("创建商品发布申请参数:{}", GsonUtil.toJson(param));

        try {
            goodsService.createApply(param);
            return new RpcResponse<>(true);
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

    /**
     * 审核商品发布申请
     * 
     * @param param
     * @return
     */

    @PostMapping("/audit/apply")
    public RpcResponse<Boolean> auditApply(@RequestBody AuditParam param) {
        if (logger.isDebugEnabled())
            logger.debug("审核商品发布申请参数:{}", GsonUtil.toJson(param));

        try {
            goodsService.auditApply(param);
            return new RpcResponse<>(true);
        }
        catch (Exception exp) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(exp);
            return new RpcResponse<>(error.getFirst(), error.getSecond());
        }
    }

}
