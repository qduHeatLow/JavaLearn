package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    //微信官方服务接口
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    public static final String APP_ID = "appid";
    public static final String SECRET = "secret";
    public static final String JS_CODE = "js_code";
    public static final String GRANT_TYPE = "grant_type";
    public static final String GRANT_TYPE_VALUE = "authorization_code";
    public static final String OPEN_ID = "openid";

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserLoginVO userlogin(UserLoginDTO userLoginDTO) {
        //1.调用微信官方接口 获取用户openid
        String openid = getOpenid(userLoginDTO.getCode());
        //2.判断openid是否为空 为空则登录失败 抛出业务异常
        if(openid == null){
            throw  new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //3.判断用户是否为新用户 如果新用户 则自动注册（即保存到表中）
        User user = userMapper.getByOpenid(openid);
        if(user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }


        //根据用户id构建token
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

        //封装VO
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .token(token)
                .openid(user.getOpenid())
                .build();
        return userLoginVO;
    }

    /**
     * 调用微信官方接口 获取用户openid
     * @param code
     * @return
     */
    private String getOpenid(String code){
        //1.调用微信官方接口 获取用户openid
        //https://api.weixin.qq.com/sns/jscode2session
        // ?appid=<AppId>&secret=<AppSecret>&js_code=<code>&grant_type=authorization_code
        //1.1 构建请求路径
        Map<String, String> map = new HashMap<>();
        map.put(APP_ID,weChatProperties.getAppid());
        map.put(SECRET,weChatProperties.getSecret());
        map.put(JS_CODE,code);
        map.put(GRANT_TYPE,GRANT_TYPE_VALUE);

        //1.2 发送请求 会返回json字符串
        String json = HttpClientUtil.doGet(WX_LOGIN, map);

        //1.3 使用alibaba.fastjson 解析json 从而得到openid
        JSONObject jsonObject = JSON.parseObject(json);
        return jsonObject.getString(OPEN_ID);
    }
}
