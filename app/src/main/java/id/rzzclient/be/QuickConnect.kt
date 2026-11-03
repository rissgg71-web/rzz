package id.rzzclient.be

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast

object QuickConnect {

    fun connect(context: Context, address: String, port: String) {
        val host = address.trim()
        val portNumber = port.toIntOrNull()

        if (host.isEmpty() || portNumber == null ||
            portNumber !in 1..65535
        ) {
            Toast.makeText(
                context,
                "IP/domain atau port tidak valid",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val server = "$host:$portNumber"

        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE)
                as ClipboardManager

        clipboard.setPrimaryClip(
            ClipData.newPlainText("Minecraft Server", server)
        )

        val intent = context.packageManager
            .getLaunchIntentForPackage("com.mojang.minecraftpe")

        if (intent == null) {
            Toast.makeText(
                context,
                "Minecraft Bedrock belum terinstal",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)

        Toast.makeText(
            context,
            "Server $server disalin! Tambahkan di Minecraft.",
            Toast.LENGTH_LONG
        ).show()
    }
}
