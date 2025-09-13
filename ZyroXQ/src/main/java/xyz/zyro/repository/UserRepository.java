package xyz.zyro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import xyz.zyro.entity.User;
import xyz.zyro.entity.type.AuthProviderType;

public interface UserRepository extends JpaRepository<User, String>{

	Optional<User> findByEmail(String email);

	Optional<User> findByProviderIdAndProviderType(String providerId,AuthProviderType authProviderType );
}
