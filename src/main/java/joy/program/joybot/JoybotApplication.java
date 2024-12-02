package joy.program.joybot;

import joy.program.joybot.config.StorageConfig;
import joy.program.joybot.service.FileUploaderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties({StorageConfig.class})
public class JoybotApplication {

	public static void main(String[] args) {
		SpringApplication.run(JoybotApplication.class, args);

	}

	@Bean
	CommandLineRunner init (FileUploaderService fileUploaderService){
		return (args) -> {
//			fileUploaderService.deleteAll();
			fileUploaderService.init();
		};
	}

}
