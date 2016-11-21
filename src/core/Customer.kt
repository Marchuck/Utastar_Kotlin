package core

import tornadofx.getProperty
import tornadofx.property
/**
 * Utastar_Kotlin
 *
 * @author Lukasz Marczak
 *
 * @since 21 lis 2016.
 * 17 : 51
 */

class Customer {
    var name by property<String>()
    fun nameProperty() = getProperty(Customer::name)

    var birthday by property<String>()
    fun birthdayProperty() = getProperty(Customer::birthday)

    var street by property<String>()
    fun streetProperty() = getProperty(Customer::street)

    var zip by property<String>()
    fun zipProperty() = getProperty(Customer::zip)

    var city by property<String>()
    fun cityProperty() = getProperty(Customer::city)

    override fun toString() = name
}