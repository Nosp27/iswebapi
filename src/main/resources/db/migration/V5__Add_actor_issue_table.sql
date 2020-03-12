CREATE TABLE actor_issue(
    actor_id int not null references actor(id),
    issue_id int not null references issue(id)
);