package org.geekpower.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.transaction.Transactional;

import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.geekpower.common.BaseError;
import org.geekpower.common.BaseException;
import org.geekpower.common.CurrentContext;
import org.geekpower.common.PageResult;
import org.geekpower.common.dto.GoodsApplyDTO;
import org.geekpower.common.dto.GoodsDTO;
import org.geekpower.common.dto.UserDTO;
import org.geekpower.common.enums.ApplyStatus;
import org.geekpower.common.enums.AuditStatus;
import org.geekpower.common.enums.GoodsStatus;
import org.geekpower.entity.GoodsApplyPO;
import org.geekpower.entity.GoodsAuditPO;
import org.geekpower.entity.GoodsPO;
import org.geekpower.form.ApplyParam;
import org.geekpower.form.AuditParam;
import org.geekpower.form.PageParam;
import org.geekpower.repository.IGoodsApplyRepository;
import org.geekpower.repository.IGoodsAuditRepository;
import org.geekpower.repository.IGoodsRepository;
import org.geekpower.service.IGoodsService;
import org.geekpower.utils.BeanCopier;
import org.geekpower.utils.RepositoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service("goodsService")
public class GoodsServiceImpl implements IGoodsService {

    private final String PROCESS_DEFINITION_ID = "applyPrcesss:1:8cfa6873-c8dd-11ea-9c02-a85e459e6193";

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private IGoodsRepository goodsRepository;
    @Autowired
    private IGoodsApplyRepository goodsApplyRepository;
    @Autowired
    private IGoodsAuditRepository goodsAuditRepository;

    @Override
    public PageResult<GoodsDTO> getGoodsList(PageParam param) {
        // Example<GoodsPO> example = Example.of(new GoodsPO());

        Page<GoodsPO> pageData = goodsRepository.findAll(RepositoryUtils.initPageable(param));

        List<GoodsDTO> goodsList = BeanCopier.copyList(pageData.getContent(), GoodsDTO.class);

        PageResult<GoodsDTO> result = new PageResult<>(param.getPageNo(), param.getPageSize(), goodsList);

        // 是否需要总页数
        if (param.isNeedTotal())
            result.setTotal(pageData.getTotalElements());

        return result;
    }

    @Override
    public PageResult<GoodsApplyDTO> getGoodsApplyListByMyRole(PageParam param) {
        UserDTO user = CurrentContext.getUser();
        GoodsApplyPO filter = new GoodsApplyPO();

        filter.setStatus(ApplyStatus.PROGRESS.getCode());
        filter.setRoleId(user.getRoleId());

        Example<GoodsApplyPO> example = Example.of(filter);

        Page<GoodsApplyPO> pageData = goodsApplyRepository.findAll(example, RepositoryUtils.initPageable(param));

        List<GoodsApplyDTO> applyList = BeanCopier.copyList(pageData.getContent(), GoodsApplyDTO.class);
        applyList.forEach(dto -> {
            GoodsPO goodsPO = goodsRepository.findById(dto.getGoodsId()).get();
            dto.setGoodsObj(BeanCopier.copy(goodsPO, GoodsDTO.class));
        });
        PageResult<GoodsApplyDTO> result = new PageResult<>(param.getPageNo(), param.getPageSize(), applyList);

        // 是否需要总页数
        if (param.isNeedTotal())
            result.setTotal(pageData.getTotalElements());

        return result;
    }

    @Override
    @Transactional
    public void createApply(ApplyParam param) {
        GoodsPO goods = goodsRepository.findById(param.getGoodsId())
                .orElseThrow(() -> new BaseException(BaseError.GOODS_NOT_FOUND.getCode(), "商品不存在"));

        if (Objects.equals(goods.getStatus(), GoodsStatus.APPLYED.getCode())
                || Objects.equals(goods.getStatus(), GoodsStatus.DEPLOY.getCode())) {
            throw new BaseException(BaseError.GOODS_CAN_NOT_DEPLOY.getCode(), "商品不能申请发布");
        }
        Date now = new Date();
        goods.setStatus(GoodsStatus.APPLYED.getCode());
        goods.setUpdateTime(now);

        goodsRepository.save(goods);

        GoodsApplyPO apply = new GoodsApplyPO();
        apply.setGoodsId(param.getGoodsId());
        apply.setStatus(ApplyStatus.PROGRESS.getCode());
        apply.setCreateTime(now);
        apply.setUpdateTime(now);
        goodsApplyRepository.save(apply);

        Map<String, Object> variables = new HashMap<>();
        variables.put("applyId", apply.getApplyId());
        variables.put("price", goods.getGoodsPrice().doubleValue());
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(PROCESS_DEFINITION_ID, variables);

        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId())
                .singleResult();

