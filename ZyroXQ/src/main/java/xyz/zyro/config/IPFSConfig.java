package xyz.zyro.config;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import io.ipfs.api.IPFS;

@Configuration
//@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class IPFSConfig {

	IPFS ipfs;
	@Bean
	public IPFS IPFSConfigs() {
		return new IPFS("/ip4/127.0.0.1/tcp/5001");
	}
}
