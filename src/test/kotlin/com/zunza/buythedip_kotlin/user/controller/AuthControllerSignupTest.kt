package com.zunza.buythedip_kotlin.user.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.zunza.buythedip_kotlin.config.SecurityConfig
import com.zunza.buythedip_kotlin.user.dto.SignupRequest
import com.zunza.buythedip_kotlin.user.service.AuthService
import com.zunza.buythedip_kotlin.util.restDocMockMvcBuild
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.ManualRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.post
import org.springframework.web.context.WebApplicationContext

@Import(SecurityConfig::class)
@WebMvcTest(AuthController::class)
class AuthControllerSignupTest(
    @MockkBean private val authService: AuthService,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val context: WebApplicationContext
) : BehaviorSpec({

    val restDocumentation = ManualRestDocumentation()
    val mockMvc = restDocMockMvcBuild(context, restDocumentation)

    beforeEach { restDocumentation.beforeTest(javaClass, it.name.testName) }
    afterEach { restDocumentation.afterTest() }

    Given("회원가입 요청 시") {
        val url = "/api/auth/signup"
        When("유효한 요청 이라면") {
            val signupRequest = SignupRequest("testId", "testpassword1!", "tester")
            every { authService.signup(signupRequest) } returns Unit

            Then("201 CREATED, 요청 성공여부(true)를 응답한다.") {
                mockMvc.post(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(signupRequest)
                }
                .andExpect {
                    status { isCreated() }
                    jsonPath("$.code") { value(201) }
                    jsonPath("$.success") { value(true) }
                }
                .andDo {
                    handle(
                        document(
                            "signup-success",
                            requestFields(
                                fieldWithPath("accountId").description("계정 ID"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("nickname").description("닉네임")
                            ),
                            responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("success").description("성공 여부")
                            )
                        )
                    )
                }
            }
        }
    }
})
