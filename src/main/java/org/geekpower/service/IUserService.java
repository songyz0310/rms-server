package org.geekpower.service;

import org.geekpower.common.PageResult;
import org.geekpower.common.dto.UserDTO;
import org.geekpower.common.dto.UserSessionDTO;
import org.geekpower.form.PageParam;
import org.geekpower.form.UserParam;

public interface IUserService {

    public UserSessionDTO login(UserParam param);

    public PageResult<UserDTO> queryUserList(PageParam param);

    public boolean registe(UserParam param);

    public void activate(String key);

}
