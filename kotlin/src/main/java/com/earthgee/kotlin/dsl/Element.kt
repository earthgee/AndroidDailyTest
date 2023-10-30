package com.earthgee.kotlin.dsl

import android.text.Html

/**
 *  Created by zhaoruixuan1 on 2023/9/21
 *  test
 *  功能：
 */
interface Element {

    fun render(builder: StringBuilder, intent: String)

}

class TextElement(val text: String) : Element {

    override fun render(builder: StringBuilder, intent: String) {
        builder.append("$intent$text\n")
    }

}

abstract class Tag(val name: String) : Element {

    val children = arrayListOf<Element>()
    val attributes = hashMapOf<String, String>()

    protected fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    override fun render(builder: StringBuilder, intent: String) {
        builder.append("$intent<$name${renderAttributes()}>\n")
        children.forEach {
            it.render(builder, intent + "  ")
        }
        builder.append("$intent</$name>\n")
    }

    private fun renderAttributes(): String {
        val builder = StringBuilder()
        for ((attr, value) in attributes) {
            builder.append(" $attr=\"$value\"")
        }
        return builder.toString()
    }

}

abstract class TagWithText(name: String) : Tag(name) {

    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }

}

class Title : TagWithText("title")

class Head : TagWithText("head") {

    fun title(init: Title.() -> Unit) = initTag(Title(), init)

}

class Body : TagWithText("body")

class HTML : TagWithText("html") {

    fun head(init: Head.() -> Unit) = initTag(Head(), init)

    fun body(init: Body.() -> Unit) = initTag(Body(), init)

}

fun htmlDsl(init: HTML.() -> Unit): HTML {
    val html = HTML()
    html.init()
    return html
}

