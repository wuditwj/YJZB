package io.agora.tutorials.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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
