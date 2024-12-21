package joy.program.joybot.controller.fileUploader.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileUploaderRequest {
    private MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }
}
