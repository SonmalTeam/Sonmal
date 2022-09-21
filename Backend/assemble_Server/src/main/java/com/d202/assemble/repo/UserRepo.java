package com.d202.assemble.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.d202.assemble.dto.User;

public interface UserRepo extends JpaRepository<User, Long>{
	public Optional<User> findByEmail(String email);
}
