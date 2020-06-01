package org.geekpower.service;

import org.geekpower.common.dto.UserDTO;
import org.geekpower.common.dto.UserSessionDTO;

public interface IAuthorityService {

    public UserSessionDTO createSession(UserDTO userParam);

    public UserSessionDTO checkSession(String token);

}
