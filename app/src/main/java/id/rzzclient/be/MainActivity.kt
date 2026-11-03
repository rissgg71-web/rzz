package id.rzzclient.be

import android.app.Activity
import android.os.Bundle
import android.graphics.Typeface
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val title = TextView(this).apply {
            text = "RZ Client BE"
            textSize = 26f
            typeface = Typeface.DEFAULT_BOLD
        }
        val body = TextView(this).apply {
            text = "Build 0.1.0\\n\\nCompanion app untuk proxy Minecraft Bedrock.\\n\\nProxy Node.js dijalankan terpisah di server/VPS. APK ini belum mengubah HUD atau file game Minecraft.\\n\\nTarget pengembangan: Bedrock 1.26.60+ (perlu pengujian protokol tiap versi)."
            textSize = 15f
            setLineSpacing(6f, 1f)
        }
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(28, 28, 28, 28)
            addView(title)
            addView(body)
        }
        setContentView(layout)
    }
}
