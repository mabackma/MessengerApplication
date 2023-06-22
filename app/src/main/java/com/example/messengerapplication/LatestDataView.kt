package com.example.messengerapplication

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.text.util.Linkify
import android.util.AttributeSet
import android.util.Log
import java.util.regex.Pattern
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class LatestDataView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val maxRows: Int = 10000

    // alkuvaiheessa tämä riittää kun käytetään compound controlia
    // huom: onDraw ja onMeasure ym. ovat jo valmiiksi tehty LinearLayoutissa
    init {
        // vaihdetaan LinearLayoutin orientaation pystysuuntaiseksi,
        // jotta TextViewit rakentuvat allekkain
        this.orientation = VERTICAL

        // idea:
        // tehdään koodin muistiin uusi TextView (jota ei käytetä missään),
        // ja otetaan siitä yhden TextViewin korkeus näytöllä
        // kerrotaan tämä luku maxRows-muuttujalla (eli 5)
        // lisätään päälle mahdolliset LinearLayouting omat lisäkorkeudet (padding ym.)
        // lasketaan kaikki yhteen => tarvittava korkeus LinearLayoutille

        // tehdään uusi TextView muistiin, ja käsketään Androidia mittaamaan se tällä näytöllä
        var someTextView : TextView = TextView(context)
        someTextView.measure(0,0)
        var rowHeight = someTextView.measuredHeight

        // mitataan myös itse LinearLayout (paddingit ym.)
        this.measure(0, 0)

        // Calculate the desired height based on the number of rows
        val desiredHeight = 20 * rowHeight

        // Set the height of the LinearLayout
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            desiredHeight
        )
        this.layoutParams = layoutParams

        // lasketaan kaikki yhteen ja asetetaan korkeus LinearLayoutissa
        var additionalHeight = this.measuredHeight + (20 * rowHeight)
        this.minimumHeight = additionalHeight
    }

    // apufunktio/metodi, joka lisää LinearLayoutiin uuden TextViewin lennosta
    fun addData(message : String)
    {
        // ennen kuin lisätään uusi TextView, huolehditaan siitä
        // että LinearLayoutissa ei ole ylimääräisiä TextViewejä (max 5 kpl)
        // niin kauan kuin lukumäärä on liian suuri -> poista vanhin TextView
        while(this.childCount >= maxRows) {
            this.removeViewAt(0)
        }

        var newTextView : TextView = TextView(context) as TextView

        if(extractUrl(message) != null) {
            val clickableUrl = extractUrl(message)
            val clickableUrlSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    // Handle the click event for the URL
                    // For example, open the URL in a browser
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(clickableUrl))
                    context.startActivity(intent)
                }
            }

            val spannableString = SpannableString(message)
            val startIndex = message.indexOf(clickableUrl!!)
            spannableString.setSpan(clickableUrlSpan, startIndex, startIndex + clickableUrl.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            newTextView.setText(spannableString)
            newTextView.movementMethod = LinkMovementMethod.getInstance()
            newTextView.setBackgroundColor(Color.BLACK)
            newTextView.setTextColor(Color.GREEN)
        }
        else {
            newTextView.setText(message)
            newTextView.setBackgroundColor(Color.BLACK)
            newTextView.setTextColor(Color.GREEN)
        }

        newTextView.textSize = 20f
        this.addView(newTextView)
        // fade-in animaatio päälle
        val animation = AnimationUtils.loadAnimation(context, R.anim.customfade)
        // starting the animation
        newTextView.startAnimation(animation)

        scrollToBottom()
    }

    fun scrollToBottom() {
        post { // Ensure the scrolling is done after the view is laid out
            val scrollView = parent as? ScrollView
            scrollView?.fullScroll(View.FOCUS_DOWN)
        }
    }

    // Poistaa kaikki tekstikentät
    fun clearData() {
        removeAllViews()
    }

    // Extracts the url from the message
    fun extractUrl(str: String): String? {
        val segments = str.split("\\s+".toRegex()) // Split the string into segments by whitespace

        for (segment in segments) {
            val urlPattern = Pattern.compile("((http|https)://)(www.)?" +
                    "[a-zA-Z0-9@:%._\\+~#?&//=]{2,256}\\.[a-z]" +
                    "{2,6}\\b([-a-zA-Z0-9@:%._\\+~#?&//=]*)"
            )

            val matcher = urlPattern.matcher(segment)
            if (matcher.matches()) {
                return segment // Return the segment containing the valid URL
            }
        }

        return null // No valid URL found
    }

}