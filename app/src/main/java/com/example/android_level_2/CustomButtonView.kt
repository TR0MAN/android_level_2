package com.example.android_level_2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.drawable.toBitmap

class CustomButtonView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : View(context, attrs, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            this( context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    // атрибуты для текста кнопки
    private var buttonText: String? = null
    private var buttonTextColor: Int? = null
    private var buttonTextSize: Float? = null
    private var buttonTextStyle: Int? = null

    // атрибуты для кнопки
    private var buttonBackgroundColor: Int? = null
    private var buttonBorderLineColor: Int? = null
    private var buttonBorderLineWidth: Float? = null
    private var buttonCornerRadius: Float? = null

    // атрибуты иконки для кнопки
    private var buttonImage: Drawable? = null
    private var buttonImageHeight: Float? = null
    private var distanceToText: Float? = null

    // набор кистей для рисования (кнопки, текста, изображения)
    private var textPaint = Paint()
    private var buttonBackgroundPaint = Paint()
    private var buttonStrokePaint = Paint()

    // вспомогательные глобальные переменные
    private var centerX = 0f
    private var centerY = 0f
    private val textBoundRect = Rect()
    private var textWidth = 0
    private var textHeight = 0
    private var allInnerContentWidth: Float? = null
    private val placeForImage = Rect()
    private val imageBody = Rect()

    init {
        getViewAttributes(attrs, defStyleAttr, defStyleRes)
        initPaints()
    }

    private fun initPaints() {
        buttonBackgroundPaint.style = Paint.Style.FILL
        buttonBackgroundPaint.color = buttonBackgroundColor!!
        buttonBackgroundPaint.isAntiAlias = true

        buttonStrokePaint.style = Paint.Style.FILL
        buttonStrokePaint.color = buttonBorderLineColor!!
        buttonStrokePaint.isAntiAlias = true

        textPaint.style = Paint.Style.STROKE
        textPaint.color = buttonTextColor!!
        textPaint.textSize = buttonTextSize!!
        when (buttonTextStyle) {
            0 -> textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))
            1 -> textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
            2 -> textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC))
            3 -> textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC))
        }
        textPaint.isAntiAlias = true

    }

    private fun getViewAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return

        val attributes = context.obtainStyledAttributes(
            attrs,
            R.styleable.CustomButtonView,
            defStyleAttr,
            defStyleRes
        )
        buttonText =
            attributes.getString(R.styleable.CustomButtonView_buttonText) ?: "Custom Button"
        buttonTextColor =
            attributes.getColor(R.styleable.CustomButtonView_buttonTextColor, Color.BLACK)
        buttonTextSize = attributes.getDimension(R.styleable.CustomButtonView_buttonTextSize, 40f)
        buttonBackgroundColor =
            attributes.getColor(R.styleable.CustomButtonView_buttonBackgroundColor, Color.GRAY)
        buttonBorderLineColor =
            attributes.getColor(R.styleable.CustomButtonView_buttonBorderLineColor, Color.GRAY)
        buttonBorderLineWidth =
            attributes.getDimension(R.styleable.CustomButtonView_buttonBorderLineWidth, 0f)
        buttonCornerRadius =
            attributes.getDimension(R.styleable.CustomButtonView_buttonCornerRadius, 0f)
        buttonImage = attributes.getDrawable(R.styleable.CustomButtonView_buttonImage)
        buttonImageHeight =
            attributes.getDimension(R.styleable.CustomButtonView_buttonImageHeight, 40f)
        distanceToText =
            attributes.getDimension(R.styleable.CustomButtonView_buttonDistanceToText, 10f)
        buttonTextStyle = attributes.getInt(R.styleable.CustomButtonView_buttonTextStyle, 0)
        attributes.recycle()
    }

    // с моей реализацией и без переопределения вообще, работает одинаково
    // в параметры приходит значение Width и Height уже с учтенным (отминусованным) margin из XML-файла
    // результат вычисления приходит в метод onDraw как getWidth и getHeight
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minimumWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val minimumHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        setMeasuredDimension(
            resolveSize(minimumWidth, widthMeasureSpec),
            resolveSize(minimumHeight, heightMeasureSpec)
        )
    }

    // метод рисования на "холсте"
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        centerX = width / 2f
        centerY = height / 2f

        // получаем размер прямоугольника с текстом, узнаем ширину и высоту текста
        textPaint.getTextBounds(buttonText, 0, buttonText!!.length, textBoundRect)
        textWidth = textPaint.measureText(buttonText).toInt()
        textHeight = textBoundRect.height()

        drawButtonStroke(canvas)
        drawButtonBackground(canvas)
        drawButtonIcon(canvas)
        drawButtonText(canvas)
    }

    // отрисовываем текст
    private fun drawButtonText(canvas: Canvas) {

        // если иконки у кнопки нет, то выводим текст без учета смещения на картинку
        if (allInnerContentWidth == null) {
            canvas.drawText(
                buttonText!!,
                centerX - textWidth / 2,
                centerY + textHeight / 2.1f,
                textPaint
            )
        } else {
            canvas.drawText(
                buttonText!!,
                centerX - allInnerContentWidth!! / 2 + buttonImageHeight!! + distanceToText!!,
                centerY + textHeight / 2.1f,
                textPaint
            )
        }
    }

    // отрисовываем картинку поверх фона прямоугольника
    private fun drawButtonIcon(canvas: Canvas) {
        // если картинки нет, то вообще ничего не рассчитываем и не делаем
        buttonImage?.let { icon ->
            val bitmap = icon.toBitmap(
                icon.minimumWidth,
                icon.minimumHeight,
                Bitmap.Config.ARGB_8888
            )
            // узнаем максимальную высоту кнопки
            val maxImageHeight =
                (height - paddingTop - paddingBottom - buttonBorderLineWidth!!) * 0.85f

            // узнаем размер содержимого внтури кнопки (текст + ширина картинки + отступ между ними)
            allInnerContentWidth = textWidth + buttonImageHeight!! + distanceToText!!

            // корректируем размер картинки, если установленная высота картинки больше чем
            // высота свободного места внутри кнопки
            val buttonIcon = if (buttonImageHeight!! > maxImageHeight)
                maxImageHeight.toInt()
            else
                buttonImageHeight!!.toInt()

            // берем все 100% картинки
            imageBody.apply {
                left = 0
                top = 0
                right = bitmap.width
                bottom = bitmap.height
            }

            // указываем координаты места куда будет вписываться картинка
            placeForImage.apply {
                left = (centerX - allInnerContentWidth!! / 2).toInt()
                top = (centerY - buttonIcon / 2).toInt()
                right = ((centerX - allInnerContentWidth!! / 2) + buttonIcon).toInt()
                bottom = (centerY + buttonIcon / 2).toInt()
            }
            canvas.drawBitmap(bitmap, imageBody, placeForImage, null)
        }
    }

    // отрисовываем фон кнопки
    private fun drawButtonBackground(canvas: Canvas) {
        canvas.drawRoundRect(
            (centerX - width / 2f) + paddingLeft + buttonBorderLineWidth!!,
            (centerY - height / 2f) + paddingTop + buttonBorderLineWidth!!,
            (centerX + width / 2f) - paddingRight - buttonBorderLineWidth!!,
            (centerY + height / 2f) - paddingBottom - buttonBorderLineWidth!!,
            buttonCornerRadius!!,
            buttonCornerRadius!!,
            buttonBackgroundPaint
        )
    }

    // отрисовываем обводку кнопки. если цвет по умолчанию, то ставим цвет как у фона кнопки
    private fun drawButtonStroke(canvas: Canvas) {
        if (buttonStrokePaint.color == Color.GRAY) {
            buttonStrokePaint.color = buttonBackgroundColor!!
        }
        canvas.drawRoundRect(
            (centerX - width / 2f) + paddingLeft,
            (centerY - height / 2f) + paddingTop,
            (centerX + width / 2f) - paddingRight,
            (centerY + height / 2f) - paddingBottom,
            buttonCornerRadius!!,
            buttonCornerRadius!!,
            buttonStrokePaint
        )
    }

    // реализация реакции на нажатие на кнопку
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                buttonBackgroundPaint.color = Color.LTGRAY
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                buttonBackgroundPaint.color = buttonBackgroundColor!!
                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }
}

// значек G
// 20х20 px     отступ справа 16px

// GOOGLE
// размер кнопки 328х40 px      отступ слева 14px

// текст 74x24 px
// цвет #18181F     размер 16px