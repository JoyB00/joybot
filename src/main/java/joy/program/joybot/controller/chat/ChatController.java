package joy.program.joybot.controller.chat;

import joy.program.joybot.controller.chat.DTO.ChatRequest;
import joy.program.joybot.service.FileUploaderService;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final FileUploaderService fileUploaderService;

    @Value("classpath:/prompts/prompt-template.st")
    private Resource promptTemplateResource;

    public ChatController(ChatClient.Builder builder, PgVectorStore vectorStore, VectorStore vectorStore1, FileUploaderService fileUploaderService) {
        this.chatClient = builder
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()))
                .build();
        this.vectorStore = vectorStore1;
        this.fileUploaderService = fileUploaderService;
    }

    @PostMapping(path = ""
            , produces = MediaType.APPLICATION_JSON_VALUE
            , consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public String chat(@RequestBody ChatRequest request) {
        PromptTemplate promptTemplate = new PromptTemplate(promptTemplateResource);
        Map<String,Object> promptParameters = new HashMap<>();
        promptParameters.put("input", request.getMessage());
        promptParameters.put("documents", String.join("\n", findSimilarDocuments(request.getMessage())));

        return chatClient.prompt(promptTemplate.create(promptParameters))
                .user(request.getMessage())
                .call()
                .content();
    }

    @GetMapping("/image-detector")
    public String imageDescribe(@RequestParam String message) throws IOException {
        Resource imageResource = fileUploaderService.loadAsResource("images.jpeg");
        PromptTemplate promptTemplate = new PromptTemplate(promptTemplateResource);
        Map<String,Object> promptParameters = new HashMap<>();
        promptParameters.put("input", message);
        promptParameters.put("documents", String.join("\n", findSimilarDocuments(message)));

        return chatClient.prompt(promptTemplate.create(promptParameters))
                .user(u->u.text(message).media(MimeTypeUtils.IMAGE_JPEG, imageResource))
                .call()
                .content();
    }

    private List<String> findSimilarDocuments(String message) {
        List<Document> similarDocument = vectorStore.similaritySearch(SearchRequest.query(message).withTopK(3));
        return similarDocument.stream().map(Document::getContent).toList();
    }
}
