package com.my.expenditure.repository;

import com.my.expenditure.entity.Expenditure;
import com.my.expenditure.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpeditureRepository extends JpaRepository<Expenditure, Long> {

    List<Expenditure> findAllByUser(User user);

    @Query("SELECT e FROM Expenditure e where e.created=:created and e.user=:user")
    List<Expenditure> findAllByUserAndDate(@Param("created") String created, @Param("user") User user);
}

//public interface UserRepository extends JpaRepository<User, Long> {
//    @Query("select u from User u where u.firstname = :firstname or
//            u.lastname = :lastname")
//            User findByLastnameOrFirstname(@Param("lastname") String lastname,
//            @Param("firstname") String firstname);
//}