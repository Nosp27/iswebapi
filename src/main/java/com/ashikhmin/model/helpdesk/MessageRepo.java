package com.ashikhmin.model.helpdesk;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepo extends CrudRepository<Message, Integer> {
    List<Message> findByIssue_Id(int issueId);
}
