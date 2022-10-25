package com.example.playgroundevotor.services

import com.example.playgroundevotor.Prefs
import ru.evotor.devices.commons.printer.printable.IPrintable
import ru.evotor.devices.commons.printer.printable.PrintableText
import ru.evotor.framework.core.IntegrationService
import ru.evotor.framework.core.action.event.receipt.changes.receipt.print_extra.SetPrintExtra
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEvent
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEventProcessor
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEventResult
import ru.evotor.framework.core.action.processor.ActionProcessor
import ru.evotor.framework.receipt.Receipt
import ru.evotor.framework.receipt.ReceiptApi
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupTop
import java.text.SimpleDateFormat
import java.util.*

class PrintExtraService : IntegrationService() {

    private val iso8601Formatter = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)

    override fun createProcessors(): MutableMap<String, ActionProcessor> {
        return mutableMapOf(
            PrintExtraRequiredEvent.NAME_SELL_RECEIPT to printExtraRequiredEventProcessor,
            PrintExtraRequiredEvent.NAME_PAYBACK_RECEIPT to printExtraRequiredEventProcessor,
            PrintExtraRequiredEvent.NAME_BUY_RECEIPT to printExtraRequiredEventProcessor,
            PrintExtraRequiredEvent.NAME_BUYBACK_RECEIPT to printExtraRequiredEventProcessor,
        )
    }

    private val printExtraRequiredEventProcessor = object : PrintExtraRequiredEventProcessor() {
        override fun call(
            action: String,
            event: PrintExtraRequiredEvent,
            callback: Callback
        ) {
            val context = this@PrintExtraService

            val message = StringBuilder()
            try {
                message.appendLine("\n----------------------------------")
                val date = iso8601Formatter.format(Date())
                message.appendLine("${date}  ${action} ")

                val receiptType = when (action) {
                    PrintExtraRequiredEvent.NAME_SELL_RECEIPT -> Receipt.Type.SELL
                    PrintExtraRequiredEvent.NAME_PAYBACK_RECEIPT -> Receipt.Type.PAYBACK
                    PrintExtraRequiredEvent.NAME_BUY_RECEIPT -> Receipt.Type.BUY
                    PrintExtraRequiredEvent.NAME_BUYBACK_RECEIPT -> Receipt.Type.BUYBACK
                    else -> {
                        callback.skip()
                        return
                    }
                }

                val receipt = requireNotNull(ReceiptApi.getReceipt(context, receiptType))
                message.appendLine("${date}    чек №${receipt.header.number}")
                message.appendLine("${date}    ${receipt.getPositions().joinToString { it.name }}")

                val printable = arrayOf<IPrintable>(PrintableText("^._.^"))
                val printExtra = SetPrintExtra(PrintExtraPlacePrintGroupTop(null), printable)
                appendLog(message)

                callback.onResult(PrintExtraRequiredEventResult(listOf(printExtra)).toBundle())
            } catch (e: Exception) {
                message.appendLine(e.localizedMessage)
                appendLog(message)
                callback.skip()
            }
        }

        fun appendLog(message: StringBuilder) {
            Prefs(applicationContext).logs = Prefs(applicationContext).logs + message
        }
    }
}