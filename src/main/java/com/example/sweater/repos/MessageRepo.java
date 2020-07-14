package com.example.sweater.repos;

import com.example.sweater.domain.Message;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

//автоматически реализуется Спрингом, создавая бин messageRepo, позволяет создавать, читать, удалять и обновлять информацию в БД о сущностях
public interface MessageRepo extends CrudRepository<Message, Long> {

    List<Message> findByTag(String tag);

}
