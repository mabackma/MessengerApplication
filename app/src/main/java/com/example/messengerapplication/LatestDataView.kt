package com.example.messengerapplication

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView

class LatestDataView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val maxRows: Int = 100

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

        // lasketaan kaikki yhteen ja asetetaan korkeus LinearLayoutissa
        var additionalHeight = this.measuredHeight + (maxRows * rowHeight)
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
        newTextView.setText(message)
        newTextView.setBackgroundColor(Color.BLACK)
        newTextView.setTextColor(Color.GREEN)
        newTextView.textSize = 20f
        this.addView(newTextView)

        // fade-in animaatio päälle
        val animation = AnimationUtils.loadAnimation(context, R.anim.customfade)
        // starting the animation
        newTextView.startAnimation(animation)
    }
}