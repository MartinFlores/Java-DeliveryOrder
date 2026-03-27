package com.martin.appserver.routing;

import com.martin.appserver.controllers.AppsController;
import com.martin.appserver.controllers.CompanyConfigController;
import com.martin.appserver.controllers.ConfigController;
import com.martin.appserver.controllers.ConfigStatusController;
import com.martin.appserver.controllers.LogsController;
import com.martin.appserver.controllers.NetworkController;
import com.martin.appserver.controllers.OrdersController;
import com.martin.appserver.controllers.PrintController;
import com.martin.appserver.controllers.ProductsController;
import com.martin.appserver.controllers.ShiftsController;
import com.martin.appserver.controllers.UsersController;

public class RouteRegistry {
   public RouteRegistry() {
   }

   public static void register() {
      System.out.println("Iniciando registro automático...");
      RouteScanner.scan(new ConfigController());
      RouteScanner.scan(new ConfigStatusController());
      RouteScanner.scan(new NetworkController());
      RouteScanner.scan(new UsersController());
      RouteScanner.scan(new CompanyConfigController());
      RouteScanner.scan(new LogsController());
      RouteScanner.scan(new AppsController());
      RouteScanner.scan(new ShiftsController());
      RouteScanner.scan(new ProductsController());
      RouteScanner.scan(new OrdersController());
      RouteScanner.scan(new PrintController());
      System.out.println("Total de rutas: " + Router.count());
   }
}
