package joy.program.joybot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebResponse<T> {
    private T data;
    private String errors;

    // Private constructor to enforce the use of the builder
    private WebResponse(Builder<T> builder) {
        this.data = builder.data;
        this.errors = builder.errors;
    }

    // Getters
    public T getData() {
        return data;
    }

    public String getErrors() {
        return errors;
    }

    // Builder Class
    public static class Builder<T> {
        private T data;
        private String errors;

        // Setter methods for builder
        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> errors(String errors) {
            this.errors = errors;
            return this;
        }

        // Build method to create WebResponse
        public WebResponse<T> build() {
            return new WebResponse<>(this);
        }
    }

    // Static method to get a new builder instance
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }
}

