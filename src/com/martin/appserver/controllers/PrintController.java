package com.martin.appserver.controllers;

import com.martin.appserver.routing.Controller;
import com.martin.appserver.routing.Get;
import com.martin.appserver.routing.Post;
import com.martin.appserver.services.PrinterService;
import com.martin.appserver.utils.PrintFormatter;
import com.martin.appserver.utils.JsonResponse;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Map;

@Controller("/api/v1/printers")
public class PrintController {

    @Get("/paired")
    public String listPaired() {
        JSONArray printers = PrinterService.listPairedPrinters();
        return printers.toString();
    }

    @Get("/saved")
    public String getSaved() {
        JSONObject saved = PrinterService.getSavedPrinter();
        return saved != null ? saved.toString() : "{}";
    }

    @Post("/save")
    public String save(String body) {
        try {
            JSONObject config = new JSONObject(body);
            PrinterService.savePrinter(config);
            return JsonResponse.success("Printer saved");
        } catch (Exception e) {
            return JsonResponse.error("Error saving printer: " + e.getMessage());
        }
    }

    @Post("/test")
    public String test() {
        JSONObject config = PrinterService.getSavedPrinter();
        if (config == null) {
            return JsonResponse.error("No printer configured");
        }
        int width = config.optInt("paperWidth", 48);
        byte[] testTicket = PrintFormatter.generateTestTicket(width);
        boolean success = PrinterService.sendBytes(testTicket);
        return success ? JsonResponse.success("Test printed") : JsonResponse.error("Printing failed");
    }

    @Post("/print")
    public String printOrder(String body) {
        // Here we would implement order printing
        // For now, it's a placeholder as requested by the flow
        return JsonResponse.success("Printing order...");
    }
}
