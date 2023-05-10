package com.james.server.Repository;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.james.server.Model.Post;

@Repository
public class UploadRepo {
    
    @Autowired
    private JdbcTemplate template;

    final String UPLOAD_SQL="insert into posts values (null, ?,?)";
    final String GET_SQL="select * from posts where post_id = ?";

    public void upload(MultipartFile file, String comments){

        try (InputStream is = file.getInputStream()) {
            template.update(UPLOAD_SQL, comments, is);
        } catch (DataAccessException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public Post getBlob(Integer id){

        Post result = new Post();

        template.query(GET_SQL,
            (rs)->{
                
                    result.setComment(rs.getString("comments"));
                    result.setImage(rs.getBytes("picture"));
                
            },
            id
        );

        System.out.println(result.getComment());
        System.out.println(result.getImage());
        return result;

    }

}
