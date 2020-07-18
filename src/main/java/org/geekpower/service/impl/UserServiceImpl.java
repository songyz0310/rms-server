package org.geekpower.service.impl;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    @Value("${spring.mail.username}")
    private String email;
    @Value("${server.host}")
    private String host;
    @Value("${server.port}")
    private String port;
    @Value("${server.servlet.context-path}")
    private String context;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IAuthorityService authorityService;
    @Autowired
    private RedisTemplate<String, Serializable> serializableRedisTemplate;

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
    public boolean registe(UserParam param) {
        long count = userRepository.countByAccount(param.getAccount());
        if (count > 0) {
            throw new BaseException(BaseError.SEC_REPEAT_ACCOUNT.getCode(), "账号已经被占用");
        }

        count = userRepository.countByEmail(param.getEmail());
        if (count > 0) {
            throw new BaseException(BaseError.SEC_REPEAT_EMAIL.getCode(), "邮箱已经被占用");
        }

        String key = UUID.randomUUID().toString();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(email);
        message.setTo(param.getEmail());
        message.setSubject("激活链接");
        message.setText("您的激活链接：" + host + ":" + port + context + "/user/activate?key=" + key + " ,三十分钟有效");

        serializableRedisTemplate.opsForValue().set(key, param, 30, TimeUnit.MINUTES);

        mailSender.send(message);

        return true;
    }

    @Override
    public void activate(String key) {
        UserParam user = (UserParam) serializableRedisTemplate.opsForValue().get(key);
        if (Objects.isNull(user)) {
            throw new BaseException(BaseError.SEC_ACTIVATE_TIMEOUT.getCode(), "激活链接超时");
        }
        serializableRedisTemplate.delete(key);
        UserPO userPO = BeanCopier.copy(user, UserPO.class);

        userRepository.save(userPO);
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
