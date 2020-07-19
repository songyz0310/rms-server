package org.geekpower.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

/**
 * 商品页面管理，@Service注解添加名称是为了工作流获取Spring Bean时方便，默认该注解注入到Spring容器中，名称为类名且首字母小写
 * 
 * @author songyz
 * @createTime 2020-07-19 11:33:29
 */
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
        // 使用JPA分页查询
        Page<GoodsPO> pageData = goodsRepository.findAll(RepositoryUtils.initPageable(param));

        // 数据类型转换
        List<GoodsDTO> goodsList = BeanCopier.copyList(pageData.getContent(), GoodsDTO.class);

        // 构造返回对象
        PageResult<GoodsDTO> result = new PageResult<>(param.getPageNo(), param.getPageSize(), goodsList);

        // 是否需要总页数
        if (param.isNeedTotal())
            result.setTotal(pageData.getTotalElements());

        return result;
    }

    @Override
    public PageResult<GoodsApplyDTO> getGoodsApplyListByMyRole(PageParam param) {
        // 获取当前登录人员信息
        UserDTO user = CurrentContext.getUser();

        // JPA数据过滤对象
        GoodsApplyPO filter = new GoodsApplyPO();

        filter.setStatus(ApplyStatus.PROGRESS.getCode());// 查询申请状态为进行中的
        filter.setRoleId(user.getRoleId());// 查询审批角色为当前人员角色

        // 构造匹配器
        Example<GoodsApplyPO> example = Example.of(filter);

        // 分页过滤查询
        Page<GoodsApplyPO> pageData = goodsApplyRepository.findAll(example, RepositoryUtils.initPageable(param));

        // 数据类型转换
        List<GoodsApplyDTO> applyList = BeanCopier.copyList(pageData.getContent(), GoodsApplyDTO.class);

        // 构造返回值对象
        PageResult<GoodsApplyDTO> result = new PageResult<>(param.getPageNo(), param.getPageSize(), applyList);

        // 如果申请列表为空，直接返回空分页集合
        if (pageData.isEmpty())
            return result;

        // // 循环查找商品,由于频繁开启数据库查询链接，对于数据库压力过大，需要改进
        // applyList.forEach(dto -> {
        // GoodsPO goodsPO = goodsRepository.findById(dto.getGoodsId()).get();
        // dto.setGoodsObj(BeanCopier.copy(goodsPO, GoodsDTO.class));
        // });

        // 改进第一步，根据申请中的商品ID，进行去重
        Set<Integer> goodsIds = applyList.stream().map(GoodsApplyDTO::getGoodsId).collect(Collectors.toSet());

        // 改进第二步，通过ID集合一次性查询出所有的商品，先将它转换成DTO类型，在转换成Map，方便后续使用
        Map<Integer, GoodsDTO> goodsMap = goodsRepository.findAllById(goodsIds).stream()
                .map(po -> BeanCopier.copy(po, GoodsDTO.class))
                .collect(Collectors.toMap(GoodsDTO::getGoodsId, goods -> goods));

        // 改进第三步，循环申请，从Map中通过商品ID查找商品实体，进行赋值
        applyList.forEach(dto -> dto.setGoodsObj(goodsMap.get(dto.getGoodsId())));

        // 是否需要总页数
        if (param.isNeedTotal())
            result.setTotal(pageData.getTotalElements());

        return result;
    }

    @Override
    @Transactional
    public void createApply(ApplyParam param) {
        // 通过商品ID，查询商品
        GoodsPO goods = goodsRepository.findById(param.getGoodsId())
                .orElseThrow(() -> new BaseException(BaseError.GOODS_NOT_FOUND, param.getGoodsId()));

        // 判断商品状态，已申请和已发布的商品，禁止再次发布
        if (Objects.equals(goods.getStatus(), GoodsStatus.APPLYED.getCode())
                || Objects.equals(goods.getStatus(), GoodsStatus.DEPLOY.getCode())) {
            throw new BaseException(BaseError.GOODS_CAN_NOT_DEPLOY, param.getGoodsId());
        }

        // 修改商品状态
        Date now = new Date();
        goods.setStatus(GoodsStatus.APPLYED.getCode());
        goods.setUpdateTime(now);

        goodsRepository.save(goods);

        // 创建发布申请记录
        GoodsApplyPO apply = new GoodsApplyPO();
        apply.setGoodsId(param.getGoodsId());
        apply.setStatus(ApplyStatus.PROGRESS.getCode());
        apply.setCreateTime(now);
        apply.setUpdateTime(now);
        goodsApplyRepository.save(apply);

        // 构造流程变量集合
        Map<String, Object> variables = new HashMap<>();
        // 将申请ID绑定到流程变量中，后续的服务任务会使用到改参数
        variables.put("applyId", apply.getApplyId());
        // 将商品的价格绑定到流程变量中，后续的网关节点会使用到改参数
        variables.put("price", goods.getGoodsPrice().doubleValue());

        // 通过流程定义ID，启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(PROCESS_DEFINITION_ID, variables);

        // 通过流程实例ID查询当前实例的任务
        // 警告如果使用并行网关，可能查询到多个任务，此处会有异常
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId())
                .singleResult();

        // 通过流程定义ID查询流程定义信息，找到当前任务的审批角色配置
        int roleId = repositoryService.getBpmnModel(PROCESS_DEFINITION_ID).getMainProcess()// 查询流程定义模型
                .findFlowElementsOfType(UserTask.class)// 过滤流程中的人工任务
                .stream()//
                .filter(node -> Objects.equals(node.getId(), task.getTaskDefinitionKey()))// 找到当前实例的任务定义
                .filter(node -> Objects.nonNull(node.getCandidateGroups()) && node.getCandidateGroups().size() > 0)// 过滤配置了角色的
                .map(node -> node.getCandidateGroups().get(0))// 只获取第一个角色，警告如果需要配置多个角色则，全部解析存储
                .map(Integer::parseInt)// 由于系统角色ID是int类型，故在此进行转换
                .findFirst()// 匹配第一条
                .orElseGet(() -> 0);// 如果没有找到步骤，则将角色Id设置为0

        // 通过流程实例信息更新申请记录
        apply.setInstanceId(processInstance.getProcessInstanceId());
        apply.setTaskId(task.getId());
        apply.setTaskName(task.getName());
        apply.setRoleId(roleId);
        goodsApplyRepository.save(apply);
    }

    @Override
    @Transactional
    public void auditApply(AuditParam param) {
        // 通过申请ID查找申请
        GoodsApplyPO apply = goodsApplyRepository.findById(param.getApplyId())
                .orElseThrow(() -> new BaseException(BaseError.APPLY_NOT_FOUND, param.getApplyId()));

        // 判断申请状态，不等于进行中的，禁止审批操作
        if (Objects.equals(apply.getStatus(), ApplyStatus.PROGRESS.getCode()) == false) {
            throw new BaseException(BaseError.APPLY_CAN_NOT_OPERAT, param.getApplyId());
        }

        // 判断申请任务，如果任务Id与数据库当前任务Id不同，禁止审批操作
        if (Objects.equals(apply.getTaskId(), param.getTaskId()) == false) {
            throw new BaseException(BaseError.APPLY_CAN_NOT_OPERAT, param.getApplyId());
        }

        Date now = new Date();
        // 构造流程变量集合
        Map<String, Object> variables = new HashMap<>();
        // 将审批结果绑定到流程变量中，后续的网关判断依赖此变量
        variables.put("flag", param.isFlag());
        variables.put("auditAdvice", param.getAuditAdvice());

        // 调用流程引擎任务Api完成任务
        taskService.complete(param.getTaskId(), variables);

        // 保存审批记录
        GoodsAuditPO audit = new GoodsAuditPO();
        audit.setApplyId(apply.getApplyId());
        audit.setTaskId(apply.getTaskId());
        audit.setTaskName(apply.getTaskName());
        audit.setStatus((param.isFlag() ? AuditStatus.PASS : AuditStatus.REJECT).getCode());

        audit.setAuditUser(CurrentContext.getUser().getUserId());
        audit.setAuditTime(now);
        audit.setAuditAdvice(param.getAuditAdvice());
        goodsAuditRepository.save(audit);

        // 通过流程实例ID查询当前实例的任务
        Task task = taskService.createTaskQuery().processInstanceId(apply.getInstanceId()).singleResult();
        if (Objects.isNull(task)) {// 如果任务为空，需要清空审批记录中的任务信息
            apply.setTaskId(null);
            apply.setTaskName(null);
            apply.setRoleId(-1);
        }
        else {
            // 通过流程定义ID查询流程定义信息，找到当前任务的审批角色配置
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

        // 更新商品申请记录
        apply.setUpdateTime(now);
        goodsApplyRepository.save(apply);
    }

    @Override
    @Transactional
    public void passApply(int applyId) {
        // 通过申请ID查找申请
        GoodsApplyPO applyPO = goodsApplyRepository.findById(applyId)
                .orElseThrow(() -> new BaseException(BaseError.APPLY_NOT_FOUND, applyId));

        // 通过商品ID，查询商品
        GoodsPO goodsPO = goodsRepository.findById(applyPO.getGoodsId())
                .orElseThrow(() -> new BaseException(BaseError.GOODS_NOT_FOUND, applyPO.getGoodsId()));

        // 更新商品申请状态
        Date now = new Date();
        applyPO.setStatus(ApplyStatus.PASS.getCode());
        applyPO.setUpdateTime(now);
        goodsApplyRepository.save(applyPO);

        // 更新商品状态
        goodsPO.setStatus(GoodsStatus.DEPLOY.getCode());
        goodsPO.setUpdateTime(now);
        goodsRepository.save(goodsPO);
    }

    @Override
    @Transactional
    public void rejectApply(int applyId) {
        // 通过申请ID查找申请
        GoodsApplyPO applyPO = goodsApplyRepository.findById(applyId)
                .orElseThrow(() -> new BaseException(BaseError.APPLY_NOT_FOUND, applyId));

        // 通过商品ID，查询商品
        GoodsPO goodsPO = goodsRepository.findById(applyPO.getGoodsId())
                .orElseThrow(() -> new BaseException(BaseError.GOODS_NOT_FOUND, applyPO.getGoodsId()));

        // 更新商品申请状态
        Date now = new Date();
        applyPO.setStatus(ApplyStatus.REJECT.getCode());
        applyPO.setUpdateTime(now);
        goodsApplyRepository.save(applyPO);

        // 更新商品状态
        goodsPO.setStatus(GoodsStatus.REJECT.getCode());
        goodsPO.setUpdateTime(now);
        goodsRepository.save(goodsPO);
    }

}
