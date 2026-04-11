package com.qi.repository;

import com.qi.modal.TilesLogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<TilesLogin,Long> {

    Optional<TilesLogin> findByEmail(String email);
}
