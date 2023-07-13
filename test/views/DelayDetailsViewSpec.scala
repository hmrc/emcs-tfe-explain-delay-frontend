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
import fixtures.messages.DelayDetailsMessages
import forms.DelayDetailsFormProvider
import models.NormalMode
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.DelayDetailsView

class DelayDetailsViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "DetailsSelectItemView" - {
    Seq(DelayDetailsMessages.English, DelayDetailsMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        Seq("mandatory" -> true, "optional" -> false).foreach { fieldType =>

          s"when the information field is ${fieldType._1}" - {
            val fieldIsMandatory = fieldType._2

            val form = app.injector.instanceOf[DelayDetailsFormProvider].apply(fieldIsMandatory)
            val view = app.injector.instanceOf[DelayDetailsView]

            implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
            implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

            implicit val doc: Document = Jsoup.parse(view(form, fieldIsMandatory, NormalMode).toString())

            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.title -> messagesForLanguage.title,
              Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
              Selectors.h1 -> messagesForLanguage.heading,
              Selectors.hint -> messagesForLanguage.hint(fieldIsMandatory),
              Selectors.button -> messagesForLanguage.continue
            ))
          }

        }

      }
    }

  }

}
