/*
 * Copyright 2021 HM Revenue & Customs
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

import java.util.concurrent.TimeUnit

import com.mongodb.client.model.Indexes.ascending
import javax.inject.{Inject, Singleton}
import org.joda.time.{DateTime, DateTimeZone}
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.{IndexOptions, UpdateOptions}
import org.mongodb.scala.{bson, model}
import play.api.Configuration
import play.api.libs.json._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.play.json.formats.MongoJodaFormats
import scala.concurrent.{ExecutionContext, Future}

case class DatedCacheMap(id: String, data: Map[String, JsValue], lastUpdated: DateTime = DateTime.now(DateTimeZone.UTC))

object DatedCacheMap {
  implicit val dateFormat: Format[DateTime] = MongoJodaFormats.dateTimeFormat
  implicit val formats: OFormat[DatedCacheMap] = Json.format[DatedCacheMap]

  def apply(cacheMap: CacheMap): DatedCacheMap = DatedCacheMap(cacheMap.id, cacheMap.data)
}

class ReactiveMongoRepository @Inject()(config: Configuration, mongo: MongoComponent)(implicit ec: ExecutionContext)
  extends PlayMongoRepository[DatedCacheMap](
    mongoComponent = mongo,
    collectionName = config.get[String]("appName"),
    domainFormat = DatedCacheMap.formats,
    indexes = Seq(model.IndexModel(
      ascending("lastUpdated"),
      IndexOptions()
        .name("userAnswersExpiry")
        .expireAfter(config.get[Int]("mongodb.timeToLiveInSeconds"), TimeUnit.SECONDS))
    )
  ) {

  def upsert(cm: CacheMap): Future[Boolean] = {
    val cmDocument = Json.toJson(DatedCacheMap(cm))
    val modifier = bson.BsonDocument(Json.obj("$set" -> cmDocument).toString())

    collection.updateOne(filter = equal("id", cm.id), update = modifier, options = UpdateOptions().upsert(true)).headOption() map(
      _ match {
        case Some(result) => result.wasAcknowledged()
        case _ => false
      }
      )
  }

  def get(id: String): Future[Option[CacheMap]] = {
    collection.find(equal("id", id)).map (x =>
      CacheMap(x.id, x.data)
    ).headOption()
  }

}

@Singleton
class SessionRepository @Inject()(config: Configuration, mongoComponent: MongoComponent)(implicit ec: ExecutionContext) {

  private lazy val sessionRepository = new ReactiveMongoRepository(config, mongoComponent)

  def apply(): ReactiveMongoRepository = sessionRepository
}

