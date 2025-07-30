package com.zunza.buythedip_kotlin.news.service

import com.zunza.buythedip_kotlin.infrastructure.gemini.GenAiService
import com.zunza.buythedip_kotlin.news.dto.FetchedNewsDto
import com.zunza.buythedip_kotlin.news.dto.TranslatedNewsDto
import com.zunza.buythedip_kotlin.news.util.parse
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {  }

@Service
class NewsTranslationService(
    private val genAiService: GenAiService,
) {
    suspend fun translateNews(fetchedNewsList: List<FetchedNewsDto>): List<TranslatedNewsDto> {
        return coroutineScope {
            val deferredTranslations: List<Deferred<TranslatedNewsDto>> = fetchedNewsList.map { fetchedNews ->
                    async(Dispatchers.IO) {
                        val response = translate(fetchedNews)
                        val parsed = parse(response)
                        TranslatedNewsDto.createOf(parsed, fetchedNews)
                    }
                }

            logger.info { "News successfully translated." }
            deferredTranslations.awaitAll()
        }
    }

    private fun translate(fetchedNews: FetchedNewsDto): String {
        return genAiService.generate(setPrompt(fetchedNews.headline, fetchedNews.summary))
    }

    private fun setPrompt(headline: String, summary: String): String {
        val body = "headline: $headline\nsummary: $summary\n"
        val prompt = """
			Translate only the headline and the summary paragraph (lede) of an English-language news article into Korean. Follow the rules below carefully:
			1.	Do not translate company names or person names (e.g., “Trump”, “Apple”); keep them in English.
			2.	Recognize that the content is a news article and use a tone appropriate for Korean news reporting.
			3.	Translate only the headline and the first summary (lede) paragraph.
			4.	Do not include anything other than the translation.
			5.	Format the output exactly like this:
			   headline: translated content
			
			   summary: translated content
			"""

        return body + prompt
    }

    private fun convertToTranslatedNewsDto(parsed: Map<String, String>, fetchedNews: FetchedNewsDto) {
        TranslatedNewsDto.createOf(parsed, fetchedNews)
    }
}
