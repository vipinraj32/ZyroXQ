package xyz.zyro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import xyz.zyro.entity.Advertiser;

public interface AdvertiserRepository extends JpaRepository<Advertiser, String> {

}
