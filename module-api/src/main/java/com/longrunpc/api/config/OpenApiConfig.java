package com.longrunpc.api.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.customizers.OpenApiCustomizer;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI attendanceOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Prography Attendance API")
                .description("출석 관리 서비스 API 문서")
                .version("v1")
                .contact(new Contact()
                    .name("Prography 11th Backend Team")));
    }

    @Bean
    public OpenApiCustomizer responseSchemaCustomizer() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }

            openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation -> {
                    if (operation.getResponses() == null) {
                        operation.setResponses(new ApiResponses());
                    }

                    ApiResponses responses = operation.getResponses();
                    ensureSuccessResponse(operation, responses);
                    ensureCommonFailureResponses(responses);
                    applyResponseContents(responses);
                })
            );
        };
    }

    private void ensureSuccessResponse(io.swagger.v3.oas.models.Operation operation, ApiResponses responses) {
        if (responses.keySet().stream().noneMatch(code -> code.startsWith("2"))) {
            String successDescription = operation.getSummary() == null
                ? "요청 성공"
                : operation.getSummary() + " 성공";
            responses.addApiResponse("200", new ApiResponse().description(successDescription));
        }
    }

    private void ensureCommonFailureResponses(ApiResponses responses) {
        if (responses.get("400") == null) {
            responses.addApiResponse("400", new ApiResponse().description("COMMON_001: 입력값이 올바르지 않습니다."));
        }
        if (responses.get("500") == null) {
            responses.addApiResponse("500", new ApiResponse().description("COMMON_002: 서버 내부 오류가 발생했습니다."));
        }
    }

    private void applyResponseContents(ApiResponses responses) {
        String apiResponseSchemaRef = findApiResponseSchemaRef(responses);

        responses.forEach((statusCode, response) -> {
            if (statusCode.startsWith("2")) {
                ensureContent(response, successResponseContent(apiResponseSchemaRef));
            } else {
                // Non-2xx must always expose unified error envelope: data=null, error={code,message}
                response.setContent(errorResponseContent());
            }
        });
    }

    private void ensureContent(ApiResponse response, Content defaultContent) {
        if (response.getContent() == null || response.getContent().isEmpty()) {
            response.setContent(defaultContent);
        }
    }

    private String findApiResponseSchemaRef(ApiResponses responses) {
        for (ApiResponse response : responses.values()) {
            if (response == null || response.getContent() == null) {
                continue;
            }
            for (MediaType mediaType : response.getContent().values()) {
                if (mediaType == null || mediaType.getSchema() == null) {
                    continue;
                }
                String ref = mediaType.getSchema().get$ref();
                if (ref != null && ref.contains("/ApiResponse")) {
                    return ref;
                }
            }
        }
        return null;
    }

    private Content successResponseContent(String apiResponseSchemaRef) {
        Schema<?> schema = apiResponseSchemaRef != null
            ? new Schema<>().$ref(apiResponseSchemaRef)
            : new ObjectSchema()
                .addProperty("success", new BooleanSchema().example(true))
                .addProperty("data", new ObjectSchema())
                .addProperty("error", new Schema<>().nullable(true).example(null))
                .required(List.of("success", "data"));

        MediaType mediaType = new MediaType().schema(schema);
        return new Content().addMediaType("application/json", mediaType);
    }

    private Content errorResponseContent() {
        ObjectSchema errorSchema = new ObjectSchema();
        errorSchema.addProperty("code", new Schema<String>().example("COMMON_001"));
        errorSchema.addProperty("message", new Schema<String>().example("입력값이 올바르지 않습니다."));

        ObjectSchema schema = new ObjectSchema();
        schema.addProperty("success", new BooleanSchema().example(false));
        schema.addProperty("data", new Schema<>().nullable(true).example(null));
        schema.addProperty("error", errorSchema);
        schema.setRequired(List.of("success", "error"));

        MediaType mediaType = new MediaType()
            .schema(schema)
            .example(sampleErrorResponse())
            .addExamples("invalid-input", new Example()
                .summary("유효성 검증 실패")
                .value(sampleErrorResponse("COMMON_001", "입력값이 올바르지 않습니다.")))
            .addExamples("internal-error", new Example()
                .summary("서버 내부 오류")
                .value(sampleErrorResponse("COMMON_002", "서버 내부 오류가 발생했습니다.")));

        return new Content().addMediaType("application/json", mediaType);
    }

    private java.util.Map<String, Object> sampleErrorResponse() {
        return sampleErrorResponse("COMMON_001", "입력값이 올바르지 않습니다.");
    }

    private java.util.Map<String, Object> sampleErrorResponse(String code, String message) {
        java.util.Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("success", false);
        response.put("data", null);
        response.put("error", java.util.Map.of(
            "code", code,
            "message", message
        ));
        return response;
    }
}
