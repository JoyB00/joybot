package joy.program.joybot.controller.chat;

import com.detectlanguage.DetectLanguage;
import com.detectlanguage.Result;
import com.detectlanguage.errors.APIError;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class ChatController {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("classpath:/prompts/prompt-template.st")
    private Resource promptTemplateResource;

    public ChatController(ChatClient.Builder builder, PgVectorStore vectorStore, VectorStore vectorStore1) {
        this.chatClient = builder
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
                .build();
        this.vectorStore = vectorStore1;
    }

    @GetMapping("/")
    public String chat(@RequestParam String message) {
        PromptTemplate promptTemplate = new PromptTemplate(promptTemplateResource);
        Map<String,Object> promptParameters = new HashMap<>();
        promptParameters.put("input", message);
        promptParameters.put("documents", String.join("\n",findSimilarDocuments(message)));


        return chatClient.prompt(promptTemplate.create(promptParameters))
                .user(message)
                .call()
                .content();
    }

    private List<String> findSimilarDocuments(String message) {
        List<Document> similarDocument = vectorStore.similaritySearch(SearchRequest.query(message).withTopK(3));
        return similarDocument.stream().map(Document::getContent).toList();
    }
}
