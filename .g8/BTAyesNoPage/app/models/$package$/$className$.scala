package models.$package$

import utils.{Enumerable, RadioOption, WithName}

sealed trait $className$

object $className$ {

  case object Yes extends WithName("Yes") with $className$
  case object No extends WithName("No") with $className$

  val values: Set[$className$] = Set(
    Yes, No
  )

  val options: Set[RadioOption] = values.map {
    value =>
      RadioOption("$className;format="decap"$", value.toString)
  }

  implicit val enumerable: Enumerable[$className$] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}