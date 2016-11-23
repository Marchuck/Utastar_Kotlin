package core

/**
 * Utastar_Kotlin
 *
 * @author Lukasz Marczak
 *
 * @since 21 lis 2016.
 * 18 : 35
 */
class UtaException : Exception {

    constructor(s: String) {
        Exception(s)
    }
}