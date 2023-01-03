/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package repositories

import com.mongodb.client.model.Updates.set
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Indexes.ascending
import org.mongodb.scala.model.Updates.combine
import org.mongodb.scala.model.{Filters, IndexModel, IndexOptions, UpdateOptions}
import play.api.Configuration
import play.api.libs.json._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.Codecs.toBson
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.{LocalDateTime, ZoneOffset}
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class DatedCacheMap(id: String, data: Map[String, JsValue], lastUpdated: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC))

object DatedCacheMap {
  implicit val dateFormat: Format[LocalDateTime] = MongoJavatimeFormats.localDateTimeFormat
  implicit val formats: OFormat[DatedCacheMap] = Json.format[DatedCacheMap]

  def apply(cacheMap: CacheMap): DatedCacheMap = DatedCacheMap(cacheMap.id, cacheMap.data)
}

class ReactiveMongoRepository(config: Configuration, mongo: MongoComponent)
    extends PlayMongoRepository[DatedCacheMap](
      mongoComponent = mongo,
      collectionName = config.get[String]("appName"),
      domainFormat = DatedCacheMap.formats,
      indexes = Seq(
        IndexModel(
          ascending("lastUpdated"),
          IndexOptions()
            .unique(false)
            .name("userAnswersExpiry")
            .expireAfter(config.get[Int]("mongodb.timeToLiveInSeconds"), TimeUnit.SECONDS)
        )
      )
    ) {

  def upsert(cm: CacheMap): Future[Boolean] = {
    val cmDocument = DatedCacheMap(cm)

    collection
      .updateOne(
        filter = Filters.equal("id", cmDocument.id),
        update = combine(
          set("data", toBson(cmDocument.data)),
          set("lastUpdated", cmDocument.lastUpdated)
        ),
        options = UpdateOptions().upsert(true))
      .headOption()
      .map {
        case Some(result) => result.wasAcknowledged()
        case _ => false
      }
  }

  def get(id: String): Future[Option[CacheMap]] = {
    collection.find(equal("id", id)).map (doc =>
      CacheMap(doc.id, doc.data)
    ).headOption()
  }
}

@Singleton
class SessionRepository @Inject()(config: Configuration, mongoComponent: MongoComponent) {
  private lazy val sessionRepository = new ReactiveMongoRepository(config, mongoComponent)
  def apply(): ReactiveMongoRepository = sessionRepository
}

