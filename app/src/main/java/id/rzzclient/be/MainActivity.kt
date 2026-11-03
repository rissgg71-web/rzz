
package id.rzzclient.be

import android.app.Activity
import android.os.Bundle
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.RectF
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.*
import android.text.InputType
import android.graphics.drawable.GradientDrawable
import kotlin.math.sin

class MainActivity : Activity() {
    private val prefs by lazy {
        getSharedPreferences("rz_client", Context.MODE_PRIVATE)
    }

    private lateinit var content: LinearLayout
    private lateinit var favoritesContainer: LinearLayout
    private lateinit var recentContainer: LinearLayout

    data class Server(
        val name: String,
        val address: String,
        val port: Int,
        val category: String,
        val description: String
    )

    private val featured = listOf(
        Server("Survival Universe", "play.example.net", 19132, "SURVIVAL", "Explore and survive"),
        Server("PvP Galaxy", "pvp.example.net", 19132, "PVP", "Battle in the arena"),
        Server("Creative Nebula", "creative.example.net", 19132, "CREATIVE", "Build your universe")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildScreen()
    }

    private fun buildScreen() {
        val root = FrameLayout(this)
        root.addView(SpaceBackground(this))

        val scroll = ScrollView(this).apply {
            clipToPadding = false
            setPadding(dp(18), dp(18), dp(18), dp(28))
        }

        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        scroll.addView(content)
        root.addView(scroll)
        setContentView(root)

        addHeader()
        addHero()
        addQuickConnect()
        addFeatured()
        addFavorites()
        addRecent()
        addFooter()
        refreshSavedSections()
    }

    private fun addHeader() {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        row.addView(text("◉ RZ CLIENT", 21f, Color.WHITE, true))

        val version = text("BE • 0.2.1", 10f, 0xFF59D5FF.toInt(), true)
        version.setPadding(dp(10), dp(6), dp(10), dp(6))
        version.background = rounded(0xFF14435E.toInt(), 30)
        row.addView(version)

        content.addView(row)
    }

    private fun addHero() {
        val title = text("EXPLORE BEYOND", 29f, Color.WHITE, true)
        title.setPadding(0, dp(30), 0, 0)
        content.addView(title)

        content.addView(
            text("Your Minecraft Bedrock gateway", 13f, 0xFF9BB6D3.toInt())
        )

        val subtitle = text(
            "CONNECT TO YOUR NEXT WORLD",
            10f,
            0xFF56D7FF.toInt(),
            true
        )
        subtitle.setPadding(0, dp(8), 0, dp(17))
        content.addView(subtitle)
    }

    private fun addQuickConnect() {
        val panel = panel()
        panel.addView(sectionTitle("QUICK CONNECT"))

        val ip = EditText(this).apply {
            hint = "Server IP / Address"
            isSingleLine = true
            textSize = 14f
            setTextColor(Color.WHITE)
            setHintTextColor(0xFF7894B3.toInt())
            setPadding(dp(13), 0, dp(13), 0)
            background = rounded(0xFF0A1930.toInt(), 12)
        }

        val port = EditText(this).apply {
            hint = "Port"
            isSingleLine = true
            inputType = InputType.TYPE_CLASS_NUMBER
            setText("19132")
            textSize = 14f
            setTextColor(Color.WHITE)
            setHintTextColor(0xFF7894B3.toInt())
            setPadding(dp(13), 0, dp(13), 0)
            background = rounded(0xFF0A1930.toInt(), 12)
        }

        panel.addView(ip, LinearLayout.LayoutParams(-1, dp(48)).apply {
            bottomMargin = dp(9)
        })

        panel.addView(port, LinearLayout.LayoutParams(-1, dp(48)).apply {
            bottomMargin = dp(12)
        })

        panel.addView(actionButton("CONNECT") {
            val address = ip.text.toString().trim()
            val portNumber = port.text.toString().toIntOrNull()

            when {
                address.isBlank() -> toast("Masukkan IP server dulu")
                portNumber == null || portNumber !in 1..65535 ->
                    toast("Port harus 1 sampai 65535")
                else -> {
                    prefs.edit()
                        .putString("last_ip", address)
                        .putInt("last_port", portNumber)
                        .apply()

                    saveRecent(address, portNumber)
                    refreshSavedSections()
                    toast("Alamat berhasil disimpan")
                }
            }
        })

        content.addView(panel)
    }

