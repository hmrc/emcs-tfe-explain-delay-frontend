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

package views

import base.ViewSpecBase
import fixtures.messages.DelayReasonMessages
import forms.DelayReasonFormProvider
import models.DelayType.{ConsigneeOrChangeOfDestination, ReportOfReceipt}
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.DelayReasonView

class DelayReasonViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  lazy val form = app.injector.instanceOf[DelayReasonFormProvider].apply()
  lazy val view = app.injector.instanceOf[DelayReasonView]

  "DetailsSelectItemView" - {

    Seq(DelayReasonMessages.English, DelayReasonMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        "when DelayType is `ReportOfReceipt`" - {

          implicit val doc: Document = Jsoup.parse(view(form, ReportOfReceipt, testOnwardRoute).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(messagesForLanguage.reportReceiptDelay),
            Selectors.h1 -> messagesForLanguage.heading(messagesForLanguage.reportReceiptDelay),
            Selectors.radioButton(1) -> messagesForLanguage.radioCancelled,
            Selectors.radioButton(2) -> messagesForLanguage.radioPending,
            Selectors.radioButton(3) -> messagesForLanguage.radioAccident,
            Selectors.radioButton(4) -> messagesForLanguage.radioWeather,
            Selectors.radioButton(5) -> messagesForLanguage.radioStrikes,
            Selectors.radioButton(6) -> messagesForLanguage.radioInvestigation,
            Selectors.radioButton(7) -> messagesForLanguage.radioOther,
            Selectors.button -> messagesForLanguage.continue
          ))
        }

        "when DelayType is `ChangeOfDestination`" - {

          implicit val doc: Document = Jsoup.parse(view(form, ConsigneeOrChangeOfDestination, testOnwardRoute).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(messagesForLanguage.changeOfDestinationDelay),
            Selectors.h1 -> messagesForLanguage.heading(messagesForLanguage.changeOfDestinationDelay)
          ))
        }
      }
    }
  }
}
