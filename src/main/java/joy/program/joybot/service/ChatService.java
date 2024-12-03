package joy.program.joybot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final VectorStore vectorStore;

    public Prompt promptTemplateConfig(String message, Resource promptTemplateResource) {
        PromptTemplate promptTemplate = new PromptTemplate(promptTemplateResource);
        Map<String,Object> promptParameters = new HashMap<>();
        promptParameters.put("input", message);
        promptParameters.put("documents", String.join("\n", findSimilarDocuments(message)));
        return promptTemplate.create(promptParameters);
    }

    private List<String> findSimilarDocuments(String message) {
        List<Document> similarDocument = vectorStore.similaritySearch(SearchRequest.query(message).withTopK(3));
        return similarDocument.stream().map(Document::getContent).toList();
    }
}
