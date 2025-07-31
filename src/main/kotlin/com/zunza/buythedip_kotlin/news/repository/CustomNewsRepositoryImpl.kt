package com.zunza.buythedip_kotlin.news.repository

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.zunza.buythedip_kotlin.news.dto.NewsCursorRequest
import com.zunza.buythedip_kotlin.news.dto.NewsPageResponse
import com.zunza.buythedip_kotlin.news.dto.NewsPreview
import com.zunza.buythedip_kotlin.news.dto.NextCursor
import com.zunza.buythedip_kotlin.news.entity.QNews
import org.springframework.stereotype.Repository

@Repository
class CustomNewsRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
    private val news: QNews = QNews.news
) : CustomNewsRepository {

    companion object{
        private const val SIZE = 15L
    }

    override fun findNewsPageByCursor(newsCursor: NewsCursorRequest): NewsPageResponse {
        val result: MutableList<NewsPreview> = jpaQueryFactory
            .select(Projections.constructor(
                    NewsPreview::class.java,
                    news.id,
                    news.headline,
                    news.datetime
                )
            )
            .from(news)
            .where(cursorCondition(newsCursor))
            .orderBy(news.datetime.desc(), news.id.desc())
            .limit(SIZE + 1)
            .fetch()
            .toMutableList()

        val nextCursor = getNextCursor(result)
        val hasMore = result.hasMore()
        if (result.count() > SIZE) result.removeLast()

        return NewsPageResponse(
            result,
            nextCursor,
            hasMore
        )
    }

    private fun cursorCondition(newsCursor: NewsCursorRequest): BooleanExpression? {
        return when (newsCursor.datetime) {
            0L -> null
            else -> news.datetime.lt(newsCursor.datetime)
                .or(news.datetime.eq(newsCursor.datetime)
                    .and(news.id.lt(newsCursor.newsId)))
        }
    }

    private fun getNextCursor(result: MutableList<NewsPreview>): NextCursor {
        return when {
            result.count() > SIZE -> NextCursor(result.last().newsId, result.last().datetime)
            else -> NextCursor()
        }
    }

    private fun MutableList<NewsPreview>.hasMore(): Boolean = this.count() > SIZE
}
