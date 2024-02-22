/*
 * Copyright 2024 HM Revenue & Customs
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

package connectors

import play.api.libs.json.{Format, Json}
import repositories.SessionRepository
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.{CascadeUpsert, LoggingUtil}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DataCacheConnector @Inject()(
                                    val sessionRepository: SessionRepository,
                                    val cascadeUpsert: CascadeUpsert
                                  )(implicit val ec: ExecutionContext) extends LoggingUtil{

  def save[A](cacheId: String, key: String, value: A)(implicit fmt: Format[A]): Future[CacheMap] =
    sessionRepository().get(cacheId).flatMap { optionalCacheMap =>
      val updatedCacheMap = cascadeUpsert(key, value, optionalCacheMap.getOrElse(new CacheMap(cacheId, Map())))
      sessionRepository().upsert(updatedCacheMap).map { _ =>
        updatedCacheMap
      }
    }

  def remove(cacheId: String, key: String): Future[Boolean] =
    sessionRepository().get(cacheId).flatMap { optionalCacheMap =>
      optionalCacheMap.fold(Future(false)) { cacheMap =>
        val newCacheMap = cacheMap copy (data = cacheMap.data - key)
        sessionRepository().upsert(newCacheMap)
      }
    }

  def fetch(cacheId: String): Future[Option[CacheMap]] =
    sessionRepository().get(cacheId)

  def getEntry[A](cacheId: String, key: String)(implicit fmt: Format[A]): Future[Option[A]] =
    fetch(cacheId).map { optionalCacheMap =>
      optionalCacheMap.flatMap { cacheMap =>
        cacheMap.getEntry(key)
      }
    }

  def addToCollection[A](cacheId: String, collectionKey: String, value: A)(implicit fmt: Format[A]): Future[CacheMap] =
    sessionRepository().get(cacheId).flatMap { optionalCacheMap =>
      val updatedCacheMap =
        cascadeUpsert.addRepeatedValue(collectionKey, value, optionalCacheMap.getOrElse(new CacheMap(cacheId, Map())))
      sessionRepository().upsert(updatedCacheMap).map { _ =>
        updatedCacheMap
      }
    }

  def removeFromCollection[A](cacheId: String, collectionKey: String, item: A)(
    implicit fmt: Format[A]): Future[CacheMap] =
    sessionRepository().get(cacheId).flatMap { optionalCacheMap =>
      optionalCacheMap.fold(throw new Exception(s"Couldn't find document with key $cacheId")) { cacheMap =>
        val newSeq = cacheMap.data(collectionKey).as[Seq[A]].filterNot(x => x == item)
        val newCacheMap = if (newSeq.isEmpty) {
          cacheMap copy (data = cacheMap.data - collectionKey)
        } else {
          cacheMap copy (data = cacheMap.data + (collectionKey -> Json.toJson(newSeq)))
        }

        sessionRepository().upsert(newCacheMap).map { _ =>
          newCacheMap
        }
      }
    }

  def replaceInCollection[A](cacheId: String, collectionKey: String, index: Int, item: A)(
    implicit fmt: Format[A]): Future[CacheMap] =
    sessionRepository().get(cacheId).flatMap { optionalCacheMap =>
      optionalCacheMap.fold(throw new Exception(s"Couldn't find document with key $cacheId")) { cacheMap =>
        val newSeq = cacheMap.data(collectionKey).as[Seq[A]].updated(index, item)
        val updatedCacheMap = cacheMap copy (data = cacheMap.data + (collectionKey -> Json.toJson(newSeq)))
        sessionRepository().upsert(updatedCacheMap).map { _ =>
          updatedCacheMap
        }
      }
    }
}
