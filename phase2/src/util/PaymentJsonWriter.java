package util;

import core.Table;
import org.json.simple.JSONObject;
import visual.Login;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class PaymentJsonWriter {
    public static void PaymentWriter(Table table){
        Date dNow = new Date();
        String server =table.getServer().toString();
        int tableNumber = table.getTableNumber();
        double tablePayment = table.getTablePayment();

        JSONObject obj = new JSONObject();
        obj.put("date", dNow);
        obj.put("server", server);
        obj.put("tableNumber", tableNumber);
        obj.put("payment", tablePayment);


        try(FileWriter file = new FileWriter("/data/payments.json")){
            file.write(obj.toJSONString());//TODO: append

        } catch (IOException e){
            Login.logger.warning("Unable to update Restaurant payment.json.");
        }
    }

}
