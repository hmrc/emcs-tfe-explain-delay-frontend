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

package generators

import models.{DelayReason, DelayType}
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryDelayDetailsUserAnswersEntry: Arbitrary[(DelayDetailsPage.type, JsValue)] =
    Arbitrary {
      for {
        page <- arbitrary[DelayDetailsPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDelayDetailsChoiceUserAnswersEntry: Arbitrary[(DelayDetailsChoicePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DelayDetailsChoicePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDelayReasonUserAnswersEntry: Arbitrary[(DelayReasonPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DelayReasonPage.type]
        value <- arbitrary[DelayReason].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDelayTypeUserAnswersEntry: Arbitrary[(DelayTypePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DelayTypePage.type]
        value <- arbitrary[DelayType].map(Json.toJson(_))
      } yield (page, value)
    }
}
