package xyz.zyro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import xyz.zyro.entity.type.Role;

public interface RoleRepository extends JpaRepository<Role,Integer> {

}
	