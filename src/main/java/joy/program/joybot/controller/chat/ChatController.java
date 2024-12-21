package joy.program.joybot.controller.chat;

import jakarta.servlet.http.HttpServletRequest;
import joy.program.joybot.controller.chat.DTO.ChatRequest;
import joy.program.joybot.controller.chat.DTO.ImageDetectorRequest;
import joy.program.joybot.model.WebResponse;
import joy.program.joybot.service.ChatService;
import joy.program.joybot.service.FileUploaderService;
import joy.program.joybot.service.ImageUploaderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatClient chatClient;
    private final ChatService chatService;
    private final FileUploaderService fileUploaderService;
    private final ImageUploaderService imageUploaderService;

    @Value("classpath:/prompts/prompt-template.st")
    private Resource promptTemplateResource;

    public ChatController(ChatClient.Builder builder, PgVectorStore vectorStore, ChatService chatService, FileUploaderService fileUploaderService, ImageUploaderService imageUploaderService) {
        this.chatClient = builder
//                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()))
                .build();
        this.chatService = chatService;
        this.fileUploaderService = fileUploaderService;
        this.imageUploaderService = imageUploaderService;
    }

    @PostMapping(path = ""
            , produces = MediaType.APPLICATION_JSON_VALUE
            , consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public String chat(@RequestBody ChatRequest request) {
        return chatClient.prompt(chatService.promptTemplateConfig(request.getMessage(), promptTemplateResource))
                .user(request.getMessage())
                .call()
                .content();
    }

    @PostMapping(path = "/image-detector"
            , produces = MediaType.APPLICATION_JSON_VALUE
            , consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public String imageDescribe(HttpServletRequest request) throws IOException {
        if (request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            String imageName = multipartRequest.getParameter("imageName");
            String message = multipartRequest.getParameter("message");

            Resource imageResource = imageUploaderService.loadAsResource(imageName);

            return chatClient.prompt()
                    .user(u -> u.text(message).media(MimeTypeUtils.IMAGE_JPEG, imageResource))
                    .call()
                    .content();
        } else {
            throw new IllegalArgumentException("Request is not a multipart request");
        }
//        String response = chatClient.prompt()
//                .user(u->u.text(request.getMessage()).media(MimeTypeUtils.IMAGE_JPEG, imageResource))
//                .call()
//                .content();
//        return WebResponse.<String>builder()
//                .data(response)
//                .build();
    }
}
