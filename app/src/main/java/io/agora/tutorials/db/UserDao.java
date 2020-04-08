package io.agora.tutorials.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.agora.tutorials.entity.UserInfo;

@Dao
public interface UserDao {

    //增
    @Insert
    void insert(UserInfo... users);

    //删
    @Delete
    void delete(UserInfo... users);

    //改
    @Update
    void update(UserInfo... users);

    //查
    @Query("SELECT * FROM userInfo")
    List<UserInfo> getAllUsers();

    //根据字段查询
    @Query("SELECT * FROM UserInfo WHERE mobile= :mobile")
    UserInfo getUserByMobile(String mobile);

}
