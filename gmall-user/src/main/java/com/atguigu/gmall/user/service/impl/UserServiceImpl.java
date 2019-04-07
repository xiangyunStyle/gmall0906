package com.atguigu.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.service.UserService;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    UserAddressMapper userAddressMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<UserAddress> getAddressList() {
        return userAddressMapper.selectAll();
    }



    @Override
    public List<UserAddress> getAddressListByUserId(String userId) {

        UserAddress userAddress =  new UserAddress();
        userAddress.setUserId(userId);
        List<UserAddress> userAddresses = userAddressMapper.select(userAddress);

        return userAddresses;
    }

    @Override
    public UserAddress getAddressById(String addressId) {

        UserAddress userAddress = new UserAddress();
        userAddress.setId(addressId);
        UserAddress userAddress1 = userAddressMapper.selectOne(userAddress);

        return userAddress1;
    }

    @Override
    public List<UserInfo> getUserList() {

        //List<UserInfo> userInfos = userInfoMapper.selectUserList();

        List<UserInfo> userInfos = userInfoMapper.selectAll();
        return userInfos;
    }

    @Override
    public UserInfo login(UserInfo userInfo) {

        // 先查缓存

        UserInfo userParam = new UserInfo();
        userParam.setLoginName(userInfo.getLoginName());
        userParam.setPasswd(userInfo.getPasswd());
        UserInfo userLogin = userInfoMapper.selectOne(userParam);

        return userLogin;
    }

    @Override
    public void addUserCache(UserInfo userLogin) {
        Jedis jedis = redisUtil.getJedis();

        // 设置用户缓存
        jedis.setex("user:"+userLogin.getId()+":info",60*60*24, JSON.toJSONString(userLogin));

        jedis.close();
    }
}
