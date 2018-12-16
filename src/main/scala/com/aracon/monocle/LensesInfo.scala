/*
 * Copyright 2018 Pere Villega
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

package com.aracon.monocle

import monocle.{ Lens, PLens }
import monocle.macros.GenLens

// See http://julien-truffaut.github.io/Monocle/optics/lens.html
object LensesInfo {
  // Lenses are the most common use of Monocle
  // A Lens is an optic used to zoom inside a Product, e.g. case class, Tuple, HList or even Map.
  // Lenses support
  // - set : to modify the lensed value
  // - get : to obtain the lensed value
  // - modify : calls get and then set on the lensed value. Can be used in a context with `modifyF`

  // Lenses compose, so they can be chained to access nested level
  // The motivation behind lenses is to modify nested classes in a structure without having to unwrap each level for each operation

  // Given this structure
  case class Street(number: Int, name: String)
  case class Address(city: String, street: Street)
  case class Company(name: String, address: Address)
  case class Employee(name: String, company: Company)

  // And this value
  val employee =
    Employee("john", Company("awesome inc", Address("london", Street(23, "high street"))))

  // By default we would need to do the following to modify the name of the street:
  employee.copy(
    company = employee.company.copy(
      address = employee.company.address.copy(
        street = employee.company.address.street.copy(
          name = employee.company.address.street.name.capitalize // luckily capitalize exists
        )
      )
    )
  )

  // But by defining Lenses:
  val company: Lens[Employee, Company] = GenLens[Employee](_.company)
  val address: Lens[Company, Address]  = GenLens[Company](_.address)
  val street: Lens[Address, Street]    = GenLens[Address](_.street)
  val streetName: Lens[Street, String] = GenLens[Street](_.name)

  // And composing them
  val composedLenses
    : PLens[Employee, Employee, String, String] = company composeLens address composeLens street composeLens streetName

  // We can modify the street name more easily
  composedLenses.modify(_.capitalize)(employee)

  // Note before we defined lenses using a macro as they are simple (get/set same field) but they can be defined manually
  val companyManual: Lens[Employee, Company] =
    Lens[Employee, Company](_.company)(c => e => e.copy(company = c))

}
