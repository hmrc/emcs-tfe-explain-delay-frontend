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

package viewmodels.checkAnswers

import base.SpecBase
import fixtures.messages.DelayDetailsMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.DelayDetailsPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.link

class DelayDetailsSummarySpec extends SpecBase with Matchers {

  lazy val app = applicationBuilder().build()
  lazy val delayDetailsSummary = app.injector.instanceOf[DelayDetailsSummary]
  lazy val link = app.injector.instanceOf[link]


  ".row" - {

    Seq(DelayDetailsMessages.English, DelayDetailsMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(app, messagesForLanguage.lang)

        "when there's no answer" - {

          "when showAction links is true" - {

            "must output the expected data" in {

              delayDetailsSummary.row(showActionLinks = true)(emptyUserAnswers, msgs) mustBe
                Some(SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = ValueViewModel(HtmlContent(
                    link(
                      controllers.routes.DelayDetailsController.onPageLoad(testErn, testArc, CheckMode).url,
                      messagesForLanguage.noDelayInformationValue
                    )
                  ))
                ))
            }
          }

          "when showAction links is false" - {

            "must return None" in {

              delayDetailsSummary.row(showActionLinks = false)(emptyUserAnswers, msgs) mustBe None
            }
          }
        }

        "when there's an answer" - {

          "when showActionLinks is true" - {

            "must output the expected row" in {

              delayDetailsSummary.row(showActionLinks = true)(emptyUserAnswers.set(DelayDetailsPage, Some("some details")), msgs) mustBe
                Some(SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(HtmlContent("some details")),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.routes.DelayDetailsController.onPageLoad(testErn, testArc, CheckMode).url,
                      id = DelayDetailsPage
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                ))
            }
          }

          "when showActionLinks is false" - {

            "must output the expected row without change links" in {

              delayDetailsSummary.row(showActionLinks = false)(emptyUserAnswers.set(DelayDetailsPage, Some("some details")), msgs) mustBe
                Some(SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(HtmlContent("some details")),
                  actions = Seq()
                ))
            }
          }
        }
      }
    }
  }
}
