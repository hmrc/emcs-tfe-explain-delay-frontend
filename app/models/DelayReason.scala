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

package models

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait DelayReason

object DelayReason extends Enumerable.Implicits {

  case object Other extends WithName("0") with DelayReason
  case object CancelledCommercialTransaction extends WithName("1") with DelayReason
  case object PendingCommercialTransaction extends WithName("2") with DelayReason
  case object OngoingInvestigation extends WithName("3") with DelayReason
  case object BadWeather extends WithName("4") with DelayReason
  case object Strikes extends WithName("5") with DelayReason
  case object Accident extends WithName("6") with DelayReason

  val values: Seq[DelayReason] = Seq(
    CancelledCommercialTransaction,
    PendingCommercialTransaction,
    Accident,
    BadWeather,
    Strikes,
    OngoingInvestigation,
    Other
  )

  def options(implicit messages: Messages): Seq[RadioItem] = values.zipWithIndex.map {
    case (value, index) =>
      RadioItem(
        content = Text(messages(s"delayReason.${value.toString}")),
        value = Some(value.toString),
        id = Some(s"value_$index")
      )
  }

  implicit val enumerable: Enumerable[DelayReason] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
