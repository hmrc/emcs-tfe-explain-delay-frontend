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
import fixtures.messages.ConfirmationMessages
import models.ConfirmationDetails
import models.DelayReason.Strikes
import models.DelayType.ReportOfReceipt
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.ConfirmationView

class ConfirmationViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  lazy val view = app.injector.instanceOf[ConfirmationView]

  "ConfirmationView" - {

    Seq(ConfirmationMessages.English, ConfirmationMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        implicit val doc: Document = Jsoup.parse(
          view(
            ConfirmationDetails(
              receipt = testConfirmationReference,
              delayType = ReportOfReceipt,
              delayReason = Strikes,
              delayDetails = None
            )
          ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.h2(1) -> messagesForLanguage.delayInformationH2,
          Selectors.p(1) -> messagesForLanguage.print,
          Selectors.h2(2) -> messagesForLanguage.whatNextH2,
          Selectors.p(2) -> messagesForLanguage.whatNextP1,
          Selectors.h2(3) -> messagesForLanguage.mayNeedToDoH2,
          Selectors.p(3) -> messagesForLanguage.mayNeedToDoP1,
          Selectors.p(4) -> messagesForLanguage.mayNeedToDoP2,
          Selectors.button -> messagesForLanguage.atAGlanceLink,
          Selectors.p(5) -> messagesForLanguage.feedback
        ))
      }
    }
  }
}
