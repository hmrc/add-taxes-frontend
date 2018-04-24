package models.other.oil.reverb

import utils.{Enumerable, RadioOption, WithName}

sealed trait MyNewPage

object MyNewPage {

  case object Option1 extends WithName("option1") with MyNewPage
  case object Option2 extends WithName("option2") with MyNewPage

  val values: Set[MyNewPage] = Set(
    Option1, Option2
  )

  val options: Set[RadioOption] = values.map {
    value =>
      RadioOption("myNewPage", value.toString)
  }

  implicit val enumerable: Enumerable[MyNewPage] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}