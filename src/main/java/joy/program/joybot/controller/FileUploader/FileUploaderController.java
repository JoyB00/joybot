package joy.program.joybot.controller.FileUploader;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import joy.program.joybot.controller.FileUploader.DTO.FileUploaderRequest;
import joy.program.joybot.model.WebResponse;
import joy.program.joybot.service.FileUploaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FileUploaderController {
    private final FileUploaderService fileUploaderService;

    @PostMapping(path = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> uploadFile(@RequestBody FileUploaderRequest request){
        fileUploaderService.store(request.getFile());
        return WebResponse.<String>builder()
                .data("File uploaded successfully")
                .build();
    }


}
