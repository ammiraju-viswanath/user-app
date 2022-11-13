package com.interview.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.interview.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {


	@Query(value= "select s.* from user_table s where "
			+ "s.id like %:keyword% or s.name like %:keyword% or s.address like %:keyword% or s.email like %:keyword%"
			, nativeQuery = true)
	Page<User> findByKeyword(Pageable pageble,  @Param ("keyword") String keyword);

}
