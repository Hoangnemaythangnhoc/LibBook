package com.example.libbook.repository.impl;

import com.example.libbook.entity.Tag;
import com.example.libbook.repository.TagRepository;
import com.example.libbook.utils.ConnectUtils;
import org.springframework.stereotype.Repository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;



@Repository
public class TagRepositoryImpl implements TagRepository {
    @Override
    public ArrayList<Tag> getAllTags() {
        String sql = "SELECT * FROM Tag";
        ArrayList<Tag> tags = new ArrayList<>();
        ConnectUtils db = ConnectUtils.getInstance();

        try (Connection connection = db.openConection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()){
                Tag tag = new Tag();
                tag.setTagId(resultSet.getInt("TagId"));
                tag.setTagName(resultSet.getString("TagName"));
                tags.add(tag);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return tags;
    }

    @Override
    public Tag getTagById(int id) {
        String sql = "SELECT * FROM [dbo].[Tag] WHERE TagID = ? ";
        ConnectUtils db = ConnectUtils.getInstance();

        try (Connection connection = db.openConection();
             PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1,id);
            try (ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()){
                    Tag tag = new Tag();
                    tag.setTagId(resultSet.getInt("TagID"));
                    tag.setTagName(resultSet.getString("TagName"));
                    return tag;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}
