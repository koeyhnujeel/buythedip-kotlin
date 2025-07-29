package com.zunza.buythedip_kotlin.common

import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@RestControllerAdvice
class GlobalResponseAdvice(
    private val httpServletResponse: HttpServletResponse
) : ResponseBodyAdvice<Any> {

    override fun supports(
        returnType: MethodParameter,
        converterType: Class<out HttpMessageConverter<*>?>
    ): Boolean {
        return returnType.containingClass.isAnnotationPresent(RestController::class.java)
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>?>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        val status: Int = httpServletResponse.status

        return when (body) {
            is ApiResponse<*> -> body
            null -> ApiResponse(null, status)
            else -> ApiResponse(body, status)
        }
    }

    @ExceptionHandler(CustomException::class)
    fun customExceptionHandler(e: CustomException): ResponseEntity<ApiResponse<ErrorResponse>> {
        val errorResponse = ErrorResponse(e.message)
        val apiResponse = ApiResponse(errorResponse, e.getStatusCode())
        return ResponseEntity.status(e.getStatusCode()).body(apiResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidExceptionHandler(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<ErrorResponse>> {
        val errorMessages = e.fieldErrors.map { it.defaultMessage }

        val message =  if (errorMessages.count() == 1) errorMessages[0] else null
        val messages = if (errorMessages.count() > 1) errorMessages else null

        val errorResponse = ErrorResponse(message, messages)
        val apiResponse = ApiResponse(errorResponse, e.statusCode.value())
        return ResponseEntity.status(e.statusCode).body(apiResponse)
    }
}
