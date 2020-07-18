package org.geekpower;

import java.io.Serializable;

import org.geekpower.entity.UserPO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTemplateTest {

    @Autowired
    private RedisTemplate<String, String> strRedisTemplate;
    @Autowired
    private RedisTemplate<String, Serializable> serializableRedisTemplate;

    @Test
    public void testString() {
        strRedisTemplate.opsForValue().set("strKey", "zwqh");
        System.out.println(strRedisTemplate.opsForValue().get("strKey"));
    }

    @Test
    public void testSerializable() {
        UserPO user = new UserPO();
        user.setUserId(1002);
        user.setUserName("宋印赠");
        user.setAccount("songyz");
        serializableRedisTemplate.opsForValue().set("user", user);
        UserPO user2 = (UserPO) serializableRedisTemplate.opsForValue().get("user");
        System.out.println("user:" + user2.getUserId() + "," + user2.getUserName() + "," + user2.getAccount());
    }

}
