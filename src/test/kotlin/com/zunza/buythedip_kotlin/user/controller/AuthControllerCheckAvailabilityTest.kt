package com.zunza.buythedip_kotlin.user.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.zunza.buythedip_kotlin.config.SecurityConfig
import com.zunza.buythedip_kotlin.user.dto.ValidationRequest
import com.zunza.buythedip_kotlin.user.exception.DuplicateAccountIdException
import com.zunza.buythedip_kotlin.user.exception.DuplicateNicknameException
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
class AuthControllerCheckAvailabilityTest(
    @MockkBean private val authService: AuthService,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val context: WebApplicationContext
) : BehaviorSpec({

    val restDocumentation = ManualRestDocumentation()
    val mockMvc = restDocMockMvcBuild(context, restDocumentation)

    beforeEach { restDocumentation.beforeTest(javaClass, it.name.testName) }
    afterEach { restDocumentation.afterTest() }

    Given("회원가입 시 중복체크 요청을 받았을 때") {
        val url = "/api/auth/validation/check-availability"

        When("유효한 accountId 라면") {
            val request = ValidationRequest(ValidationRequest.ValidationType.ACCOUNT_ID, "not_existing_id")
            every { authService.checkAvailability(request) } returns Unit

            Then("200 OK, 요청 성공여부(true)를 응답한다.") {
                mockMvc.post(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.code") { value(200) }
                    jsonPath("$.success") { value(true) }
                }
                .andDo {
                    handle (
                        document(
                            "auth-check-availability-conflict-account-id-success",
                            requestFields(
                                fieldWithPath("validationType").description("검사 유형 (ACCOUNT_ID or NICKNAME)"),
                                fieldWithPath("value").description("검사 대상 값")
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

        When("accountId가 이미 존재한다면") {
            val request = ValidationRequest(ValidationRequest.ValidationType.ACCOUNT_ID, "existing_id")
            every { authService.checkAvailability(request) } throws DuplicateAccountIdException()
            Then("에러 메시지, 409 Conflict, 요청 성공여부(false)를 응답한다.") {
                mockMvc.post(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andExpect {
                    status { isConflict() }
                    jsonPath("$.data.message") { value("이미 사용 중인 아이디 입니다.") }
                    jsonPath("$.code") { value(409) }
                    jsonPath("$.success") { value(false) }
                }
                .andDo {
                    handle(
                        document(
                            "auth-check-availability-conflict-account-id-fail",
                            requestFields(
                                fieldWithPath("validationType").description("검사 유형 (ACCOUNT_ID)"),
                                fieldWithPath("value").description("검사 대상 값")
                            ),
                            responseFields(
                                fieldWithPath("data.message").description("에러 메시지"),
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("success").description("성공 여부")
                            )
                        )
                    )
                }
            }
        }

        When("유효한 Nickname 이라면") {
            val request = ValidationRequest(ValidationRequest.ValidationType.NICKNAME, "not_existing_nickname")
            every { authService.checkAvailability(request) } returns Unit

            Then("200 OK, 요청 성공여부(true)를 응답한다.") {
                mockMvc.post(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.code") { value(200) }
                    jsonPath("$.success") { value(true) }
                }
                .andDo {
                    handle(
                        document(
                            "auth-check-availability-conflict-nickname-success",
                            requestFields(
                                fieldWithPath("validationType").description("검사 유형 (NICKNAME)"),
                                fieldWithPath("value").description("검사 대상 값")
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

        When("nickname이 이미 존재한다면") {
            val request = ValidationRequest(ValidationRequest.ValidationType.NICKNAME, "existing_nickname")
            every { authService.checkAvailability(request) } throws DuplicateNicknameException()
            Then("에러 메시지, 409 Conflict, 요청 성공여부(false)를 응답한다.") {
                mockMvc.post(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andExpect {
                    status { isConflict() }
                    jsonPath("$.data.message") { value("이미 사용 중인 닉네임 입니다.") }
                    jsonPath("$.code") { value(409) }
                    jsonPath("$.success") { value(false) }
                }
                .andDo {
                    handle(
                        document(
                            "auth-check-availability-conflict-nickname-fail",
                            requestFields(
                                fieldWithPath("validationType").description("검사 유형 (NICKNAME)"),
                                fieldWithPath("value").description("검사 대상 값")
                            ),
                            responseFields(
                                fieldWithPath("data.message").description("에러 메시지"),
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

