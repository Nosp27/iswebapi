package com.ashikhmin.model.helpdesk;

import org.springframework.data.repository.CrudRepository;

public interface IssueRepo extends CrudRepository<Issue, Integer> {

}
