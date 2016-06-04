package cdzdapp.repository;

import cdzdapp.domain.User;

public interface UserRepository {
    User getUserById(Integer id);

    User getUserByName(String name);

    void addUser(User user);
}
