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

package testonly

import play.api.mvc.QueryStringBindable
import playconfig.featuretoggle.Feature

import scala.util.Try

object FeatureQueryBinder {

  implicit def queryBinder: QueryStringBindable[List[(Feature, Boolean)]] =
    new QueryStringBindable[List[(Feature, Boolean)]] {
      override def bind(
        key: String,
        params: Map[String, Seq[String]]): Option[Either[String, List[(Feature, Boolean)]]] = {
        val requestedMaybeFeatures: List[Option[Feature]] = params.keySet.map(Feature.fromQuery).toList
        if (requestedMaybeFeatures.exists(_.isEmpty)) {
          Some(Left("contains unknown feature"))
        } else {
          val featuresWithMaybeSettings: List[(Feature, Option[Seq[String]])] =
            requestedMaybeFeatures.collect {
              case Some(feature) =>
                (feature, params.get(params.keySet.find(_.toLowerCase == feature.key.toLowerCase).get))
            }

          if (featuresWithMaybeSettings.exists(_._2.isEmpty)) {
            Some(Left("has missing settings"))
          } else if (featuresWithMaybeSettings.exists(_._2.exists(_.size > 1))) {
            Some(Left("contains duplicate settings for the same feature"))
          } else {
            val featuresWithMaybeSetting: List[(Feature, Option[Boolean])] =
              featuresWithMaybeSettings.collect {
                case (feature, Some(maybeBool)) => (feature, Try(maybeBool.head.toBoolean).toOption)
              }

            if (featuresWithMaybeSetting.exists(_._2.isEmpty)) {
              Some(Left("contains none boolean settings"))
            } else {
              Some(Right(featuresWithMaybeSetting.collect { case (feature, Some(bool)) => (feature, bool) }))
            }
          }
        }
      }

      override def unbind(key: String, features: List[(Feature, Boolean)]): String =
        s"""$key=${features.map { case (feature, enable) => s"${feature.key}=$enable" }.mkString("&")}"""

    }

}
