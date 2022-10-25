package com.example.playgroundevotor;

import android.os.RemoteException;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.evotor.devices.commons.printer.printable.IPrintable;
import ru.evotor.devices.commons.printer.printable.PrintableText;
import ru.evotor.framework.core.IntegrationService;
import ru.evotor.framework.core.action.event.receipt.changes.receipt.print_extra.SetPrintExtra;
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEvent;
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEventProcessor;
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEventResult;
import ru.evotor.framework.core.action.processor.ActionProcessor;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupHeader;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupSummary;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupTop;

public class MyPrintService extends IntegrationService {

    private static final String TAG = "MyApp123";

    @Nullable
    @Override
    protected Map<String, ActionProcessor> createProcessors() {
        Map<String, ActionProcessor> map = new HashMap<>();
        map.put(
                PrintExtraRequiredEvent.NAME_BUYBACK_RECEIPT,
                new PrintExtraRequiredEventProcessor() {
                    @Override
                    public void call(@NotNull String s, @NotNull PrintExtraRequiredEvent printExtraRequiredEvent, @NotNull Callback callback) {
                        List<SetPrintExtra> setPrintExtras = new ArrayList<>();
                        Receipt MyReceipt124 = ReceiptApi.getReceipt(MyPrintService.this, Receipt.Type.BUYBACK);
                        Log.d(TAG, "Номер чека: " + MyReceipt124.getHeader().getNumber());
                        appendLog("Номер чека: " + MyReceipt124.getHeader().getNumber());

                        setPrintExtras.add(new SetPrintExtra(
                                //Метод, который указывает место, где будут распечатаны данные.
                                //Данные печатаются после клише и до текста “Кассовый чек”
                                new PrintExtraPlacePrintGroupTop(null),
                                //Массив данных, которые требуется распечатать.
                                new IPrintable[]{
                                        //Простой текст
                                        new PrintableText("Proin eget tortor risus. Nulla quis lorem ut libero malesuada feugiat. Proin eget tortor risus."),
                                        //Штрихкод с контрольной суммой если она требуется для выбранного типа штрихкода
                                        // new PrintableBarcode("4750232005910", PrintableBarcode.BarcodeType.EAN13),
                                        //Изображение
                                        // new PrintableImage(getBitmapFromAsset("ic_launcher.png"))
                                }
                        ));
                        setPrintExtras.add(new SetPrintExtra(
                                //Данные печатаются после текста “Кассовый чек”, до имени пользователя
                                new PrintExtraPlacePrintGroupHeader(null),
                                new IPrintable[]{
                                        //new PrintableBarcode("4750232005910", PrintableBarcode.BarcodeType.EAN13),
                                        new PrintableText("Proin eget tortor risus. Nulla quis lorem ut libero malesuada feugiat. Proin eget tortor risus.")
                                }
                        ));
                        //Добавляем к каждой позиции чека продажи необходимые данные
                        Receipt r = ReceiptApi.getReceipt(MyPrintService.this, Receipt.Type.BUYBACK);
                        if (r != null) {
                            setPrintExtras.add(new SetPrintExtra(
                                    new PrintExtraPlacePrintGroupSummary(null),
                                    new IPrintable[]{
                                            new PrintableText("ДАННЫЕ ВНУТРИ ЧЕКА")
                                    }
                            ));
                        }

                        try {
                            callback.onResult(new PrintExtraRequiredEventResult(setPrintExtras).toBundle());
                        } catch (RemoteException exc) {
                            exc.printStackTrace();
                        }
                    }
                }
        );
        return map;
    }

    private void appendLog(String str){
        Prefs prefs = new Prefs(getApplicationContext());
        prefs.setLogs(prefs.getLogs() +"\n\n<-----------Evotor>----------->\n"+str );
    }
}
