package de.innovationhub.prox.projectservice;


import de.innovationhub.prox.projectservice.config.ProposalConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableConfigurationProperties({ProposalConfig.class})
public class ProjectServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProjectServiceApplication.class, args);
  }
}
