package com.zunza.buythedip_kotlin.news.repository

import com.zunza.buythedip_kotlin.news.entity.Topic
import com.zunza.buythedip_kotlin.news.entity.Topic.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TopicRepository : JpaRepository<Topic, Category>{
}
