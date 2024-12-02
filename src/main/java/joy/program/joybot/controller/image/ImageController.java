package joy.program.joybot.controller.image;

import joy.program.joybot.service.FileUploaderService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/image")
public class ImageController {

    private final ChatClient chatClient;
    private final FileUploaderService fileUploaderService;

    public ImageController(ChatClient.Builder builder, PgVectorStore vectorStore, FileUploaderService fileUploaderService) {
        this.chatClient = builder
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()))
                .build();
        this.fileUploaderService = fileUploaderService;
    }

    @GetMapping("")
    public String imageDescribe(@RequestParam String message) throws IOException {
        Resource imageResource = fileUploaderService.loadAsResource("images.jpeg");

        return chatClient.prompt(message)
                .user(u->u.text(message).media(MimeTypeUtils.IMAGE_JPEG, imageResource))
                .call()
                .content();
    }

}
