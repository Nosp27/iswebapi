package com.ashikhmin.model.helpdesk;

import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;

public interface MessageRepo extends CrudRepository<Message, Integer> {
    List<Message> findByIssue_Id(int issueId);
    List<Message> findAllByIssue_IdAndSendTimeAfterOrderBySendTimeAsc(
            int issue_id, Timestamp timestamp
    );
}
