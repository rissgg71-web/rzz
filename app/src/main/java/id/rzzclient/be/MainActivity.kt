
package id.rzzclient.be

import android.app.Activity
import android.os.Bundle
import android.graphics.*
import android.view.View
import android.view.Gravity
import android.widget.*
import android.graphics.drawable.GradientDrawable

class MainActivity : Activity() {

    private val bgColor = Color.rgb(10, 17, 32)
    private val cyan = Color.rgb(0, 210, 240)
    private val white = Color.WHITE
    private lateinit var ipInput: EditText
    private lateinit var portInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bgColor
        window.navigationBarColor = bgColor

        val root = FrameLayout(this)
        root.addView(SpaceBackground(this))

        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22.dp(), 20.dp(), 22.dp(), 28.dp())
        }

        scroll.addView(content)
        root.addView(scroll)
        setContentView(root)

        val prefs = getSharedPreferences("rz_settings", MODE_PRIVATE)

        // HEADER
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val logo = TextView(this).apply {
            text = "RZ"
            textSize = 27f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            background = rounded(cyan, 13)
            setPadding(12.dp(), 7.dp(), 12.dp(), 7.dp())
        }

        val brand = TextView(this).apply {
            text = "  RZ CLIENT BE"
            textSize = 19f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setTextColor(white)
        }

        header.addView(logo)
        header.addView(brand)
        content.addView(header)

        content.addView(space(30))

        addText(content, "EXPLORE THE UNIVERSE", 11f, cyan, true)
        content.addView(space(8))
        addText(content, "Your world.\nYour connection.", 31f, white, true)
        content.addView(space(8))
        addText(content, "PLAY BETTER • CONNECT FASTER", 11f,
            Color.LTGRAY, false)

        content.addView(space(28))

        // CONNECTION CARD
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(17.dp(), 18.dp(), 17.dp(), 18.dp())
            background = rounded(Color.rgb(22, 36, 57), 20)
        }

        addText(card, "QUICK CONNECT", 17f, white, true)
        card.addView(space(15))

        ipInput = EditText(this).apply {
            hint = "Server IP / Domain"
            setHintTextColor(Color.LTGRAY)
            setTextColor(white)
            textSize = 14f
            isSingleLine = true
            setPadding(14.dp(), 12.dp(), 14.dp(), 12.dp())
            background = rounded(Color.rgb(35, 53, 76), 12)
            setText(prefs.getString("ip", "") ?: "")
        }

        portInput = EditText(this).apply {
            hint = "Port"
            setHintTextColor(Color.LTGRAY)
            setTextColor(white)
            textSize = 14f
            isSingleLine = true
            inputType = 2
            setPadding(14.dp(), 12.dp(), 14.dp(), 12.dp())
            background = rounded(Color.rgb(35, 53, 76), 12)
            setText(prefs.getString("port", "19132") ?: "19132")
        }

        card.addView(ipInput)
        card.addView(space(10))
        card.addView(portInput)
        card.addView(space(15))

        val connect = makeButton("CONNECT", cyan)
        card.addView(connect)

        connect.setOnClickListener {
            val ip = ipInput.text.toString().trim()
            val port = portInput.text.toString().trim().toIntOrNull()

            if (ip.isEmpty() || port == null || port !in 1..65535) {
                Toast.makeText(this, "IP atau port tidak valid",
                    Toast.LENGTH_SHORT).show()
            } else {
                prefs.edit()
                    .putString("ip", ip)
                    .putString("port", port.toString())
                    .apply()

                Toast.makeText(this,
                    "Server tersimpan. Integrasi proxy berikutnya!",
                    Toast.LENGTH_LONG).show()
            }
        }

        content.addView(card)
        content.addView(space(27))

        // SERVER SECTION
        addText(content, "YOUR SERVERS", 18f, white, true)
        content.addView(space(12))

        addServerCard(content, "My Survival",
            "Your saved Minecraft server", "SAVED")

        content.addView(space(10))

        addServerCard(content, "Discover Servers",
            "Explore your favorite worlds", "EXPLORE")

        content.addView(space(25))

        addText(content, "ABOUT RZ CLIENT", 16f, cyan, true)
        content.addView(space(8))
        addText(content,
            "A lightweight companion for Minecraft Bedrock. " +
            "Built for your own server experience.",
            13f, Color.LTGRAY, false)

        content.addView(space(25))

        addText(content, "RZ CLIENT BE  •  VERSION 0.2.0",
            11f, Color.GRAY, true)
    }

    private fun addServerCard(
        parent: LinearLayout,
        title: String,
        subtitle: String,
        tag: String
    ) {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16.dp(), 15.dp(), 16.dp(), 15.dp())
            background = rounded(Color.rgb(20, 35, 53), 16)
        }

        val row = LinearLayout(this).apply {
            gravity = Gravity.CENTER_VERTICAL
        }

        val icon = TextView(this).apply {
            text = "◈"
            textSize = 27f
            setTextColor(cyan)
        }

        val details = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val name = TextView(this).apply {
            text = title
            textSize = 15f
            setTextColor(white)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }

        val desc = TextView(this).apply {
            text = subtitle
            textSize = 11f
            setTextColor(Color.LTGRAY)
        }

        details.addView(name)
        details.addView(desc)

        val badge = TextView(this).apply {
            text = tag
            textSize = 9f
            setTextColor(cyan)
            setPadding(8.dp(), 5.dp(), 8.dp(), 5.dp())
            background = rounded(Color.rgb(30, 65, 83), 8)
        }

        row.addView(icon)
        row.addView(space(14))
        row.addView(details, LinearLayout.LayoutParams(0, -2, 1f))
        row.addView(badge)

        box.addView(row)
        parent.addView(box)
    }

    private fun addText(
        parent: LinearLayout,
        value: String,
        size: Float,
        color: Int,
        bold: Boolean
    ) {
        val text = TextView(this).apply {
            this.text = value
            textSize = size
            setTextColor(color)
            if (bold) {
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
        }
        parent.addView(text)
    }

    private fun makeButton(label: String, color: Int): TextView {
        return TextView(this).apply {
            text = label
            textSize = 14f
            gravity = Gravity.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setTextColor(Color.BLACK)
            setPadding(15.dp(), 14.dp(), 15.dp(), 14.dp())
            background = rounded(color, 12)
        }
    }

    private fun rounded(color: Int, radius: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius.dp().toFloat()
        }
    }

    private fun space(height: Int): View {
        return View(this).apply {
            layoutParams = LinearLayout.LayoutParams(1, height.dp())
        }
    }

    private fun Int.dp(): Int =
        (this * resources.displayMetrics.density).toInt()

    // SPACE BACKGROUND
    class SpaceBackground(context: android.content.Context) : View(context) {

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        private val stars = List(140) { i ->
            val random = java.util.Random(i.toLong() + 123)
            Triple(
                random.nextFloat(),
                random.nextFloat(),
                random.nextFloat() * 2.5f + 0.5f
            )
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val w = width.toFloat()
            val h = height.toFloat()

            paint.shader = LinearGradient(
                0f, 0f, w, h,
                intArrayOf(
                    Color.rgb(8, 15, 32),
                    Color.rgb(14, 28, 54),
                    Color.rgb(5, 12, 26)
                ),
                null,
                Shader.TileMode.CLAMP
            )

            canvas.drawRect(0f, 0f, w, h, paint)
            paint.shader = null

            // PLANET
            paint.color = Color.rgb(27, 63, 102)
            canvas.drawCircle(w * 0.83f, h * 0.20f,
                w * 0.19f, paint)

            paint.color = Color.rgb(36, 85, 125)
            canvas.drawCircle(w * 0.79f, h * 0.18f,
                w * 0.16f, paint)

            // PLANET RING
            paint.color = Color.argb(120, 0, 210, 240)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 3.dp().toFloat()

            canvas.save()
            canvas.rotate(-22f, w * 0.83f, h * 0.20f)
            canvas.drawOval(
                w * 0.58f, h * 0.15f,
                w * 1.08f, h * 0.26f, paint
            )
            canvas.restore()

            paint.style = Paint.Style.FILL

            // STARS
            for ((x, y, size) in stars) {
                paint.color = Color.argb(
                    100 + (size * 55).toInt().coerceAtMost(155),
                    210, 235, 255
                )

                canvas.drawCircle(x * w, y * h, size, paint)
            }
        }

        private fun Int.dp(): Int =
            (this * resources.displayMetrics.density).toInt()
    }
}
