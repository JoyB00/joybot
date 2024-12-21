package joy.program.joybot.controller.chat.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageDetectorRequest {
    private String message;
    private String imageName;

    public String getMessage() {
        return message;
    }

    public String getImageName() {
        return imageName;
    }
}
