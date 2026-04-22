package com.backed.service;

import com.backed.entity.User;
import com.backed.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public List<User> findAll() {
        return userMapper.selectList(null);
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userMapper.selectById(id));
    }

    public Optional<User> findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public User save(User user) {
        userMapper.insert(user);
        return user;
    }

    public User update(User user) {
        userMapper.updateById(user);
        return user;
    }

    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return userMapper.existsByUsername(username);
    }

    public User toggleEnabled(Long id) {
        User user = findById(id).orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setEnabled(!user.getEnabled());
        userMapper.updateById(user);
        return user;
    }
}
