package joy.program.joybot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
@Data
public class StorageConfig {

    private String location = "upload-dir";
    private String imageLocation = "upload-image";

    public String getLocation() {
        return location;
    }

    public String getImageLocation() {
        return imageLocation;
    }




}
