package com.agritech.recommendation.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class ChatServiceTest {
    @Test
    void sendsPromptToConfiguredOllamaModel() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        ChatService service = new ChatService(builder, "http://ollama.test", "qwen2.5:0.5b");

        server.expect(requestTo("http://ollama.test/api/generate"))
                .andExpect(method(POST))
                .andExpect(content().json("""
                        {"model":"qwen2.5:0.5b","prompt":"Explain AI simply","stream":false}
                        """))
                .andRespond(withSuccess(
                        """
                        {"model":"qwen2.5:0.5b","response":"AI finds patterns in data."}
                        """,
                        MediaType.APPLICATION_JSON));

        ChatResponse response = service.chat("Explain AI simply");

        assertThat(response.model()).isEqualTo("qwen2.5:0.5b");
        assertThat(response.response()).isEqualTo("AI finds patterns in data.");
        server.verify();
    }
}
