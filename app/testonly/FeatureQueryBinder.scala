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

package testonly

import config.featureToggles.FeatureSwitch
import config.featureToggles.FeatureSwitch.FeatureSwitch
import play.api.mvc.QueryStringBindable

import scala.util.Try

object FeatureQueryBinder {

  implicit def queryBinder: QueryStringBindable[List[(FeatureSwitch, Boolean)]] =
    new QueryStringBindable[List[(FeatureSwitch, Boolean)]] {
      override def bind(
        key: String,
        params: Map[String, Seq[String]]): Option[Either[String, List[(FeatureSwitch, Boolean)]]] = {
        val requestedMaybeFeatureSwitchs: List[Option[FeatureSwitch]] = params.keySet.map(FeatureSwitch.fromQuery).toList
        if (requestedMaybeFeatureSwitchs.exists(_.isEmpty)) {
          Some(Left("contains unknown feature"))
        } else {
          val featuresWithMaybeSettings: List[(FeatureSwitch, Option[Seq[String]])] =
            requestedMaybeFeatureSwitchs.collect {
              case Some(feature) =>
                (feature, params.get(params.keySet.find(_.toLowerCase == feature.name.toLowerCase).get))
            }

          if (featuresWithMaybeSettings.exists(_._2.isEmpty)) {
            Some(Left("has missing settings"))
          } else if (featuresWithMaybeSettings.exists(_._2.exists(_.size > 1))) {
            Some(Left("contains duplicate settings for the same feature"))
          } else {
            val featuresWithMaybeSetting: List[(FeatureSwitch, Option[Boolean])] =
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

      override def unbind(key: String, features: List[(FeatureSwitch, Boolean)]): String =
        s"""$key=${features.map { case (feature, enable) => s"${feature.name}=$enable" }.mkString("&")}"""

    }

}