        int roleId = repositoryService.getBpmnModel(PROCESS_DEFINITION_ID).getMainProcess()
                .findFlowElementsOfType(UserTask.class).stream()
                .filter(node -> Objects.equals(node.getId(), task.getTaskDefinitionKey()))
                .filter(node -> Objects.nonNull(node.getCandidateGroups()) && node.getCandidateGroups().size() > 0)
                .map(node -> node.getCandidateGroups().get(0))//
                .map(Integer::parseInt)//
                .findFirst()//
                .orElseGet(() -> 0);

        apply.setInstanceId(processInstance.getProcessInstanceId());
        apply.setTaskId(task.getId());
        apply.setTaskName(task.getName());
        apply.setRoleId(roleId);
        goodsApplyRepository.save(apply);
    }

    @Override
    @Transactional
    public void auditApply(AuditParam param) {
        GoodsApplyPO apply = goodsApplyRepository.findById(param.getApplyId())
                .orElseThrow(() -> new BaseException(BaseError.APPLY_NOT_FOUND.getCode(), "申请不存在"));

        if (Objects.equals(apply.getStatus(), ApplyStatus.PROGRESS.getCode()) == false) {
            throw new BaseException(BaseError.APPLY_CAN_NOT_OPERAT.getCode(), "申请已经通过或驳回");
        }

        if (Objects.equals(apply.getTaskId(), param.getTaskId()) == false) {
            throw new BaseException(BaseError.APPLY_CAN_NOT_OPERAT.getCode(), "申请步骤不合法");
        }

        Date now = new Date();

        Map<String, Object> variables = new HashMap<>();
        variables.put("flag", param.isFlag());
        variables.put("auditAdvice", param.getAuditAdvice());
        taskService.complete(param.getTaskId(), variables);

        GoodsAuditPO audit = new GoodsAuditPO();
        audit.setApplyId(apply.getApplyId());
        audit.setTaskId(apply.getTaskId());
        audit.setTaskName(apply.getTaskName());
        audit.setStatus((param.isFlag() ? AuditStatus.PASS : AuditStatus.REJECT).getCode());

        audit.setAuditUser(CurrentContext.getUser().getUserId());
        audit.setAuditTime(now);
        audit.setAuditAdvice(param.getAuditAdvice());
        goodsAuditRepository.save(audit);

        Task task = taskService.createTaskQuery().processInstanceId(apply.getInstanceId()).singleResult();
        if (Objects.isNull(task)) {
            apply.setTaskId(null);
            apply.setTaskName(null);
            apply.setRoleId(-1);
        }
        else {
            int roleId = repositoryService.getBpmnModel(PROCESS_DEFINITION_ID).getMainProcess()
                    .findFlowElementsOfType(UserTask.class).stream()
                    .filter(node -> Objects.equals(node.getId(), task.getTaskDefinitionKey()))
                    .filter(node -> Objects.nonNull(node.getCandidateGroups()) && node.getCandidateGroups().size() > 0)
                    .map(node -> node.getCandidateGroups().get(0))//
                    .map(Integer::parseInt)//
                    .findFirst()//
                    .orElseGet(() -> 0);

            apply.setTaskId(task.getId());
            apply.setTaskName(task.getName());
            apply.setRoleId(roleId);
        }
        apply.setUpdateTime(now);
        goodsApplyRepository.save(apply);

    }

    @Override
    @Transactional
    public void passApply(int applyId) {
        System.out.println("调用通过方法：" + applyId);

        GoodsApplyPO applyPO = goodsApplyRepository.findById(applyId)
                .orElseThrow(() -> new BaseException(BaseError.APPLY_NOT_FOUND.getCode(), "申请不存在"));

        GoodsPO goodsPO = goodsRepository.findById(applyPO.getGoodsId())
                .orElseThrow(() -> new BaseException(BaseError.GOODS_NOT_FOUND.getCode(), "商品不存在"));

        Date now = new Date();
        applyPO.setStatus(ApplyStatus.PASS.getCode());
        applyPO.setUpdateTime(now);
        goodsApplyRepository.save(applyPO);

        goodsPO.setStatus(GoodsStatus.DEPLOY.getCode());
        goodsPO.setUpdateTime(now);
        goodsRepository.save(goodsPO);

    }

    @Override
    @Transactional
    public void rejectApply(int applyId) {
        System.out.println("调用驳回方法：" + applyId);
        
        GoodsApplyPO applyPO = goodsApplyRepository.findById(applyId)
                .orElseThrow(() -> new BaseException(BaseError.APPLY_NOT_FOUND.getCode(), "申请不存在"));

        GoodsPO goodsPO = goodsRepository.findById(applyPO.getGoodsId())
                .orElseThrow(() -> new BaseException(BaseError.GOODS_NOT_FOUND.getCode(), "商品不存在"));

        Date now = new Date();
        applyPO.setStatus(ApplyStatus.REJECT.getCode());
        applyPO.setUpdateTime(now);
        goodsApplyRepository.save(applyPO);

        goodsPO.setStatus(GoodsStatus.REJECT.getCode());
        goodsPO.setUpdateTime(now);
        goodsRepository.save(goodsPO);

    }

}
