package joy.program.joybot.controller.fileUploader;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import joy.program.joybot.controller.fileUploader.DTO.FileUploaderRequest;
import joy.program.joybot.model.WebResponse;
import joy.program.joybot.service.FileUploaderService;
import joy.program.joybot.service.ImageUploaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/file")
public class FileUploaderController {

    @Autowired
    private FileUploaderService fileUploaderService;

    @Autowired
    private ImageUploaderService imageUploaderService;


    @PostMapping(path = "",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> uploadFile(HttpServletRequest request) {

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");
        String directory = "";
        if(multipartRequest.getParameter("directory")!=null){
            directory = multipartRequest.getParameter("directory");
        }

        if (file == null || file.isEmpty()) {
            return WebResponse.<String>builder()
                    .data("No file uploaded")
                    .build();
        }

        // Simpan file ke sistem
        if (directory.equals("image")) {
            imageUploaderService.store(file);
        }
        fileUploaderService.store(file);

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
