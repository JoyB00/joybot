package joy.program.joybot.controller.fileUploader;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import joy.program.joybot.controller.fileUploader.DTO.FileUploaderRequest;
import joy.program.joybot.model.WebResponse;
import joy.program.joybot.service.FileUploaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileUploaderController {
    private final FileUploaderService fileUploaderService;

    @PostMapping(path = "",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> uploadFile(@RequestBody FileUploaderRequest request){
        fileUploaderService.store(request.getFile());
        return WebResponse.<String>builder()
                .data("File uploaded successfully")
                .build();
    }

    @GetMapping(path="/{filename}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> downloadFile(@PathVariable String filename){
        Resource resource = fileUploaderService.loadAsResource(filename);
        return WebResponse.<String>builder()
                .data(resource.getFilename())
                .build();
    }


}
