package dao;

import entity.User;
import java.util.List;
/**
 * @author 拾光
 * @version 1.0
 */
public interface UserDao {
    boolean saveUser(User user);
    User SelectOneUser(int id);
    List<User> SelectAllUser();
}
