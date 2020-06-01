package org.geekpower.service;

import java.util.Objects;

import org.geekpower.common.BaseError;
import org.geekpower.common.BaseException;
import org.geekpower.common.CurrentContext;
import org.geekpower.common.dto.UserDTO;
import org.geekpower.entity.UserPO;
import org.geekpower.form.UserParam;
import org.geekpower.repository.IUserRepository;
import org.geekpower.utils.CommonUtils;
import org.geekpower.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private IUserRepository userRepository;

	public String login(UserParam param) {
		UserPO userPO = userRepository.findByAccount(param.getAccount());

		if (Objects.isNull(userPO)) {
			throw new BaseException(BaseError.ACCOUNT_NOT_EXIT.getCode(), "账号不存在");
		}

		if (Objects.equals(param.getPassword(), userPO.getPassword()) == false) {
			throw new BaseException(BaseError.ACCOUNT_NOT_EXIT.getCode(), "密码不正确");
		}
		UserDTO userDTO = CommonUtils.copy(userPO, UserDTO.class);
		CurrentContext.setUser(userDTO);
		return JwtUtils.generateToken(userDTO, 3000);
	}

}
