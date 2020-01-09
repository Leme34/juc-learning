//package com.lee.juc.future.demo;
//
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.concurrent.TimeUnit;
//
///**
// * https://juejin.im/post/5d3c46d2f265da1b9163dbce
// */
//@Service
//public class UserServiceImpl implements UserService {
//
//    @Autowired
//    UserMapper userMapper;
//
//    @Autowired
//    SendService sendService;
//
//
//    @Transactional
//    @Override
//    public int save(UserDTO userDTO) {
//        User user = new User();
//        BeanCopyUtils.copy(userDTO, user);
//        int insert = userMapper.insert(user);
//        System.out.println("User 保存用户成功:" + user);
////        UserService currentProxy = UserService.class.cast(AopContext.currentProxy());
//        sendService.senMsg(user);
//        sendService.senEmail(user);
//        return insert;
//    }
//
//
//    @Async
//    public void senMsg(User user) {
//        try {
//            TimeUnit.SECONDS.sleep(2);
//            System.out.println("发送短信中:.....");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(Thread.currentThread().getName() + "给用户id:" + user.getId() + ",手机号:" + user.getMobile() + "发送短信成功");
////        return true;
//    }
//
//    @Async
//    public void senEmail(User user) {
//        try {
//            TimeUnit.SECONDS.sleep(3);
//            System.out.println("发送邮件中:.....");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println(Thread.currentThread().getName() + "给用户id:" + user.getId() + ",邮箱:" + user.getEmail() + "发送邮件成功");
////        return true;
//    }
//    @Override
//    public User selectById(Long userId) {
//        return userMapper.selectById(userId);
//    }
//
//    @Override
//    @Transactional
//    public int updateById(UserDTO userDTO) {
//        User user = new User();
//        user.setId(userDTO.getUserId());
//        user.setSex(userDTO.getSex());
//        user.setUsername(userDTO.getUsername());
//        user.setPassword(userDTO.getPassword());
//        return userMapper.updateById(user);
//    }
//
//
//    @Override
//    public long countFansCountByUserId(Long userId) {
//        try {
//            Thread.sleep(10000);
//            System.out.println("获取FansCount===睡眠:" + 10 + "s");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println("UserService获取FansCount的线程  " + Thread.currentThread().getName());
//        return 520;
//    }
//
//    @Override
//    public long countMsgCountByUserId(Long userId) {
//        System.out.println("UserService获取MsgCount的线程  " + Thread.currentThread().getName());
//        try {
//            Thread.sleep(10000);
//            System.out.println("获取MsgCount===睡眠:" + 10 + "s");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return 618;
//    }
//
//    @Override
//    public long countCollectCountByUserId(Long userId) {
//        System.out.println("UserService获取CollectCount的线程  " + Thread.currentThread().getName());
//        try {
//            Thread.sleep(10000);
//            System.out.println("获取CollectCount==睡眠:" + 10 + "s");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return 6664;
//    }
//
//    @Override
//    public long countFollowCountByUserId(Long userId) {
//        System.out.println("UserService获取FollowCount的线程  " + Thread.currentThread().getName());
//        try {
//            Thread.sleep(10000);
//            System.out.println("获取FollowCount===睡眠:" + 10 + "s");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return userMapper.countFollowCountByUserId(userId);
//    }
//
//    @Override
//    public long countRedBagCountByUserId(Long userId) {
//        System.out.println("UserService获取RedBagCount的线程  " + Thread.currentThread().getName());
//        try {
//            TimeUnit.SECONDS.sleep(4);
//            System.out.println("获取RedBagCount===睡眠:" + 4 + "s");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return 99;
//    }
//
//    @Override
//    public long countCouponCountByUserId(Long userId) {
//        System.out.println("UserService获取CouponCount的线程  " + Thread.currentThread().getName());
//        try {
//            TimeUnit.SECONDS.sleep(8);
//            System.out.println("获取CouponCount===睡眠:" + 8 + "s");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return 66;
//    }
//
//    @Override
//    public int save(UserVO userVO) {
//        System.out.println("userVO 保存用户成功:" + userVO);
//        return 1;
//    }
//
//
//}