    private fun addFeatured() {
        val heading = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(24), 0, dp(10))
        }

        heading.addView(
            sectionTitle("FEATURED SERVERS"),
            LinearLayout.LayoutParams(0, -2, 1f)
        )

        heading.addView(text("SWIPE →", 10f, 0xFF7FA5C9.toInt()))
        content.addView(heading)

        val horizontal = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            clipToPadding = false
        }

        val cards = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        featured.forEach { server ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(14), dp(13), dp(14), dp(13))
                background = rounded(0xE8173452.toInt(), 18)
            }

            val planet = text("✦   ◉   ✦", 25f, 0xFF64DFFF.toInt())
            planet.gravity = Gravity.CENTER
            planet.background = rounded(0xFF102944.toInt(), 13)

            card.addView(planet, LinearLayout.LayoutParams(-1, dp(76)))

            card.addView(text(server.category, 9f, 0xFF58D9FF.toInt(), true))
            card.addView(text(server.name, 16f, Color.WHITE, true))
            card.addView(text(server.description, 11f, 0xFFAAC1D9.toInt()))

            val buttons = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, dp(12), 0, 0)
            }

            buttons.addView(smallButton("USE") {
                prefs.edit()
                    .putString("last_ip", server.address)
                    .putInt("last_port", server.port)
                    .apply()

                toast("Alamat contoh dimasukkan")
            }, LinearLayout.LayoutParams(0, dp(36), 1f))

            buttons.addView(
                smallButton(if (isFavorite(server)) "★" else "☆") {
                    toggleFavorite(server)
                    buildScreen()
                },
                LinearLayout.LayoutParams(dp(44), dp(36)).apply {
                    leftMargin = dp(7)
                }
            )

            card.addView(buttons)

            cards.addView(
                card,
                LinearLayout.LayoutParams(dp(235), -2).apply {
                    rightMargin = dp(11)
                }
            )
        }

        horizontal.addView(cards)
        content.addView(horizontal)
        content.addView(
            text(
                "Alamat server di atas masih contoh, belum diverifikasi.",
                10f,
                0xFF829DBA.toInt()
            )
        )
    }

    private fun addFavorites() {
        val heading = sectionTitle("★ FAVORITES")
        heading.setPadding(0, dp(23), 0, dp(8))
        content.addView(heading)

        favoritesContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        content.addView(favoritesContainer)
    }

    private fun addRecent() {
        val heading = sectionTitle("RECENT SERVERS")
        heading.setPadding(0, dp(20), 0, dp(8))
        content.addView(heading)

        recentContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        content.addView(recentContainer)
    }

    private fun addFooter() {
        val footer = text(
            "RZ CLIENT BE • BUILT FOR YOUR NEXT ADVENTURE",
            9f,
            0xFF6885A5.toInt(),
            true
        )
        footer.gravity = Gravity.CENTER
        footer.setPadding(0, dp(28), 0, dp(8))
        content.addView(footer)
    }

    private fun refreshSavedSections() {
        if (!::favoritesContainer.isInitialized ||
            !::recentContainer.isInitialized
        ) return

        favoritesContainer.removeAllViews()

        val favorites = prefs.getStringSet(
            "favorites",
            emptySet()
        )?.toList().orEmpty()

        if (favorites.isEmpty()) {
            favoritesContainer.addView(
                text("Belum ada favorit. Tekan ☆ pada kartu server.", 12f, 0xFF8FA9C5.toInt())
            )
        }

        favorites.forEach {
            favoritesContainer.addView(savedRow(it, true))
        }

        recentContainer.removeAllViews()

        val recent = prefs.getString("recent", "")
            .orEmpty()
            .split("|")
            .filter { it.isNotBlank() }

        if (recent.isEmpty()) {
            recentContainer.addView(
                text("Server yang kamu masukkan akan muncul di sini.", 12f, 0xFF8FA9C5.toInt())
            )
        }

        recent.forEach {
            recentContainer.addView(savedRow(it, false))
        }
    }

    private fun savedRow(value: String, favorite: Boolean): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(10), dp(12), dp(10))
            background = rounded(0xC8152B45.toInt(), 12)
        }

        row.addView(
            text(if (favorite) "★" else "◷", 18f, 0xFF62DFFF.toInt())
        )

        row.addView(
            text(value, 12f, Color.WHITE),
            LinearLayout.LayoutParams(0, -2, 1f).apply {
                leftMargin = dp(9)
            }
        )

        row.addView(smallButton("USE") {
            val parts = value.split(":")
            if (parts.size == 2) {
                prefs.edit()
                    .putString("last_ip", parts[0])
                    .putInt("last_port", parts[1].toIntOrNull() ?: 19132)
                    .apply()

                toast("Alamat dimasukkan")
            }
        }, LinearLayout.LayoutParams(dp(65), dp(34)))

        return row.apply {
            layoutParams = LinearLayout.LayoutParams(-1, -2).apply {
                bottomMargin = dp(7)
            }
        }
    }

    private fun saveRecent(ip: String, port: Int) {
        val value = "$ip:$port"

        val old = prefs.getString("recent", "")
            .orEmpty()
            .split("|")
            .filter { it.isNotBlank() }

        val updated = (listOf(value) + old.filterNot { it == value }).take(5)

        prefs.edit()
            .putString("recent", updated.joinToString("|"))
            .apply()
    }

    private fun isFavorite(server: Server): Boolean {
        return prefs.getStringSet("favorites", emptySet())
            ?.contains("${server.address}:${server.port}") == true
    }

    private fun toggleFavorite(server: Server) {
        val set = prefs.getStringSet(
            "favorites",
            emptySet()
        )?.toMutableSet() ?: mutableSetOf()

        val key = "${server.address}:${server.port}"

        if (!set.add(key)) set.remove(key)

        prefs.edit().putStringSet("favorites", set).apply()
    }

    private fun panel() = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(15), dp(15), dp(15), dp(15))
        background = rounded(0xE8172A43.toInt(), 18)
    }

    private fun sectionTitle(value: String) =
        text(value, 12f, 0xFFBCEBFF.toInt(), true)

    private fun text(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ) = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        if (bold) {
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
        }
    }

    private fun actionButton(
        label: String,
        action: () -> Unit
    ) = Button(this).apply {
        text = label
        textSize = 13f
        isAllCaps = false
        setTextColor(Color.WHITE)
        background = rounded(0xFF079CCB.toInt(), 13)
        setOnClickListener { action() }
    }

    private fun smallButton(
        label: String,
        action: () -> Unit
    ) = Button(this).apply {
        text = label
        textSize = 10f
        isAllCaps = false
        setTextColor(Color.WHITE)
        background = rounded(0xFF12618A.toInt(), 10)
        setPadding(dp(4), 0, dp(4), 0)
        setOnClickListener { action() }
    }

    private fun rounded(color: Int, radius: Int) =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
            setStroke(dp(1), 0x553C9BC5)
        }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private inner class SpaceBackground(context: Context) : View(context) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        private val stars = List(75) { i ->
            Triple(
                ((i * 73) % 997) / 997f,
                ((i * 137) % 991) / 991f,
                1f + (i % 3)
            )
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            paint.shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                intArrayOf(
                    0xFF050B1B.toInt(),
                    0xFF0A1830.toInt(),
                    0xFF080D20.toInt()
                ),
                null,
                Shader.TileMode.CLAMP
            )

            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            paint.shader = null

            stars.forEachIndexed { index, star ->
                val alpha = (
                    130 + 100 * sin(
                        (System.currentTimeMillis() / 700.0) + index
                    ).toFloat()
                ).toInt().coerceIn(30, 230)

                paint.color = Color.argb(alpha, 180, 225, 255)
                canvas.drawCircle(
                    star.first * width,
                    star.second * height,
                    star.third,
                    paint
                )
            }

            val cx = width * 0.84f
            val cy = height * 0.18f

            paint.color = 0x3347BCEB
            canvas.drawCircle(cx, cy, dp(75).toFloat(), paint)

            paint.color = 0xFF183C65.toInt()
            canvas.drawCircle(cx, cy, dp(54).toFloat(), paint)

            paint.color = 0xFF24618A.toInt()
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = dp(3).toFloat()

            canvas.drawArc(
                RectF(
                    cx - dp(70), cy - dp(19),
                    cx + dp(70), cy + dp(19)
                ),
                190f, 160f, false, paint
            )

            paint.style = Paint.Style.FILL
            postInvalidateDelayed(900)
        }
    }
}
