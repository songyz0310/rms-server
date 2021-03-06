package org.geekpower.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.geekpower.common.BaseError;
import org.geekpower.common.BaseException;
import org.geekpower.common.PageResult;
import org.geekpower.common.dto.UserDTO;
import org.geekpower.common.dto.UserSessionDTO;
import org.geekpower.entity.UserPO;
import org.geekpower.form.PageParam;
import org.geekpower.form.UserParam;
import org.geekpower.repository.IUserRepository;
import org.geekpower.service.IAuthorityService;
import org.geekpower.service.IUserService;
import org.geekpower.utils.BeanCopier;
import org.geekpower.utils.RepositoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IAuthorityService authorityService;

    public UserSessionDTO login(UserParam param) {
        UserPO userPO = userRepository.queryByAccount(param.getAccount());

        if (Objects.isNull(userPO)) {
            throw new BaseException(BaseError.SEC_NO_ACCOUNT.getCode(), "账号不存在");
        }

        if (Objects.equals(param.getPassword(), userPO.getPassword()) == false) {
            throw new BaseException(BaseError.SEC_WRONG_PWD.getCode(), "密码不正确");
        }
        UserDTO userDTO = BeanCopier.copy(userPO, UserDTO.class);

        return authorityService.createSession(userDTO);
    }

    @Override
    public PageResult<UserDTO> queryUserList(PageParam param) {
        Pageable pageable = RepositoryUtils.initPageable(param);

        Page<UserPO> pageData = StringUtils.isEmpty(param.getSearch()) ? //
                userRepository.findAll(pageable) : //
                userRepository.queryUserByUserNameLike(param.getSearch(), pageable);

        List<UserDTO> pageList = BeanCopier.copyList(pageData.getContent(), UserDTO.class);

        // 必须包含的人员查询
        if (Objects.nonNull(param.getIncludes())) {
            Map<Integer, UserPO> map = userRepository.findAllById(param.getIncludes()).stream()
                    .collect(Collectors.toMap(po -> po.getUserId(), po -> po));
            for (UserDTO user : pageList) {
                map.remove(user.getUserId());
            }

            for (UserPO po : map.values()) {
                pageList.add(BeanCopier.copy(po, UserDTO.class));
            }
        }

        // 查询排除的记录
        if (Objects.nonNull(param.getExcludes())) {
            List<UserDTO> result = new LinkedList<UserDTO>();
            for (UserDTO user : pageList) {
                if (param.getExcludes().contains(user.getUserId()) == false) {
                    result.add(user);
                }
            }

            pageList = result;
        }

        return new PageResult<>(param.getPageNo(), param.getPageSize(), pageData.getTotalElements(), pageList);
    }

}
